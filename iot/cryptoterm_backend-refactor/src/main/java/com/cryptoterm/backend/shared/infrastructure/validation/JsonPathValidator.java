package com.cryptoterm.backend.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator for JSONPath expressions used in ASIC command templates.
 * Ensures that JSONPath expressions are syntactically valid before storing them.
 */
@Component
public class JsonPathValidator {

    // Pattern for valid JSONPath expressions
    // Supports: $.field, $.field.nested, $.array[0], $.field[*], $['field-name']
    private static final Pattern JSONPATH_PATTERN = Pattern.compile(
        "^\\$" +                                    // Must start with $
        "(" +
            "(\\.\\w+)" +                          // .field
            "|" +
            "(\\[\\d+\\])" +                       // [0]
            "|" +
            "(\\[\\*\\])" +                        // [*]
            "|" +
            "(\\['[^']+'])" +                      // ['field-name']
            "|" +
            "(\\[\"[^\"]+\"])" +                   // ["field-name"]
        ")*$"
    );

    /**
     * Validates a JSONPath expression.
     * 
     * @param jsonPath The JSONPath expression to validate
     * @return true if valid, false otherwise
     */
    public boolean isValid(String jsonPath) {
        if (jsonPath == null || jsonPath.isEmpty()) {
            return false;
        }

        // Check basic pattern
        if (!JSONPATH_PATTERN.matcher(jsonPath).matches()) {
            return false;
        }

        // Additional checks
        // 1. No double dots (except in bracket notation)
        if (jsonPath.contains("..") && !jsonPath.contains("['") && !jsonPath.contains("[\"")) {
            return false;
        }

        // 2. Balanced brackets
        int openBrackets = 0;
        for (char c : jsonPath.toCharArray()) {
            if (c == '[') openBrackets++;
            if (c == ']') openBrackets--;
            if (openBrackets < 0) return false;
        }
        if (openBrackets != 0) return false;

        return true;
    }

    /**
     * Validates a JSONPath expression and throws an exception if invalid.
     * 
     * @param jsonPath The JSONPath expression to validate
     * @param fieldName The name of the field (for error messages)
     * @throws IllegalArgumentException if the JSONPath is invalid
     */
    public void validateOrThrow(String jsonPath, String fieldName) {
        if (!isValid(jsonPath)) {
            throw new IllegalArgumentException(
                String.format("Invalid JSONPath expression in field '%s': %s", fieldName, jsonPath)
            );
        }
    }

    /**
     * Get a human-readable description of supported JSONPath syntax.
     * 
     * @return Description of supported syntax
     */
    public String getSupportedSyntax() {
        return "Supported JSONPath syntax:\n" +
               "- Root: $ (entire object)\n" +
               "- Field access: $.field or $['field-name']\n" +
               "- Nested fields: $.field.nested.deep\n" +
               "- Array indexing: $.array[0]\n" +
               "- Array wildcard: $.array[*] (returns first element)\n" +
               "- Complex: $.response.data.users[0].profile.name";
    }
}
