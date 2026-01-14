package pap.project.match3.model;

import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.OptionalLong;

/**
 * @param boardState new state of board
 * @param currentPlayerId id of player which currently is about to make move.
 * @param playerStates key is player id. Value is new characters state.
 * @param attackingCharacterId id of character which made attack. It is empty if it used in GameStartData
 */
public record GameState(
        @NonNull BoardState boardState,
        long currentPlayerId,
        @NonNull Map<Long, PlayerCharactersState> playerStates, //XXX name is bad
        @NonNull OptionalLong attackingCharacterId
) { }
