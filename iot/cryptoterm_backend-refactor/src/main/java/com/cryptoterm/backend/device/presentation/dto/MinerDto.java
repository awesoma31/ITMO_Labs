package com.cryptoterm.backend.dto;

import java.util.UUID;

/**
 * DTO для представления майнера
 */
public record MinerDto(
    UUID id,
    String label,
    String vendor,
    String model,
    String mode
) {}
