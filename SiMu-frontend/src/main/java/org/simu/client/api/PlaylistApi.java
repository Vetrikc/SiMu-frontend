package org.simu.client.api;

import org.simu.client.dto.CreatePlaylistRequest;
import org.simu.client.dto.PlaylistDto;
import org.springframework.stereotype.Component;

@Component
public class PlaylistApi {

    private final BackendClient backend;

    public PlaylistApi(BackendClient backend) {
        this.backend = backend;
    }

    public PlaylistDto create(String name, String description) {
        return backend.post("/api/v1/playlists", new CreatePlaylistRequest(name, description), PlaylistDto.class);
    }

    public PlaylistDto get(Long id) {
        return backend.get("/api/v1/playlists/" + id, PlaylistDto.class);
    }

    public PlaylistDto addTrack(Long playlistId, Long musicId) {
        return backend.postEmpty("/api/v1/playlists/" + playlistId + "/tracks/" + musicId, PlaylistDto.class);
    }

    public void delete(Long id) {
        backend.delete("/api/v1/playlists/" + id);
    }
}
