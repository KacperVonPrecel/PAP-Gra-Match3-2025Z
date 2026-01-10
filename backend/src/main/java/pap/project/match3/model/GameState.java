package pap.project.match3.model;

import org.springframework.lang.Nullable;

public record GameState(
        @Nullable BoardState boardState,
        int currentPlayerId
) { }
