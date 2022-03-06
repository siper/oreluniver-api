package model

data class LegacySchedule(
    val idGruops: List<Int>,
    val numberSubGruop: Int,
    val titleSubject: String,
    val typeLesson: String,
    val kurs: Int,
    val numberLesson: Int,
    val dayWeek: Int,
    val dateLesson: String,
    val korpus: Int,
    val numberRoom: String,
    val special: String,
    val title: String,
    val employeeId: Int,
    val family: String,
    val name: String,
    val secondName: String,
    val link: String?,
    val pass: String?,
    val zoomLink: String?,
    val zoomPassword: String?
)