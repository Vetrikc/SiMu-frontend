package org.simu.client.dto;

import java.util.List;

public record PlaylistDto(Long id, String name, List<String> tracks) {
}
