package com.github.krystianmuchla.skyr

import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class IntegrationTest {

    companion object {

        @Container
        private val databaseContainer = DatabaseContainer()
    }
}
