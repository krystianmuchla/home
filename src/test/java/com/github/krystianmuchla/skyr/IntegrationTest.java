package com.github.krystianmuchla.skyr;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class IntegrationTest {
    @Container
    private static final DatabaseContainer databaseContainer = new DatabaseContainer();
}
