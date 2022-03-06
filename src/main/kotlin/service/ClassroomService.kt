package service

import entity.PagedResult
import model.Buildings
import model.Classroom
import model.Classrooms
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery
import util.Pagination

class ClassroomService {

    suspend fun getAllClassrooms(page: Int, limit: Int): PagedResult<Classroom> = dbQuery {
        val sqlQuery = Buildings.innerJoin(Classrooms, { id }, { buildingId })
        val totalCount = sqlQuery
            .selectAll()
            .count()
        val data = sqlQuery.selectAll()
            .limit(limit, offset = Pagination.getOffset(page, limit).toLong())
            .map { Classroom.fromRow(it) }
        return@dbQuery PagedResult(
            data = data,
            page = page,
            pageSize = limit,
            pageCount = Pagination.getPageCount(totalCount, limit)
        )
    }

    suspend fun searchClassrooms(query: String, page: Int, limit: Int): PagedResult<Classroom> = dbQuery {
        val sqlQuery = Buildings
            .innerJoin(Classrooms, { id }, { buildingId })
            .select {
                (Classrooms.title.lowerCase() like "%$query%")
            }
        val totalCount = sqlQuery.count()
        val data = sqlQuery
            .limit(limit, offset = Pagination.getOffset(page, limit).toLong())
            .map { Classroom.fromRow(it) }
        return@dbQuery PagedResult(
            data = data,
            page = page,
            pageSize = limit,
            pageCount = Pagination.getPageCount(totalCount, limit)
        )
    }

    suspend fun getClassroom(id: Int): Classroom? = dbQuery {
        Buildings
            .innerJoin(Classrooms, { Buildings.id }, { buildingId })
            .select { (Classrooms.id eq id) }
            .mapNotNull { Classroom.fromRow(it) }
            .singleOrNull()
    }

    suspend fun getClassroom(buildingId: Int, title: String): Classroom? = dbQuery {
        Buildings
            .innerJoin(Classrooms, { id }, { Classrooms.buildingId })
            .select { (Classrooms.buildingId eq buildingId) and (Classrooms.title eq title) }
            .mapNotNull { Classroom.fromRow(it) }
            .singleOrNull()
    }
}
