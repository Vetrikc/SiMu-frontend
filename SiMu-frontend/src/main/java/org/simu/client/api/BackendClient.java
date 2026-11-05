package org.simu.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.simu.client.dto.ErrorResponse;
import org.simu.client.session.SessionAuthStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.function.Function;

/**
 * Thin wrapper over RestClient: attaches the access token, renews it on 401 and translates backend
 * errors into {@link BackendException}. Public endpoints (sign-in, sign-up, password recovery) are
 * called without a token, so a rejected password never triggers a token refresh.
 */
@Component
public class BackendClient {

    private static final Logger log = LoggerFactory.getLogger(BackendClient.class);

    private final RestClient restClient;
    private final SessionAuthStore authStore;
    private final ObjectMapper objectMapper;

    public BackendClient(RestClient backendRestClient, SessionAuthStore authStore, ObjectMapper objectMapper) {
        this.restClient = backendRestClient;
        this.authStore = authStore;
        this.objectMapper = objectMapper;
    }

    public <T> T get(String path, Class<T> type) {
        return call(token -> restClient.get().uri(path).headers(headers(token)).retrieve().body(type));
    }

    public <T> T get(String path, ParameterizedTypeReference<T> type) {
        return call(token -> restClient.get().uri(path).headers(headers(token)).retrieve().body(type));
    }

    public <T> T post(String path, Object body, Class<T> type) {
        return call(token -> restClient.post().uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers(token))
                .body(body)
                .retrieve()
                .body(type));
    }

    /**
     * For endpoints that must not use the session (sign-in, sign-up, recovery, OAuth exchange).
     */
    public <T> T postPublic(String path, Object body, Class<T> type) {
        return publicCall(() -> restClient.post().uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(type));
    }

    public void postPublicNoContent(String path, Object body) {
        publicCall(() -> restClient.post().uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity());
    }

    public <T> T postEmpty(String path, Class<T> type) {
        return call(token -> restClient.post().uri(path).headers(headers(token)).retrieve().body(type));
    }

    public void postNoContent(String path, Object body) {
        call(token -> restClient.post().uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers(token))
                .body(body)
                .retrieve()
                .toBodilessEntity());
    }

    public void delete(String path) {
        call(token -> restClient.delete().uri(path).headers(headers(token)).retrieve().toBodilessEntity());
    }

    public <T> T postMultipart(String path, MultiValueMap<String, Object> parts, Class<T> type) {
        return call(token -> restClient.post().uri(path)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(headers(token))
                .body(parts)
                .retrieve()
                .body(type));
    }

    private java.util.function.Consumer<HttpHeaders> headers(String token) {
        return httpHeaders -> {
            if (token != null) {
                httpHeaders.setBearerAuth(token);
            }
        };
    }

    private <T> T call(Function<String, T> request) {
        authStore.refreshIfExpiring();
        try {
            return request.apply(authStore.accessToken());
        } catch (HttpClientErrorException.Unauthorized e) {
            if (authStore.refresh()) {
                try {
                    return request.apply(authStore.accessToken());
                } catch (HttpClientErrorException retry) {
                    throw translate(retry);
                } catch (RestClientException retry) {
                    throw unavailable(retry);
                }
            }
            throw translate(e);
        } catch (HttpClientErrorException e) {
            throw translate(e);
        } catch (RestClientException e) {
            throw unavailable(e);
        }
    }

    private <T> T publicCall(java.util.function.Supplier<T> request) {
        try {
            return request.get();
        } catch (HttpClientErrorException e) {
            throw translate(e);
        } catch (RestClientException e) {
            throw unavailable(e);
        }
    }

    private BackendException unavailable(RestClientException e) {
        log.warn("Backend is unreachable: {}", e.getMessage());
        return new BackendException(503, "BACKEND_UNAVAILABLE", Messages.humanize("BACKEND_UNAVAILABLE", null));
    }

    private BackendException translate(HttpClientErrorException e) {
        String code = "ERROR";
        String message = e.getStatusText();
        try {
            ErrorResponse error = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            if (error.message() != null && !error.message().isBlank()) {
                message = error.message();
            }
            if (error.errorCode() != null) {
                code = error.errorCode();
            }
        } catch (Exception ignored) {
            // body is not the JSON error contract - keep the status text
        }
        return new BackendException(e.getStatusCode().value(), code, Messages.humanize(code, message));
    }
}
