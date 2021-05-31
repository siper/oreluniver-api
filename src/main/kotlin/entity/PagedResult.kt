package entity

data class PagedResult<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val pageCount: Int
)
