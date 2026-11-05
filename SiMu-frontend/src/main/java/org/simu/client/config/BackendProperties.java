package org.simu.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backend")
public record BackendProperties(String baseUrl, int connectTimeoutMs, int readTimeoutMs) {
}
