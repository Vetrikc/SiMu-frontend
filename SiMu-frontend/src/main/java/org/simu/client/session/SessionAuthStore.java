package org.simu.client.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.simu.client.dto.RefreshRequest;
import org.simu.client.dto.SigninResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Optional;

/**
 * Keeps the token pair in the server-side session and renews the access token when it is about to
 * expire (or when the backend answers 401). The browser never sees the tokens: only the session id
 * travels in an HttpOnly cookie.
 */
@Component
public class SessionAuthStore {

    private static final String ATTRIBUTE = "simu.auth";
    private static final long REFRESH_AHEAD_MS = 30_000;

    private final RestClient restClient;

    public SessionAuthStore(RestClient backendRestClient) {
        this.restClient = backendRestClient;
    }

    public Optional<SessionAuth> current() {
        HttpSession session = session(false);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((SessionAuth) session.getAttribute(ATTRIBUTE));
    }

    public boolean isAuthenticated() {
        return current().isPresent();
    }

    public String username() {
        return current().map(SessionAuth::getUsername).orElse(null);
    }

    public String accessToken() {
        return current().map(SessionAuth::getAccessToken).orElse(null);
    }

    public void save(SessionAuth auth) {
        session(true).setAttribute(ATTRIBUTE, auth);
    }

    public void clear() {
        HttpSession session = session(false);
        if (session != null) {
            session.removeAttribute(ATTRIBUTE);
        }
    }

    /**
     * Called before every backend request: renews the access token when less than 30 seconds are left.
     */
    public void refreshIfExpiring() {
        current().ifPresent(auth -> {
            if (auth.getAccessExpiresAt() - System.currentTimeMillis() < REFRESH_AHEAD_MS) {
                refresh();
            }
        });
    }

    /**
     * @return true when a new pair was obtained and stored
     */
    public boolean refresh() {
        Optional<SessionAuth> existing = current();
        if (existing.isEmpty() || existing.get().getRefreshToken() == null) {
            return false;
        }
        try {
            SigninResponse response = restClient.post()
                    .uri("/api/v1/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RefreshRequest(existing.get().getRefreshToken()))
                    .retrieve()
                    .body(SigninResponse.class);

            if (response == null || response.token() == null) {
                clear();
                return false;
            }

            SessionAuth auth = existing.get();
            auth.setAccessToken(response.token());
            auth.setRefreshToken(response.refreshToken());
            auth.setAccessExpiresAt(Instant.now().plusSeconds(response.expiresIn()).toEpochMilli());
            save(auth);
            return true;
        } catch (RuntimeException e) {
            clear();
            return false;
        }
    }

    private HttpSession session(boolean create) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return create ? request.getSession(true) : request.getSession(false);
    }
}
