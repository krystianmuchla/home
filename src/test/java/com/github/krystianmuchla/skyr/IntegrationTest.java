package com.github.krystianmuchla.skyr;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class IntegrationTest {
    @Container
    private static final DatabaseContainer databaseContainer = new DatabaseContainer();
}
