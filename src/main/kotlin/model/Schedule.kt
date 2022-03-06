package model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val title: String,
    val special: String,
    val lessonType: String,
    val subgroupNumber: Int,
    val startDate: String,
    val endDate: String,
    val link: String?,
    val password: String?,
    val groups: List<Group>,
    val classroom: Classroom,
    val teacher: Teacher
)