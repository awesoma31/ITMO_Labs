package com.cryptoterm.backend.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для валидатора сильных паролей
 */
class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
    }

    @Test
    void strongPassword_shouldBeValid() {
        String password = "Test123!@#";
        assertTrue(validator.isValid(password, null), 
                "Пароль с заглавной, строчной, цифрой и спецсимволом должен быть валидным");
    }

    @Test
    void shortPassword_shouldBeInvalid() {
        String password = "Test1!";
        assertFalse(validator.isValid(password, null), 
                "Пароль короче 8 символов должен быть невалидным");
    }

    @Test
    void passwordWithoutUppercase_shouldBeInvalid() {
        String password = "test123!@#";
        assertFalse(validator.isValid(password, null), 
                "Пароль без заглавной буквы должен быть невалидным");
    }

    @Test
    void passwordWithoutLowercase_shouldBeInvalid() {
        String password = "TEST123!@#";
        assertFalse(validator.isValid(password, null), 
                "Пароль без строчной буквы должен быть невалидным");
    }

    @Test
    void passwordWithoutDigit_shouldBeInvalid() {
        String password = "TestTest!@#";
        assertFalse(validator.isValid(password, null), 
                "Пароль без цифры должен быть невалидным");
    }

    @Test
    void passwordWithoutSpecialChar_shouldBeInvalid() {
        String password = "TestTest123";
        assertFalse(validator.isValid(password, null), 
                "Пароль без спецсимвола должен быть невалидным");
    }

    @Test
    void nullPassword_shouldBeInvalid() {
        assertFalse(validator.isValid(null, null), 
                "Null пароль должен быть невалидным");
    }

    @Test
    void emptyPassword_shouldBeInvalid() {
        assertFalse(validator.isValid("", null), 
                "Пустой пароль должен быть невалидным");
    }

    @Test
    void complexPassword_shouldBeValid() {
        String password = "MySecure!Pass123";
        assertTrue(validator.isValid(password, null), 
                "Сложный пароль должен быть валидным");
    }

    @Test
    void passwordWithMultipleSpecialChars_shouldBeValid() {
        String password = "Test123!@#$%";
        assertTrue(validator.isValid(password, null), 
                "Пароль с несколькими спецсимволами должен быть валидным");
    }
}
