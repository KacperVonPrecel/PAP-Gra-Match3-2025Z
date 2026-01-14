package pap.project.game.model.communication;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.game.match3.model.BoardState;

import java.util.Map;
import java.util.OptionalLong;

/**
 * Look {@link GameState}
 */
public record GameStateResponse(
        @NonNull BoardState boardState,
        long currentPlayerId,
        @NonNull Map<Long, PlayerCharactersState> playerStates, //XXX name is bad
        @NonNull OptionalLong attackingCharacterId,
        @Nullable GameEndDataResponse gameEndDataResponse
) {
    public GameStateResponse(@NonNull GameState gameState, @Nullable GameEndDataResponse gameEndDataResponse)
    {
        this(gameState.boardState(), gameState.currentPlayerId(), gameState.playerCharacters(), gameState.attackingCharacterId(), gameEndDataResponse);
    }
}
