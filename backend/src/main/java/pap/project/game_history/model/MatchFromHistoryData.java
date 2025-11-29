package pap.project.game_history.model;

import org.springframework.lang.NonNull;

import java.util.OptionalLong;

public record MatchFromHistoryData(
        long playerId,
        @NonNull String playerUsername,
        long opponentsId,
        @NonNull String opponentsUsername,
        long finishTime,
        int playerEloChange,
        int opponentsEloChange,
        int playerEloPoints,
        int opponentsEloPoints,
        boolean isPlayerWinner
)
{}