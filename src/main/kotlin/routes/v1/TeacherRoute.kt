package routes.v1

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import model.EntityNotFoundException
import service.GroupService
import service.TeacherService
import util.Pagination
import java.lang.IllegalArgumentException

fun Route.teacher(teacherService: TeacherService) {

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
    }
}
