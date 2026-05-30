package pap.project.game.model.communication;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.game.match3.model.BoardState;

import java.util.List;
import java.util.OptionalLong;

/**
 * Look {@link GameState}
 */
public record GameStateResponse(
        @NonNull BoardState boardState,
        long currentPlayerId,
        @NonNull List<PlayerState> playerStates,
        @NonNull OptionalLong attackingCharacterId,
        @Nullable GameEndDataResponse gameEndDataResponse
) {
    public GameStateResponse(@NonNull GameState gameState, @Nullable GameEndDataResponse gameEndDataResponse)
    {
        this(gameState.boardState(), gameState.currentPlayerId(), gameState.playerCharacters().entrySet().stream().map(e -> new PlayerState(e.getKey(), e.getValue().charactersHealth().entrySet().stream().map(e2 -> new CharacterHealth(e2.getKey(), e2.getValue())).toList())).toList(), gameState.attackingCharacterId(), gameEndDataResponse);
    }
}
