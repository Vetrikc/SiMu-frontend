package org.simu.client.web;

import org.simu.client.api.MusicApi;
import org.simu.client.api.PlaylistApi;
import org.simu.client.api.UserApi;
import org.simu.client.dto.MusicDto;
import org.simu.client.dto.PlaylistDto;
import org.simu.client.session.SessionAuthStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class PlaylistController {

    private final PlaylistApi playlistApi;
    private final UserApi userApi;
    private final MusicApi musicApi;
    private final SessionAuthStore session;

    public PlaylistController(PlaylistApi playlistApi, UserApi userApi, MusicApi musicApi,
                              SessionAuthStore session) {
        this.playlistApi = playlistApi;
        this.userApi = userApi;
        this.musicApi = musicApi;
        this.session = session;
    }

    @GetMapping("/playlists")
    public String list(Model model) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        List<PlaylistDto> playlists = userApi.myPlaylists();
        Map<String, MusicDto> music = musicApi.list().stream()
                .collect(Collectors.toMap(track -> String.valueOf(track.id()), Function.identity()));
        model.addAttribute("playlists", playlists);
        model.addAttribute("music", music);
        return "playlists";
    }

    @PostMapping("/playlists")
    public String create(@RequestParam String name, RedirectAttributes flash) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        PlaylistDto created = userApi.createMyPlaylist(name);
        flash.addFlashAttribute("success", "Плейлист «" + created.name() + "» создан");
        return "redirect:/playlists/" + created.id();
    }

    @GetMapping("/playlists/{id}")
    public String detail(@PathVariable Long id, Model model) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        PlaylistDto playlist = playlistApi.get(id);
        List<MusicDto> library = musicApi.list();
        Map<String, MusicDto> byId = library.stream()
                .collect(Collectors.toMap(track -> String.valueOf(track.id()), Function.identity()));
        List<MusicDto> tracks = playlist.tracks().stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .toList();

        model.addAttribute("playlist", playlist);
        model.addAttribute("tracks", tracks);
        model.addAttribute("library", library);
        return "playlist-detail";
    }

    @PostMapping("/playlists/add-track")
    public String addTrackFromLibrary(@RequestParam Long playlistId, @RequestParam Long musicId,
                                      RedirectAttributes flash) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        playlistApi.addTrack(playlistId, musicId);
        flash.addFlashAttribute("success", "Трек добавлен в плейлист");
        return "redirect:/music/" + musicId;
    }

    @PostMapping("/playlists/{id}/tracks")
    public String addTrack(@PathVariable Long id, @RequestParam Long musicId, RedirectAttributes flash) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        playlistApi.addTrack(id, musicId);
        flash.addFlashAttribute("success", "Трек добавлен в плейлист");
        return "redirect:/playlists/" + id;
    }

    @PostMapping("/playlists/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        if (!session.isAuthenticated()) {
            return "redirect:/signin";
        }
        playlistApi.delete(id);
        flash.addFlashAttribute("success", "Плейлист удалён");
        return "redirect:/playlists";
    }
}