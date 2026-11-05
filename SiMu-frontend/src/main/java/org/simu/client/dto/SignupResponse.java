package org.simu.client.dto;

public record SignupResponse(String message, String token, String refreshToken, long expiresIn,
                              long refreshExpiresIn, Long userId) {
}
