package org.simu.client.dto;

import java.util.List;

public record PlaylistRequest(String name, List<String> tracks) {
}
