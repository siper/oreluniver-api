package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Teachers : Table() {
    val id = integer("id")
    val name = varchar("name", 255)
    val surname = varchar("surname", 255)
    val patronymic = varchar("patronymic", 255)
    val photo = varchar("photo", 255).nullable()
    val chairId = integer("chair_id")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Teacher(
    val id: Int,
    val name: String,
    val surname: String,
    val patronymic: String,
    val photo: String?,
    val chair: Chair,
) {
    companion object {
        fun fromRow(row: ResultRow): Teacher {
            return return Teacher(
                id = row[Teachers.id],
                name = row[Teachers.name],
                surname = row[Teachers.surname],
                patronymic = row[Teachers.patronymic],
                photo = row[Teachers.photo],
                chair = Chair.fromRow(row),
            )
        }
    }
}
