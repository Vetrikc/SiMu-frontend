package org.simu.client.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.simu.client.api.BackendException;
import org.simu.client.session.SessionAuthStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.net.URI;

@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    private final SessionAuthStore session;

    public GlobalControllerAdvice(SessionAuthStore session) {
        this.session = session;
    }

    @ModelAttribute("currentUser")
    public String currentUser() {
        return session.username();
    }

    @ExceptionHandler(BackendException.class)
    public String handleBackend(BackendException ex, HttpServletRequest request,
                                Model model, HttpServletResponse response) {
        log.warn("Backend call failed: {} {} -> {} {}", request.getMethod(), request.getRequestURI(),
                ex.getStatus(), ex.getMessage());

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(ex.getStatus() >= 400 && ex.getStatus() < 500 ? ex.getStatus() : 502);
            model.addAttribute("status", ex.getStatus());
            model.addAttribute("error", ex.getMessage());
            return "error-page";
        }

        // POST: keep the message for the page we redirect back to
        RequestContextUtils.getOutputFlashMap(request).put("error", ex.getMessage());
        return "redirect:" + backTo(request);
    }

    private String backTo(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            try {
                String path = URI.create(referer).getPath();
                if (path != null && !path.isBlank()) {
                    return path;
                }
            } catch (IllegalArgumentException ignored) {
                // malformed referer - fall through to the form defaults
            }
        }
        return switch (request.getRequestURI()) {
            case "/signin", "/signup", "/forgot-password", "/reset-password" -> request.getRequestURI();
            default -> "/home";
        };
    }
}
