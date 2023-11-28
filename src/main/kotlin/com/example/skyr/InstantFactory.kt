package com.example.skyr

import java.time.Instant
import java.time.temporal.ChronoUnit

class InstantFactory private constructor() {

    companion object {
        fun create(): Instant {
            return Instant.now().truncatedTo(ChronoUnit.MILLIS)
        }
    }
}
