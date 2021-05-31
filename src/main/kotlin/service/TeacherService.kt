package service

import entity.PagedResult
import model.*
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery
import util.Pagination

class TeacherService {

    suspend fun getAllTeachers(page: Int, limit: Int): PagedResult<Teacher> = dbQuery {
        val sqlQuery = Institutes.innerJoin(Chairs, { id }, { instituteId })
            .innerJoin(Teachers, { Chairs.id }, { Teachers.chairId })
            .selectAll()
        val totalCount = sqlQuery.count()
        val data = sqlQuery
            .limit(limit, offset = Pagination.getOffset(page, limit).toLong())
            .map { Teacher.fromRow(it) }
        return@dbQuery PagedResult(
            data = data,
            page = page,
            pageSize = limit,
            pageCount = Pagination.getPageCount(totalCount, limit)
        )
    }

    suspend fun searchTeachers(query: String, page: Int, limit: Int): PagedResult<Teacher> = dbQuery {
        val sqlQuery = Institutes.innerJoin(Chairs, { id }, { instituteId })
            .innerJoin(Teachers, { Chairs.id }, { Teachers.chairId })
            .select {
                (Teachers.name.lowerCase() like "%$query%") or
                        (Teachers.surname.lowerCase() like "%$query%") or
                        (Teachers.patronymic.lowerCase() like "%$query%")
            }
        val totalCount = sqlQuery.count()
        val data = sqlQuery
            .limit(limit, offset = Pagination.getOffset(page, limit).toLong())
            .map { Teacher.fromRow(it) }
        return@dbQuery PagedResult(
            data = data,
            page = page,
            pageSize = limit,
            pageCount = Pagination.getPageCount(totalCount, limit)
        )
    }

    suspend fun getTeacher(id: Int): Teacher? = dbQuery {
        Institutes.innerJoin(Chairs, { Institutes.id }, { instituteId })
            .innerJoin(Teachers, { Chairs.id }, { Teachers.chairId })
            .select { (Teachers.id eq id) }
            .mapNotNull { Teacher.fromRow(it) }
            .singleOrNull()
    }
}
