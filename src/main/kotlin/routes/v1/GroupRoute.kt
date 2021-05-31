package routes.v1

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import model.EntityNotFoundException
import service.GroupService
import service.ScheduleService
import util.Pagination

fun Route.group(
    groupService: GroupService,
    scheduleService: ScheduleService
) {

    route("/group") {

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный id")
            val group = groupService.getGroup(id) ?: throw EntityNotFoundException()
            call.respond(HttpStatusCode.OK, group)
        }

        get("/search") {
            val q = call.parameters["q"]
            val page = call.parameters["page"]?.toInt() ?: 1
            val pageSize = call.parameters["page_size"]?.toInt() ?: 10

            Pagination.checkPage(page)
            Pagination.checkPageSize(pageSize)

            val groups = if (q.isNullOrEmpty()) {
                groupService.getAllGroups(page, pageSize)
            } else {
                groupService.searchGroups(q.lowercase(), page, pageSize)
            }
            Pagination.createResponseHeaders(groups).forEach {
                call.response.headers.append(it.name, it.value)
            }
            call.respond(HttpStatusCode.OK, groups.data)
        }

        route("schedule") {

            get("/{group_id}") {
                val id = call.parameters["group_id"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный id")
                val year = call.parameters["year"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный year")
                val week = call.parameters["week"]?.toIntOrNull() ?: throw IllegalArgumentException("Не верный week")

                call.respond(HttpStatusCode.OK, scheduleService.getGroupSchedule(id, year, week))
            }
        }
    }
}
