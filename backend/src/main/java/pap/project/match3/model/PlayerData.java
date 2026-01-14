package pap.project.match3.model;

import org.springframework.lang.NonNull;

import java.util.List;

public record PlayerData(
        long playerId,
        @NonNull String playerName,
        int playerElo,
        @NonNull List<GameCharacter> characters
) { }
