package com.example.skyr.pagination.api

import com.example.skyr.pagination.PaginatedResult

data class PaginatedResponse<T>(val data: List<T>, val pagination: PaginationResponse)

fun <T, U> paginatedResponse(paginatedResult: PaginatedResult<U>, transformer: (U) -> T) =
    PaginatedResponse(
        paginatedResult.data.stream().map { element -> transformer(element) }.toList(),
        paginationResponse(paginatedResult.pagination)
    )
