package pap.project.game_history.controller.model.load;

public record MatchDTO(
        long playerId,
        String playerUserName,
        long opponentsId,
        String opponentsUserName,
        long finishTime,
        int playerEloChange,
        int opponentsEloChange,
        int playerEloPoints,
        int opponentsEloPoints,
        boolean isPlayerWinner
)
{}