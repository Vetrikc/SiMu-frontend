package org.simu.client.api;

import org.simu.client.dto.MusicDto;
import org.simu.client.dto.MusicUpload;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Component
public class MusicApi {

    private final BackendClient backend;

    public MusicApi(BackendClient backend) {
        this.backend = backend;
    }

    public List<MusicDto> list() {
        return backend.get("/api/v1/music", new ParameterizedTypeReference<List<MusicDto>>() {
        });
    }

    public MusicDto get(Long id) {
        return backend.get("/api/v1/music/" + id, MusicDto.class);
    }

    /**
     * Sends the two parts the backend expects: the audio file and the JSON metadata part.
     */
    public MusicDto upload(String name, String artist, MultipartFile file) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(
                file.getContentType() == null ? "application/octet-stream" : file.getContentType()));
        parts.add("file", new HttpEntity<>(new ByteArrayResource(readBytes(file)) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        }, fileHeaders));

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        parts.add("music", new HttpEntity<>(new MusicUpload(name, artist), jsonHeaders));

        return backend.postMultipart("/api/v1/music", parts, MusicDto.class);
    }

    public void delete(Long id) {
        backend.delete("/api/v1/music/" + id);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
