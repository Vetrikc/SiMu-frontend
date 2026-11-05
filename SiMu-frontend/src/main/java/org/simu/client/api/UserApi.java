package org.simu.client.api;

import org.simu.client.dto.PlaylistDto;
import org.simu.client.dto.PlaylistRequest;
import org.simu.client.dto.UserInfoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserApi {

    private final BackendClient backend;

    public UserApi(BackendClient backend) {
        this.backend = backend;
    }

    public UserInfoResponse info() {
        return backend.get("/api/v1/users", UserInfoResponse.class);
    }

    public List<PlaylistDto> myPlaylists() {
        return backend.get("/api/v1/users/playlists", new ParameterizedTypeReference<List<PlaylistDto>>() {
        });
    }

    public PlaylistDto createMyPlaylist(String name) {
        return backend.post("/api/v1/users/playlists", new PlaylistRequest(name, List.of()), PlaylistDto.class);
    }

    public void deleteAccount() {
        backend.delete("/api/v1/users/me");
    }
}
