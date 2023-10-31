package com.example.skyr.pagination.api

import com.example.skyr.pagination.PaginatedResult

data class PaginatedResponse<T>(val data: List<T>, val pagination: PaginationResponse)

fun <T> paginationResponse(paginatedResult: PaginatedResult<T>) =
    PaginatedResponse(paginatedResult.data, paginationResponse(paginatedResult.pagination))
