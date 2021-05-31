package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Chairs : Table() {
    val id = integer("id")
    val title = varchar("title", 255)
    val shortTitle = varchar("short_title", 255)
    val instituteId = integer("institute_id")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Chair(
    val id: Int,
    val title: String,
    val shortTitle: String,
    val institute: Institute
) {
    companion object {
        fun fromRow(row: ResultRow): Chair {
            return Chair(
                id = row[Chairs.id],
                title = row[Chairs.title],
                shortTitle = row[Chairs.shortTitle],
                institute = Institute.fromRow(row)
            )
        }
    }
}
