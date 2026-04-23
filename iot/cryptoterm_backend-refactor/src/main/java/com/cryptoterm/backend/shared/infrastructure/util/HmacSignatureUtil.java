package com.cryptoterm.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility for generating HMAC-SHA256 signatures for ASIC proxy commands.
 */
@Component
public class HmacSignatureUtil {
    private static final Logger log = LoggerFactory.getLogger(HmacSignatureUtil.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    
    private final ObjectMapper objectMapper;
    
    public HmacSignatureUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Generate HMAC-SHA256 signature for a command.
     * The signature is computed over the canonical JSON representation (sorted keys, compact format).
     * 
     * @param commandData The command data as a Map (without signature field)
     * @param secret The secret key for HMAC
     * @return Base64-encoded signature
     */
    public String generateSignature(Map<String, Object> commandData, String secret) {
        try {
            // Create canonical JSON: sorted keys, no whitespace
            String canonicalJson = createCanonicalJson(commandData);
            
            // Compute HMAC-SHA256
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), 
                HMAC_SHA256
            );
            mac.init(secretKeySpec);
            
            byte[] hmacBytes = mac.doFinal(canonicalJson.getBytes(StandardCharsets.UTF_8));
            
            // Encode to Base64
            return Base64.getEncoder().encodeToString(hmacBytes);
            
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to generate HMAC signature", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    /**
     * Create canonical JSON representation for signing.
     * Uses sorted keys and compact format (no whitespace).
     */
    private String createCanonicalJson(Map<String, Object> data) {
        try {
            // Sort keys recursively
            Map<String, Object> sortedData = sortMapRecursively(data);
            
            // Serialize to compact JSON (no whitespace)
            return objectMapper.writeValueAsString(sortedData);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to create canonical JSON", e);
            throw new RuntimeException("Failed to create canonical JSON", e);
        }
    }
    
    /**
     * Recursively sort all maps by keys to ensure consistent JSON ordering.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> sortMapRecursively(Map<String, Object> map) {
        TreeMap<String, Object> sorted = new TreeMap<>();
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                // Recursively sort nested maps
                sorted.put(entry.getKey(), sortMapRecursively((Map<String, Object>) value));
            } else {
                sorted.put(entry.getKey(), value);
            }
        }
        
        return sorted;
    }
    
    /**
     * Verify a signature for a command.
     * 
     * @param commandData The command data (without signature field)
     * @param signature The signature to verify
     * @param secret The secret key
     * @return true if signature is valid
     */
    public boolean verifySignature(Map<String, Object> commandData, String signature, String secret) {
        String expectedSignature = generateSignature(commandData, secret);
        return expectedSignature.equals(signature);
    }
}
