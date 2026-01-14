package pap.project.match3.model;

import org.springframework.lang.NonNull;

public record GameState(
        @NonNull BoardState boardState,
        long currentPlayerId,
        @NonNull PlayerState[] playerStates
) { }
