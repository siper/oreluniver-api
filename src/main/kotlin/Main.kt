import entity.ApiError
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import model.EntityNotFoundException
import org.jetbrains.exposed.sql.Database
import routes.v1.classroom
import routes.v1.group
import routes.v1.teacher
import service.*

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiError(cause.message ?: "Неизвестная ошибка параметров"))
        }
        exception<EntityNotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiError(cause.message.toString()))
        }
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }
        )
    }

    val db = DatabaseFactory.create()
    Database.connect(db)

    val groupService = GroupService()
    val teacherService = TeacherService()
    val classroomService = ClassroomService()
    val ddosProtectionHackService = DdosProtectionHackService()
    val client = HttpClient(CIO)
    setupClient(client, ddosProtectionHackService)
    val scheduleService = ScheduleService(client, groupService, classroomService, teacherService)

    install(Routing) {
        route("/api") {
            route("/v1") {
                group(groupService, scheduleService)
                teacher(teacherService, scheduleService)
                classroom(classroomService, scheduleService)
            }
        }
    }

}

private fun setupClient(client: HttpClient, ddosProtectionHackService: DdosProtectionHackService) {
    client.plugin(HttpSend).intercept { request ->
        return@intercept execute(
            request.apply {
                header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.136 Safari/537.36"
                )
            }
        )
    }
    client.plugin(HttpSend).intercept { request ->
        return@intercept execute(ddosProtectionHackService.handleDdosProtection(request, this))
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}