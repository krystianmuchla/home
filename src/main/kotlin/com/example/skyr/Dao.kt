package com.example.skyr

import com.example.skyr.pagination.PaginatedResult
import com.example.skyr.pagination.Pagination
import com.example.skyr.pagination.PaginationResult

abstract class Dao {

    protected fun resolveResult(updatedRows: Int): Boolean {
        return updatedRows > 0
    }

    protected fun <T> resolveSingleResult(result: List<T>): T? {
        if (result.isEmpty()) return null
        if (result.size == 1) return result[0]
        throw IllegalStateException("Could not resolve single result")
    }

    protected fun <T> resolvePaginatedResult(pagination: Pagination, result: MutableList<T>): PaginatedResult<T> {
        val fetchedElements = result.size
        val next = fetchedElements > pagination.pageSize
        if (next) {
            result.removeLast()
        }
        val paginationResult = PaginationResult(next, pagination.pageNumber > 1)
        return PaginatedResult(result, paginationResult)
    }

    protected fun limit(pageSize: Int): Int {
        return pageSize + 1
    }

    protected fun offset(pageNumber: Int, pageSize: Int): Int {
        return (pageNumber - 1) * pageSize
    }
}
