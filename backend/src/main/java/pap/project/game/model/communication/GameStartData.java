package pap.project.game.model.communication;

import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @param playerData key is playerId
 */
public record GameStartData(
        @NonNull String gameId,
        @NonNull GameState gameState,
        @NonNull List<XXX3> playerData
) { }
