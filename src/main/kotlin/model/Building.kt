package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Buildings : Table() {
    val id = integer("id")
    val title = varchar("title", 255)
    val address = varchar("address", 255)
    val latitude = float("latitude")
    val longitude = float("longitude")
    val img = varchar("img", 255)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Building(
    val id: Int,
    val title: String,
    val address: String,
    val latitude: Float,
    val longitude: Float,
    val img: String
) {
    companion object {
        fun fromRow(row: ResultRow): Building {
            return Building(
                id = row[Buildings.id],
                title = row[Buildings.title],
                address = row[Buildings.address],
                latitude = row[Buildings.latitude],
                longitude = row[Buildings.longitude],
                img = row[Buildings.img]
            )
        }
    }
}
