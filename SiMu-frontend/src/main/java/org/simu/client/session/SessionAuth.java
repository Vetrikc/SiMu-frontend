package org.simu.client.session;

import java.io.Serializable;

/**
 * Tokens of the signed-in user. They live in the HTTP session on the server, never in
 * localStorage: the browser only gets an HttpOnly session cookie.
 */
public class SessionAuth implements Serializable {

    private String accessToken;
    private String refreshToken;
    private String username;
    private long accessExpiresAt;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAccessExpiresAt() {
        return accessExpiresAt;
    }

    public void setAccessExpiresAt(long accessExpiresAt) {
        this.accessExpiresAt = accessExpiresAt;
    }
}
