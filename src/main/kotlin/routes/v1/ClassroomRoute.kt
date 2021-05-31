package routes.v1

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import model.EntityNotFoundException
import service.ClassroomService
import service.ScheduleService
import util.Pagination

fun Route.classroom(
    classroomService: ClassroomService,
    scheduleService: ScheduleService
) {

    route("/classroom") {

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный id")
            val classroom = classroomService.getClassroom(id) ?: throw EntityNotFoundException()
            call.respond(HttpStatusCode.OK, classroom)
        }

        get("/search") {
            val q = call.parameters["q"]
            val page = call.parameters["page"]?.toInt() ?: 1
            val pageSize = call.parameters["page_size"]?.toInt() ?: 10

            Pagination.checkPage(page)
            Pagination.checkPageSize(pageSize)

            val classrooms = if (q.isNullOrEmpty()) {
                classroomService.getAllClassrooms(page, pageSize)
            } else {
                classroomService.searchClassrooms(q.lowercase(), page, pageSize)
            }
            Pagination.createResponseHeaders(classrooms).forEach {
                call.response.headers.append(it.name, it.value)
            }
            call.respond(HttpStatusCode.OK, classrooms.data)
        }

        route("schedule") {

            get("/{classroom_id}") {
                val id =
                    call.parameters["classroom_id"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный id")
                val year = call.parameters["year"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный year")
                val week = call.parameters["week"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный week")

                call.respond(HttpStatusCode.OK, scheduleService.getClassroomSchedule(id, year, week))
            }
        }
    }
}
