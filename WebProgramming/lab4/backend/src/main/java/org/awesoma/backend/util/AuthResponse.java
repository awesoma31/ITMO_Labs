package org.awesoma.backend.util;

public record AuthResponse(boolean success, String accessToken, String refreshToken, String error) {
}
