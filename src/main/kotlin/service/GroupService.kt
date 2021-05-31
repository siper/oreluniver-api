package service

import entity.PagedResult
import model.*
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery
import util.Pagination

class GroupService {

    suspend fun getAllGroups(page: Int, limit: Int): PagedResult<Group> = dbQuery {
        val sqlQuery = Institutes.innerJoin(Groups, { Institutes.id }, { Groups.instituteId }).selectAll()
        val totalCount = sqlQuery.count()
        val data = sqlQuery
            .limit(limit, offset = Pagination.getOffset(page, limit).toLong())
            .map { Group.fromRow(it) }
        return@dbQuery PagedResult(
            data = data,
            page = page,
            pageSize = limit,
            pageCount = Pagination.getPageCount(totalCount, limit)
        )
    }

    suspend fun searchGroups(query: String, page: Int, limit: Int): PagedResult<Group> = dbQuery {
        val sqlQuery = Institutes.innerJoin(Groups, { Institutes.id }, { Groups.instituteId })
            .select {
                (Groups.title.lowerCase() like "%$query%") or (Groups.code.lowerCase() like "%$query%")
            }
        val totalCount = sqlQuery.count()
        val data = sqlQuery
            .limit(limit, offset = Pagination.getOffset(page, limit).toLong())
            .map { Group.fromRow(it) }
        return@dbQuery PagedResult(
            data = data,
            page = page,
            pageSize = limit,
            pageCount = Pagination.getPageCount(totalCount, limit)
        )
    }

    suspend fun getGroup(id: Int): Group? = dbQuery {
        Institutes.innerJoin(Groups, { Institutes.id }, { instituteId })
            .select { (Groups.id eq id) }
            .mapNotNull { Group.fromRow(it) }
            .singleOrNull()
    }
}
