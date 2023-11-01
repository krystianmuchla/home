package com.example.skyr

import org.testcontainers.containers.MySQLContainer

class DatabaseContainer : MySQLContainer<DatabaseContainer>("mysql:8.1.0") {

    override fun start() {
        super.start()
        System.setProperty("SKYR_DATABASE_URL", jdbcUrl)
        System.setProperty("SKYR_DATABASE_USER", username)
        System.setProperty("SKYR_DATABASE_PASSWORD", password)
    }
}
