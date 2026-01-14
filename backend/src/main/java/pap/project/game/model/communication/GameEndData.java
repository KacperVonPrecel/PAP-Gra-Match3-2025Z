package pap.project.game.model.communication;

import org.springframework.lang.NonNull;

import java.util.Map;

public record GameEndData(
        long winnerId,
        boolean disconnected,
        @NonNull Map<Long, PlayerStatChange> playerGains
) { }
