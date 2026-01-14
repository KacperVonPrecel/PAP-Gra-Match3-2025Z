package pap.project.match3.model;

import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * @param playerData key is playerId
 */
public record GameStartData(
        @NonNull String gameId,
        @NonNull GameState gameState,
        @NonNull Map<Long, PlayerData> playerData
) { }
