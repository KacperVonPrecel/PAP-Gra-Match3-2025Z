package pap.project.match3.model;

import org.springframework.lang.NonNull;

import java.util.Map;

public record GameEndData(
        long winnerId,
        boolean disconnected,
        @NonNull Map<Long, PlayerStatChange> playerGains
) { }
