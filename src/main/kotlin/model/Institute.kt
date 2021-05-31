package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Institutes : Table() {
    val id = integer("id")
    val title = varchar("title", 255)
    val shortTitle = varchar("short_title", 255)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Institute(
    val id: Int,
    val title: String,
    val shortTitle: String
) {
    companion object {
        fun fromRow(row: ResultRow): Institute {
            return Institute(
                id = row[Institutes.id],
                title = row[Institutes.title],
                shortTitle = row[Institutes.shortTitle],
            )
        }
    }
}
