package pap.project.game.model.communication;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.game.match3.model.BoardState;

import java.util.Map;
import java.util.OptionalLong;

/**
 * @param boardState new state of board
 * @param currentPlayerId id of player which currently is about to make move.
 * @param playerCharacters key is player id. Value is new characters state.
 * @param attackingCharacterId id of character which made attack. It is empty if it used in GameStartData
 * @param gameEndDataResponse if present that mean game is finished
 */
public record GameState(
        @NonNull BoardState boardState,
        long currentPlayerId,
        @NonNull Map<Long, PlayerCharactersState> playerCharacters,
        @NonNull OptionalLong attackingCharacterId,
        @Nullable GameEndData gameEndData
) { }
