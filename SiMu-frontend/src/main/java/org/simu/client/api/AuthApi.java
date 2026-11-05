package org.simu.client.api;

import org.simu.client.dto.ExchangeTicketRequest;
import org.simu.client.dto.ForgotPasswordRequest;
import org.simu.client.dto.LogoutRequest;
import org.simu.client.dto.MeResponse;
import org.simu.client.dto.ResetPasswordRequest;
import org.simu.client.dto.SigninRequest;
import org.simu.client.dto.SigninResponse;
import org.simu.client.dto.SignupRequest;
import org.simu.client.dto.SignupResponse;
import org.simu.client.dto.VerifyEmailRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthApi {

    private final BackendClient backend;

    public AuthApi(BackendClient backend) {
        this.backend = backend;
    }

    public SigninResponse signin(String username, String password) {
        return backend.postPublic("/api/v1/auth/signin", new SigninRequest(username, password), SigninResponse.class);
    }

    public SignupResponse signup(String username, String email, String password) {
        return backend.postPublic("/api/v1/auth/signup", new SignupRequest(username, email, password), SignupResponse.class);
    }

    public MeResponse me() {
        return backend.get("/api/v1/auth/me", MeResponse.class);
    }

    public void logout(String refreshToken) {
        backend.postNoContent("/api/v1/auth/logout", new LogoutRequest(refreshToken));
    }

    public void verifyEmail(String token) {
        backend.postPublicNoContent("/api/v1/auth/verify-email", new VerifyEmailRequest(token));
    }

    public void forgotPassword(String email) {
        backend.postPublicNoContent("/api/v1/auth/forgot-password", new ForgotPasswordRequest(email));
    }

    public void resetPassword(String token, String newPassword) {
        backend.postPublicNoContent("/api/v1/auth/reset-password", new ResetPasswordRequest(token, newPassword));
    }

    public SigninResponse exchangeTicket(String ticket) {
        return backend.postPublic("/api/v1/auth/oauth2/exchange", new ExchangeTicketRequest(ticket), SigninResponse.class);
    }
}