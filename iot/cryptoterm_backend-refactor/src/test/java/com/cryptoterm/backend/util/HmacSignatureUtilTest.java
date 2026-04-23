package com.cryptoterm.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HmacSignatureUtilTest {

    private HmacSignatureUtil signatureUtil;
    private ObjectMapper objectMapper;
    private String testSecret = "test-secret-key";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        signatureUtil = new HmacSignatureUtil(objectMapper);
    }

    @Test
    void testGenerateSignature() {
        // Arrange
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("deviceId", "rp-001");
        commandData.put("cmdId", "test-cmd-123");
        commandData.put("command", "asic_http_proxy");

        // Act
        String signature = signatureUtil.generateSignature(commandData, testSecret);

        // Assert
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        assertTrue(signature.matches("^[A-Za-z0-9+/=]+$")); // Base64 format
    }

    @Test
    void testSignatureConsistency() {
        // Arrange
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("deviceId", "rp-001");
        commandData.put("cmdId", "test-cmd-123");

        // Act
        String signature1 = signatureUtil.generateSignature(commandData, testSecret);
        String signature2 = signatureUtil.generateSignature(commandData, testSecret);

        // Assert - same data should produce same signature
        assertEquals(signature1, signature2);
    }

    @Test
    void testSignatureChangesWithData() {
        // Arrange
        Map<String, Object> commandData1 = new HashMap<>();
        commandData1.put("deviceId", "rp-001");

        Map<String, Object> commandData2 = new HashMap<>();
        commandData2.put("deviceId", "rp-002");

        // Act
        String signature1 = signatureUtil.generateSignature(commandData1, testSecret);
        String signature2 = signatureUtil.generateSignature(commandData2, testSecret);

        // Assert - different data should produce different signatures
        assertNotEquals(signature1, signature2);
    }

    @Test
    void testSignatureChangesWithSecret() {
        // Arrange
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("deviceId", "rp-001");

        // Act
        String signature1 = signatureUtil.generateSignature(commandData, "secret1");
        String signature2 = signatureUtil.generateSignature(commandData, "secret2");

        // Assert - different secrets should produce different signatures
        assertNotEquals(signature1, signature2);
    }

    @Test
    void testVerifySignature() {
        // Arrange
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("deviceId", "rp-001");
        commandData.put("cmdId", "test-cmd-123");

        String signature = signatureUtil.generateSignature(commandData, testSecret);

        // Act
        boolean isValid = signatureUtil.verifySignature(commandData, signature, testSecret);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testVerifySignatureWithWrongSecret() {
        // Arrange
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("deviceId", "rp-001");

        String signature = signatureUtil.generateSignature(commandData, testSecret);

        // Act
        boolean isValid = signatureUtil.verifySignature(commandData, signature, "wrong-secret");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testVerifySignatureWithModifiedData() {
        // Arrange
        Map<String, Object> originalData = new HashMap<>();
        originalData.put("deviceId", "rp-001");

        String signature = signatureUtil.generateSignature(originalData, testSecret);

        Map<String, Object> modifiedData = new HashMap<>();
        modifiedData.put("deviceId", "rp-002"); // Modified!

        // Act
        boolean isValid = signatureUtil.verifySignature(modifiedData, signature, testSecret);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testKeyOrdering() {
        // Arrange - same data, different insertion order
        Map<String, Object> data1 = new HashMap<>();
        data1.put("a", "value1");
        data1.put("b", "value2");
        data1.put("c", "value3");

        Map<String, Object> data2 = new HashMap<>();
        data2.put("c", "value3");
        data2.put("a", "value1");
        data2.put("b", "value2");

        // Act
        String signature1 = signatureUtil.generateSignature(data1, testSecret);
        String signature2 = signatureUtil.generateSignature(data2, testSecret);

        // Assert - order shouldn't matter (keys are sorted)
        assertEquals(signature1, signature2);
    }

    @Test
    void testNestedObjects() {
        // Arrange
        Map<String, Object> nested = new HashMap<>();
        nested.put("ip", "192.168.1.1");
        nested.put("port", 80);

        Map<String, Object> commandData = new HashMap<>();
        commandData.put("deviceId", "rp-001");
        commandData.put("asic", nested);

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> {
            String signature = signatureUtil.generateSignature(commandData, testSecret);
            assertNotNull(signature);
        });
    }

    @Test
    void testEmptyData() {
        // Arrange
        Map<String, Object> emptyData = new HashMap<>();

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> {
            String signature = signatureUtil.generateSignature(emptyData, testSecret);
            assertNotNull(signature);
        });
    }
}
