import entity.ApiError
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
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
        exception<IllegalArgumentException> { cause ->
            call.respond(HttpStatusCode.BadRequest, ApiError(cause.message ?: "Неизвестная ошибка параметров"))
        }
        exception<EntityNotFoundException> {
            call.respond(HttpStatusCode.NotFound, ApiError(it.message.toString()))
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
    val scheduleService = ScheduleService(HttpClient(CIO), groupService, classroomService, teacherService)

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

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}