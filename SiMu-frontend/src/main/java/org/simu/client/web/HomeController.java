package org.simu.client.web;

import org.simu.client.api.MusicApi;
import org.simu.client.api.UserApi;
import org.simu.client.dto.MusicDto;
import org.simu.client.session.SessionAuthStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {

    private final MusicApi musicApi;
    private final UserApi userApi;
    private final SessionAuthStore session;

    public HomeController(MusicApi musicApi, UserApi userApi, SessionAuthStore session) {
        this.musicApi = musicApi;
        this.userApi = userApi;
        this.session = session;
    }

    @GetMapping("/")
    public String landing() {
        return session.isAuthenticated() ? "redirect:/home" : "index";
    }

    @GetMapping("/home")
    public String library(Model model) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        List<MusicDto> tracks = musicApi.list();
        model.addAttribute("tracks", tracks);
        model.addAttribute("playlists", userApi.myPlaylists());
        return "home";
    }

    @GetMapping("/music/{id}")
    public String track(@PathVariable Long id, Model model) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        model.addAttribute("track", musicApi.get(id));
        model.addAttribute("tracks", musicApi.list());
        model.addAttribute("playlists", userApi.myPlaylists());
        return "music-detail";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return session.isAuthenticated() ? "upload" : "redirect:/signin";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam String name,
                         @RequestParam String artist,
                         @RequestParam("file") MultipartFile file,
                         RedirectAttributes flash) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        if (file == null || file.isEmpty()) {
            flash.addFlashAttribute("error", "Выберите файл трека");
            return "redirect:/upload";
        }
        MusicDto created = musicApi.upload(name, artist, file);
        flash.addFlashAttribute("success", "Трек «" + created.name() + "» загружен");
        return "redirect:/music/" + created.id();
    }

    @PostMapping("/music/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        musicApi.delete(id);
        flash.addFlashAttribute("success", "Трек удалён");
        return "redirect:/home";
    }
}
