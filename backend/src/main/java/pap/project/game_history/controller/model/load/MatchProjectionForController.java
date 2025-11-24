package pap.project.game_history.controller.model.load;

import java.util.OptionalLong;

public record MatchProjectionForController(
        OptionalLong playerId,
        String playerUserName,
        OptionalLong opponentsId,
        String opponentsUserName,
        long finishTime,
        int playerEloChange,
        int opponentsEloChange,
        int playerEloPoints,
        int opponentsEloPoints,
        boolean isPlayerWinner
)
{}