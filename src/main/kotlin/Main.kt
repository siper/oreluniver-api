import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import model.EntityNotFoundException
import entity.ApiError
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import routes.v1.classroom
import service.DatabaseFactory
import service.GroupService
import routes.v1.group
import routes.v1.teacher
import service.ClassroomService
import service.TeacherService
import java.lang.IllegalArgumentException

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
            }
        )
    }

    val db = DatabaseFactory.create()
    Database.connect(db)

    val groupService = GroupService()
    val teacherService = TeacherService()
    val classroomService = ClassroomService()

    install(Routing) {
        route("/api") {
            route("/v1") {
                group(groupService)
                teacher(teacherService)
                classroom(classroomService)
            }
        }
    }

}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}