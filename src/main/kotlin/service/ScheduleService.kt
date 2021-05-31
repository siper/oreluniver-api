package service

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import model.*
import util.ScheduleUtils
import util.TimedCache
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
        val timestamp = ScheduleUtils.getScheduleTime(year, week)
        val schedule = client.get<String>("http://oreluniver.ru/schedule//$id///$timestamp/printschedule")
        return@withContext formatSchedule(schedule, year, week)
    }

    suspend fun getTeacherSchedule(id: Int, year: Int, week: Int): List<Schedule> = withContext(Dispatchers.IO) {
        val timestamp = ScheduleUtils.getScheduleTime(year, week)
        val schedule = client.get<String>("http://oreluniver.ru/schedule/$id////$timestamp/printschedule")
        return@withContext formatSchedule(schedule, year, week)
    }

    suspend fun getClassroomSchedule(id: Int, year: Int, week: Int): List<Schedule> = withContext(Dispatchers.IO) {
        val timestamp = ScheduleUtils.getScheduleTime(year, week)
        val classroom =
            classroomService.getClassroom(id) ?: throw NullPointerException("Classroom with id $id not exists in db")
        val schedule =
            client.get<String>("http://oreluniver.ru/schedule///${classroom.building.id}/${classroom.title}/$timestamp/printschedule")
        return@withContext formatSchedule(schedule, year, week)
    }

    private suspend fun formatSchedule(raw: String, year: Int, week: Int): List<Schedule> =
        withContext(Dispatchers.IO) {
            val json = Json.parseToJsonElement(raw)
            return@withContext if (json is JsonObject) {
                (0..1000).mapNotNull {
                    json.jsonObject["$it"]?.run {
                        getScheduleItem(jsonObject.toString(), year, week)
                    }
                }
            } else {
                json.jsonArray.map {
                    getScheduleItem(it.jsonObject.toString(), year, week)
                }
            }
        }

    private suspend fun getScheduleItem(raw: String, year: Int, week: Int): Schedule {
        val legacySchedule = Json.decodeFromString<LegacySchedule>(raw)

        val group = groupCache[legacySchedule.idGruop] ?: groupService.getGroup(legacySchedule.idGruop)?.also { group ->
            groupCache[legacySchedule.idGruop] = group
        } ?: throw NullPointerException("Group with id ${legacySchedule.idGruop} not exists in db")

        val teacher = teacherCache[legacySchedule.employee_id] ?: teacherService.getTeacher(legacySchedule.employee_id)
            ?.also { teacher ->
                teacherCache[legacySchedule.employee_id] = teacher
            } ?: throw NullPointerException("Teacher with id ${legacySchedule.employee_id} not exists in db")

        val classroomId = ClassroomId(legacySchedule.korpus, legacySchedule.numberRoom)
        val classroom = classroomCache[classroomId] ?: classroomService.getClassroom(
            legacySchedule.korpus,
            legacySchedule.numberRoom
        )?.also { classroom ->
            classroomCache[classroomId] = classroom
        }
        ?: throw NullPointerException("Classroom with building_id ${classroomId.buildingId} and title ${classroomId.classroomTitle} not exists in db")

        val startDate =
            ScheduleUtils.getStartDate(legacySchedule.dayWeek, legacySchedule.numberLesson, year, week).run {
                this.format(DateTimeFormatter.ISO_DATE_TIME)
            }
        val endDate = ScheduleUtils.getEndDate(legacySchedule.dayWeek, legacySchedule.numberLesson, year, week).run {
            this.format(DateTimeFormatter.ISO_DATE_TIME)
        }

        return Schedule(
            title = legacySchedule.titleSubject.trim(),
            special = legacySchedule.special.trim(),
            lessonType = legacySchedule.typeLesson,
            subgroupNumber = legacySchedule.numberSubGruop,
            startDate = startDate,
            endDate = endDate,
            group = group,
            classroom = classroom,
            teacher = teacher
        )
    }

    data class ClassroomId(val buildingId: Int, val classroomTitle: String)
}