package com.example.skyr.pagination.api

import com.example.skyr.pagination.Pagination
import jakarta.validation.constraints.Size

data class PaginationRequest(
    @field:Size(min = Pagination.MIN_PAGE_NUMBER) val pageNumber: Int = Pagination.DEFAULT_PAGE_NUMBER,
    @field:Size(
        min = Pagination.MIN_PAGE_SIZE,
        max = Pagination.MAX_PAGE_SIZE
    ) val pageSize: Int = Pagination.DEFAULT_PAGE_SIZE
)
