package entity

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(val error: String)