package com.example.skyr.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController : ApiController {

    @GetMapping(value = ["/health"], produces = ["application/json"])
    fun getHealth(): Any {
        return object {}
    }
}
