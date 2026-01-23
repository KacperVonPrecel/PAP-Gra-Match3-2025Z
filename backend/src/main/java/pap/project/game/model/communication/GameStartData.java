package pap.project.game.model.communication;

import org.springframework.lang.NonNull;

import java.util.List;

/**
 * @param playerData key is playerId
 */
public record GameStartData(
        @NonNull String gameId,
        @NonNull GameState gameState,
        @NonNull List<PlayerStartData> playerData
) { }
