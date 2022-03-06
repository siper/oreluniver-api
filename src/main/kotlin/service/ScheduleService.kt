package service

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import model.*
import util.*
import java.time.format.DateTimeFormatter

class ScheduleService(
    private val client: HttpClient,
    private val groupService: GroupService,
    private val classroomService: ClassroomService,
    private val teacherService: TeacherService,
) {
    private val groupCache = TimedCache<Int, Group>(12 * 60 * 60 * 1000)
    private val teacherCache = TimedCache<Int, Teacher>(12 * 60 * 60 * 1000)
    private val classroomCache = TimedCache<ClassroomId, Classroom>(12 * 60 * 60 * 1000)

    suspend fun getGroupSchedule(id: Int, year: Int, week: Int): List<Schedule> = withContext(Dispatchers.IO) {
        val timestamp = getScheduleTime(year, week)
        val schedule = client.get<String>("http://oreluniver.ru/schedule//$id///$timestamp/printschedule")
        return@withContext formatSchedule(schedule, year, week)
    }

    suspend fun getTeacherSchedule(id: Int, year: Int, week: Int): List<Schedule> = withContext(Dispatchers.IO) {
        val timestamp = getScheduleTime(year, week)
        val schedule = client.get<String>("http://oreluniver.ru/schedule/$id////$timestamp/printschedule")
        return@withContext formatSchedule(schedule, year, week)
    }

    suspend fun getClassroomSchedule(id: Int, year: Int, week: Int): List<Schedule> = withContext(Dispatchers.IO) {
        val timestamp = getScheduleTime(year, week)
        val classroom =
            classroomService.getClassroom(id) ?: throw NullPointerException("Classroom with id $id not exists in db")
        val schedule = client.get<String>(
            "http://oreluniver.ru/schedule///${classroom.building.id}/${classroom.title}/$timestamp/printschedule"
        )
        return@withContext formatSchedule(schedule, year, week)
    }

    private suspend fun formatSchedule(
        raw: String,
        year: Int,
        week: Int
    ): List<Schedule> = withContext(Dispatchers.IO) {
        val json = Json.parseToJsonElement(raw)
        return@withContext if (json is JsonObject) {
            val items = mutableListOf<Schedule>()
            for (i in 0..1000) {
                val jsonObject = json.jsonObject["$i"] ?: break
                items.add(getScheduleItem(jsonObject.toString(), year, week))
            }
            return@withContext items
        } else {
            json.jsonArray.map {
                getScheduleItem(it.jsonObject.toString(), year, week)
            }
        }
    }

    private suspend fun getGroup(id: Int): Group {
        return groupCache[id] ?: groupService.getGroup(id)?.also { group ->
            groupCache[id] = group
        } ?: throw NullPointerException("Group with id $id not exists in db")
    }

    private suspend fun getTeacher(id: Int): Teacher {
        return teacherCache[id] ?: teacherService.getTeacher(id)?.also { teacher ->
            teacherCache[id] = teacher
        } ?: throw NullPointerException("Teacher with id $id not exists in db")
    }

    private suspend fun getClassroom(id: ClassroomId): Classroom {
        return classroomCache[id] ?: classroomService.getClassroom(id.buildingId, id.classroomTitle)
            ?.also { classroom ->
                classroomCache[id] = classroom
            }
        ?: throw NullPointerException("Classroom with building_id ${id.buildingId} and title ${id.classroomTitle} not exists in db")
    }

    private suspend fun getScheduleItem(raw: String, year: Int, week: Int): Schedule {
        val legacySchedule = decodeSchedule(raw)

        val groups = legacySchedule.idGruops.map { id ->
            getGroup(id)
        }

        val teacher = getTeacher(legacySchedule.employeeId)

        val classroomId = ClassroomId(legacySchedule.korpus, legacySchedule.numberRoom)
        val classroom = getClassroom(classroomId)

        val startDate = legacySchedule.getStartDate(year, week).run {
            format(DateTimeFormatter.ISO_DATE_TIME)
        }
        val endDate = legacySchedule.getEndDate(year, week).run {
            format(DateTimeFormatter.ISO_DATE_TIME)
        }

        return Schedule(
            title = legacySchedule.titleSubject.trim(),
            special = legacySchedule.special.trim(),
            lessonType = legacySchedule.typeLesson,
            subgroupNumber = legacySchedule.numberSubGruop,
            startDate = startDate,
            endDate = endDate,
            link = legacySchedule.zoomLink ?: legacySchedule.link,
            password = legacySchedule.zoomPassword ?: legacySchedule.pass,
            groups = groups,
            classroom = classroom,
            teacher = teacher
        )
    }

    private fun decodeSchedule(raw: String): LegacySchedule {
        val jsonObject = Json.parseToJsonElement(raw).jsonObject

        val idGroupJson = jsonObject["idGruop"]?.jsonPrimitive
        val idGroupIds = idGroupJson?.parseGroupIds() ?: error("Empty idGroup list $idGroupJson")

        return LegacySchedule(
            idGruops = idGroupIds,
            numberSubGruop = jsonObject.require("NumberSubGruop"),
            titleSubject = jsonObject.require("TitleSubject"),
            typeLesson = jsonObject.require("TypeLesson"),
            kurs = jsonObject.require("kurs"),
            numberLesson = jsonObject.require("NumberLesson"),
            dayWeek = jsonObject.require("DayWeek"),
            dateLesson = jsonObject.require("DateLesson"),
            korpus = jsonObject.require("Korpus"),
            numberRoom = jsonObject.require("NumberRoom"),
            special = jsonObject.require("special"),
            title = jsonObject.require("title"),
            employeeId = jsonObject.require("employee_id"),
            family = jsonObject.require("Family"),
            name = jsonObject.require("Name"),
            secondName = jsonObject.require("SecondName"),
            link = jsonObject
                .require<String>("link")
                .trim()
                .takeIf { it.isNotEmpty() },
            pass = jsonObject
                .require<String>("pass")
                .trim()
                .takeIf { it.isNotEmpty() },
            zoomLink = jsonObject
                .require<String>("zoom_link")
                .trim()
                .takeIf { it.isNotEmpty() },
            zoomPassword = jsonObject
                .require<String>("zoom_password")
                .trim()
                .takeIf { it.isNotEmpty() }
        )
    }

    private fun JsonPrimitive.parseGroupIds(): List<Int> {
        return intOrNull?.let { listOf(it) } ?: content
            .split(", ")
            .map { it.toInt() }
    }

    data class ClassroomId(val buildingId: Int, val classroomTitle: String)
}