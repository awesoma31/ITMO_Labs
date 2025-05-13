package org.awesoma.back;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Самый простой тест‑заглушка:
 * проверяем, что JVM умеет выполнять условия :)
 */
class SanityTest {

    @Test
    void contextLoads() {
        // Всегда‑успешная проверка
        assertTrue(1 + 1 == 2, "Математика сломалась!");
    }
}
