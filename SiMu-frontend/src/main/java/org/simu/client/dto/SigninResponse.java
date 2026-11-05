package org.simu.client.dto;

public record SigninResponse(String token, String refreshToken, long expiresIn, long refreshExpiresIn) {
}
