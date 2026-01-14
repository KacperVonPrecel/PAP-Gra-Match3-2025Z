package pap.project.match3.model;

import java.util.Map;

public record GameEndData(
        long winnerId,
        boolean disconnected,
        Map<Long, PlayerGain> playerGains
) { }
