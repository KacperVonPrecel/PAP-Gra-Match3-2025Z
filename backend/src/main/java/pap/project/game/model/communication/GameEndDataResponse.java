package pap.project.game.model.communication;

import org.springframework.lang.NonNull;

import java.util.Map;

public record GameEndDataResponse(
        long winnerId,
        boolean disconnected,
        @NonNull Map<Long, PlayerStatChange> playerStatChange
) { }
