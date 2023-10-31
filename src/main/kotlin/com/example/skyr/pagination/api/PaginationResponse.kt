package com.example.skyr.pagination.api

import com.example.skyr.pagination.PaginationResult

data class PaginationResponse(val next: Boolean, val previous: Boolean)

fun paginationResponse(paginationResult: PaginationResult) = PaginationResponse(paginationResult.next, paginationResult.previous)
