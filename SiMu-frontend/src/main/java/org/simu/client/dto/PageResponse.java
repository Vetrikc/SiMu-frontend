package org.simu.client.dto;

import java.util.List;

/**
 * Keyset page returned by the backend: items, cursor for the next call and a flag that says
 * whether more data exists.
 */
public record PageResponse<T>(List<T> items, Long nextCursor, boolean hasMore) {
}
