package util

import entity.PagedResult
import entity.PaginationHeader
import java.lang.IllegalArgumentException

object Pagination {
    fun getOffset(page: Int, limit: Int): Int {
        return if (page == 1) {
            0
        } else {
            page * limit
        }
    }

    fun getPageCount(totalItems: Long, limit: Int): Int {
        if (totalItems in 1 until limit) {
            return 1
        }
        return (totalItems / limit).toInt()
    }

    fun createResponseHeaders(result: PagedResult<*>): List<PaginationHeader> {
        return listOf(
            PaginationHeader("page", result.page.toString()),
            PaginationHeader("pageSize", result.pageSize.toString()),
            PaginationHeader("pageCount", result.pageCount.toString()),
        )
    }

    fun checkPage(page: Int) {
        if (page < 1) {
            throw IllegalArgumentException("page должна быть больше или равна 1")
        }
    }

    fun checkPageSize(pageSize: Int) {
        if (pageSize < 1) {
            throw IllegalArgumentException("pageSize не может быть меньше 1")
        }
        if (pageSize > 30) {
            throw IllegalArgumentException("pageSize не может быть больше 30")
        }
    }
}