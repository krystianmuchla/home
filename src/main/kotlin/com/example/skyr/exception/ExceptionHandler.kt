package com.example.skyr.exception

import com.example.skyr.exception.api.ProblemResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun handle(exception: ClientErrorException): ResponseEntity<ProblemResponse> {
        return ResponseEntity(ProblemResponse(exception.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun handle(exception: NotFoundException): ResponseEntity<ProblemResponse> {
        return ResponseEntity(ProblemResponse(exception.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handle(exception: ServiceErrorException): ResponseEntity<ProblemResponse> {
        return ResponseEntity(ProblemResponse(exception.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler
    fun handle(exception: Exception): ResponseEntity<ProblemResponse> {
        return ResponseEntity(ProblemResponse("Unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
