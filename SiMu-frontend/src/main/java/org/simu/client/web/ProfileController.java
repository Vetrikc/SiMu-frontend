package org.simu.client.web;

import org.simu.client.api.AuthApi;
import org.simu.client.api.UserApi;
import org.simu.client.session.SessionAuthStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.simu.client.dto.MeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final AuthApi authApi;
    private final UserApi userApi;
    private final SessionAuthStore session;

    public ProfileController(AuthApi authApi, UserApi userApi, SessionAuthStore session) {
        this.authApi = authApi;
        this.userApi = userApi;
        this.session = session;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        MeResponse profile = authApi.me();
        model.addAttribute("profile", profile);
        model.addAttribute("account", userApi.info());
        model.addAttribute("createdAt", profile.createdAt() == null ? "—"
                : java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                        .withZone(java.time.ZoneId.systemDefault())
                        .format(profile.createdAt()));
        return "profile";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(RedirectAttributes flash) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        userApi.deleteAccount();
        session.clear();
        flash.addFlashAttribute("success", "Аккаунт удалён. Данные сохранены в архиве и недоступны.");
        return "redirect:/";
    }
}