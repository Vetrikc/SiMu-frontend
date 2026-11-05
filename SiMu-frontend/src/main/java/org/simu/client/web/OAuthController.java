package org.simu.client.web;

import org.simu.client.api.AuthApi;
import org.simu.client.config.BackendProperties;
import org.simu.client.dto.MeResponse;
import org.simu.client.dto.SigninResponse;
import org.simu.client.session.SessionAuth;
import org.simu.client.session.SessionAuthStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.time.Instant;

/**
 * Google login is delegated to auth-service: the browser goes straight to the gateway, and the
 * callback comes back here with a one-time ticket that this server exchanges for the token pair.
 */
@Controller
public class OAuthController {

    private final AuthApi authApi;
    private final SessionAuthStore session;
    private final BackendProperties backend;

    public OAuthController(AuthApi authApi, SessionAuthStore session, BackendProperties backend) {
        this.authApi = authApi;
        this.session = session;
        this.backend = backend;
    }

    @GetMapping("/oauth2/google")
    public ResponseEntity<Void> start() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(backend.baseUrl() + "/api/v1/auth/oauth2/google"))
                .build();
    }

    @GetMapping("/oauth2/callback")
    public String callback(@RequestParam(required = false) String ticket,
                           RedirectAttributes flash) {
        if (ticket == null || ticket.isBlank()) {
            flash.addFlashAttribute("error", "Ссылка входа не содержит тикет");
            return "redirect:/signin";
        }

        SigninResponse tokens = authApi.exchangeTicket(ticket);
        SessionAuth auth = new SessionAuth();
        auth.setAccessToken(tokens.token());
        auth.setRefreshToken(tokens.refreshToken());
        auth.setAccessExpiresAt(Instant.now().plusSeconds(tokens.expiresIn()).toEpochMilli());
        session.save(auth);

        MeResponse me = authApi.me();
        auth.setUsername(me.username());
        session.save(auth);

        flash.addFlashAttribute("success", "Вы вошли через Google как " + me.username());
        return "redirect:/home";
    }
}
