package org.simu.client.dto;

import java.time.Instant;
import java.util.Set;

public record MeResponse(Long id, String username, String email, Set<String> roles, boolean emailVerified,
                         boolean passwordLoginEnabled, Instant createdAt) {
}
