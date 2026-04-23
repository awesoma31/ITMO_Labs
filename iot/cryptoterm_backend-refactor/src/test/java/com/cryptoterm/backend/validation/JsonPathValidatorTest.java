package com.cryptoterm.backend.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonPathValidator.
 */
class JsonPathValidatorTest {

    private JsonPathValidator validator;

    @BeforeEach
    void setUp() {
        validator = new JsonPathValidator();
    }

    @Test
    void testValidSimpleFieldAccess() {
        assertTrue(validator.isValid("$.token"));
        assertTrue(validator.isValid("$.user"));
        assertTrue(validator.isValid("$.response"));
    }

    @Test
    void testValidNestedFieldAccess() {
        assertTrue(validator.isValid("$.user.name"));
        assertTrue(validator.isValid("$.response.data.token"));
        assertTrue(validator.isValid("$.a.b.c.d.e"));
    }

    @Test
    void testValidArrayIndexing() {
        assertTrue(validator.isValid("$.items[0]"));
        assertTrue(validator.isValid("$.users[5]"));
        assertTrue(validator.isValid("$.data[123]"));
    }

    @Test
    void testValidArrayWildcard() {
        assertTrue(validator.isValid("$.items[*]"));
        assertTrue(validator.isValid("$.users[*]"));
    }

    @Test
    void testValidBracketNotation() {
        assertTrue(validator.isValid("$['field-name']"));
        assertTrue(validator.isValid("$['field_with_underscore']"));
        assertTrue(validator.isValid("$[\"field-name\"]"));
    }

    @Test
    void testValidComplexPaths() {
        assertTrue(validator.isValid("$.response.data.users[0].profile.name"));
        assertTrue(validator.isValid("$.items[0].nested.field"));
        assertTrue(validator.isValid("$.a.b[0].c[1].d"));
    }

    @Test
    void testInvalidPaths() {
        // Missing $
        assertFalse(validator.isValid("token"));
        assertFalse(validator.isValid(".token"));
        
        // Double dots (not in bracket notation)
        assertFalse(validator.isValid("$..token"));
        
        // Invalid characters
        assertFalse(validator.isValid("$.token!"));
        assertFalse(validator.isValid("$.token@field"));
        
        // Unbalanced brackets
        assertFalse(validator.isValid("$.items[0"));
        assertFalse(validator.isValid("$.items0]"));
        
        // Empty or null
        assertFalse(validator.isValid(""));
        assertFalse(validator.isValid(null));
    }

    @Test
    void testValidateOrThrow_Valid() {
        // Should not throw
        assertDoesNotThrow(() -> 
            validator.validateOrThrow("$.token", "testField")
        );
    }

    @Test
    void testValidateOrThrow_Invalid() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateOrThrow("invalid", "testField")
        );
        
        assertTrue(exception.getMessage().contains("testField"));
        assertTrue(exception.getMessage().contains("invalid"));
    }

    @Test
    void testGetSupportedSyntax() {
        String syntax = validator.getSupportedSyntax();
        assertNotNull(syntax);
        assertTrue(syntax.contains("$.field"));
        assertTrue(syntax.contains("$.array[0]"));
        assertTrue(syntax.contains("$.array[*]"));
    }
}
