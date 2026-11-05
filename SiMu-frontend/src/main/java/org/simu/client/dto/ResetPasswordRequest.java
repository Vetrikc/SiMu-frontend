package org.simu.client.dto;

public record ResetPasswordRequest(String token, String newPassword) {
}
