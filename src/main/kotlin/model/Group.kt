package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Groups : Table() {
    val id = integer("id")
    val title = varchar("title", 255)
    val course = integer("course")
    val code = varchar("code", 255)
    val educationLevel = varchar("education_level", 255)
    val instituteId = integer("institute_id")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Group(
    val id: Int,
    val title: String,
    val course: Int,
    val code: String,
    val educationLevel: String,
    val institute: Institute,
) {
    companion object {
        fun fromRow(row: ResultRow): Group {
            return Group(
                id = row[Groups.id],
                title = row[Groups.title],
                course = row[Groups.course],
                code = row[Groups.code],
                educationLevel = row[Groups.educationLevel],
                institute = Institute.fromRow(row),
            )
        }
    }
}
