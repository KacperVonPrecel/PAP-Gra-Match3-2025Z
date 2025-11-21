package pap.project.game_history.controller.model.load;

public record MatchDTO(
        Long matchId,
        Long winnerId,
        Long loserId,
        Long finishTime,
        Integer winnerEloChange,
        Integer loserEloChange
)
{}
