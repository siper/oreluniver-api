package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Classrooms : Table() {
    val id = integer("id")
    val title = varchar("title", 255)
    val buildingId = integer("building_id")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Classroom(
    val id: Int,
    val title: String,
    val building: Building,
) {
    companion object {
        fun fromRow(row: ResultRow): Classroom {
            return Classroom(
                id = row[Classrooms.id],
                title = row[Classrooms.title],
                building = Building.fromRow(row)
            )
        }
    }
}
