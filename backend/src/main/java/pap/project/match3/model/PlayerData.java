package pap.project.match3.model;

import org.springframework.lang.NonNull;

public record PlayerData(
        long playerId,
        String playerName,
        int playerElo
) { }
