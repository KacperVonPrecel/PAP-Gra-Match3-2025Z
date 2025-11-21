package pap.project.game_history.controller.model.load;

public record MatchDTO(
        long playerId,
        String playerUserName,
        long opponentId,
        String opponentUserName,
        long finishTime,
        int winnerEloChange,
        int loserEloChange
)
{}

// nazwa, id_który_szukał, staty, czy_zwycięstwo
// player - który szukał
// opponent - przeciwnik