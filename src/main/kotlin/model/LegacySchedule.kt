package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegacySchedule(
    val id_cell: Int,
    val idGruop: Int,
    @SerialName("NumberSubGruop")
    val numberSubGruop: Int,
    @SerialName("TitleSubject")
    val titleSubject: String,
    @SerialName("TypeLesson")
    val typeLesson: String,
    val kurs: Int,
    @SerialName("NumberLesson")
    val numberLesson: Int,
    @SerialName("DayWeek")
    val dayWeek: Int,
    @SerialName("DateLesson")
    val dateLesson: String,
    @SerialName("Korpus")
    val korpus: Int,
    @SerialName("NumberRoom")
    val numberRoom: String,
    val special: String,
    val title: String,
    val employee_id: Int,
    @SerialName("Family")
    val family: String,
    @SerialName("Name")
    val name: String,
    @SerialName("SecondName")
    val secondName: String,
    val link: String?,
    val pass: String?,
    val zoom_link: String?,
    val zoom_password: String?
)