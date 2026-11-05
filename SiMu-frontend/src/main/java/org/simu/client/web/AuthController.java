package org.simu.client.web;

import org.simu.client.api.AuthApi;
import org.simu.client.dto.SigninResponse;
import org.simu.client.dto.SignupResponse;
import org.simu.client.session.SessionAuth;
import org.simu.client.session.SessionAuthStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;

@Controller
public class AuthController {

    private final AuthApi authApi;
    private final SessionAuthStore session;

    public AuthController(AuthApi authApi, SessionAuthStore session) {
        this.authApi = authApi;
        this.session = session;
    }

    @GetMapping("/signin")
    public String signinPage(Model model) {
        if (session.isAuthenticated()) {
            return "redirect:/home";
        }
        return "auth/signin";
    }

    @PostMapping("/signin")
    public String signin(@RequestParam String username,
                         @RequestParam String password,
                         RedirectAttributes flash) {
        SigninResponse tokens = authApi.signin(username, password);
        store(tokens, username);
        flash.addFlashAttribute("success", "С возвращением, " + username);
        return "redirect:/home";
    }

    @GetMapping("/signup")
    public String signupPage() {
        if (session.isAuthenticated()) {
            return "redirect:/home";
        }
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         RedirectAttributes flash) {
        SignupResponse response = authApi.signup(username, email, password);
        store(new SigninResponse(response.token(), response.refreshToken(),
                response.expiresIn(), response.refreshExpiresIn()), username);
        flash.addFlashAttribute("success",
                "Аккаунт создан. Мы отправили письмо для подтверждения e-mail.");
        return "redirect:/home";
    }

    @PostMapping("/logout")
    public String logout(RedirectAttributes flash) {
        session.current().ifPresent(auth -> {
            try {
                authApi.logout(auth.getRefreshToken());
            } catch (RuntimeException ignored) {
                // the local session is dropped even when the backend is unreachable
            }
        });
        session.clear();
        flash.addFlashAttribute("success", "Вы вышли из аккаунта");
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes flash) {
        authApi.forgotPassword(email);
        flash.addFlashAttribute("success",
                "Если такой адрес зарегистрирован, письмо со ссылкой уже отправлено.");
        return "redirect:/signin";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model,
                                    RedirectAttributes flash) {
        if (token == null || token.isBlank()) {
            flash.addFlashAttribute("error", "В ссылке нет токена восстановления");
            return "redirect:/signin";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                RedirectAttributes flash) {
        authApi.resetPassword(token, newPassword);
        session.clear();
        flash.addFlashAttribute("success", "Пароль обновлён. Войдите с новым паролем.");
        return "redirect:/signin";
    }

    /**
     * Landing page of the link from the verification e-mail.
     */
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam(required = false) String token, RedirectAttributes flash) {
        if (token == null || token.isBlank()) {
            flash.addFlashAttribute("error", "В ссылке нет токена подтверждения");
            return session.isAuthenticated() ? "redirect:/profile" : "redirect:/signin";
        }
        authApi.verifyEmail(token);
        flash.addFlashAttribute("success", "E-mail подтверждён");
        return session.isAuthenticated() ? "redirect:/profile" : "redirect:/signin";
    }

    private void store(SigninResponse tokens, String username) {
        SessionAuth auth = new SessionAuth();
        auth.setAccessToken(tokens.token());
        auth.setRefreshToken(tokens.refreshToken());
        auth.setUsername(username);
        auth.setAccessExpiresAt(Instant.now().plusSeconds(tokens.expiresIn()).toEpochMilli());
        session.save(auth);
    }
}
