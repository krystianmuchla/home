package com.example.skyr.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping(value = ["/api/health"], produces = ["application/json"])
    fun getHealth(): Any {
        return object {}
    }
}
