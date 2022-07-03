package routes.v1

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.EntityNotFoundException
import service.ScheduleService
import service.TeacherService
import util.Pagination

fun Route.teacher(
    teacherService: TeacherService,
    scheduleService: ScheduleService
) {

    route("/teacher") {

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный id")
            val group = teacherService.getTeacher(id) ?: throw EntityNotFoundException()
            call.respond(HttpStatusCode.OK, group)
        }

        get("/search") {
            val q = call.parameters["q"]
            val page = call.parameters["page"]?.toInt() ?: 1
            val pageSize = call.parameters["page_size"]?.toInt() ?: 10

            Pagination.checkPage(page)
            Pagination.checkPageSize(pageSize)

            val teachers = if (q.isNullOrEmpty()) {
                teacherService.getAllTeachers(page, pageSize)
            } else {
                teacherService.searchTeachers(q.lowercase(), page, pageSize)
            }
            Pagination.createResponseHeaders(teachers).forEach {
                call.response.headers.append(it.name, it.value)
            }
            call.respond(HttpStatusCode.OK, teachers.data)
        }

        route("schedule") {

            get("/{teacher_id}") {
                val id = call.parameters["teacher_id"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный id")
                val year = call.parameters["year"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный year")
                val week = call.parameters["week"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный week")

                call.respond(HttpStatusCode.OK, scheduleService.getTeacherSchedule(id, year, week))
            }
        }
    }
}
