package pap.project.game.model.communication;

import org.springframework.lang.NonNull;
import org.w3c.dom.stylesheets.LinkStyle;
import pap.project.game_history.model.HistoryCharacterData;

import java.util.List;

public record GameEndData(
        long winnerId,
        long loserId,
        @NonNull List<HistoryCharacterData> winnerCharacters,
        @NonNull List<HistoryCharacterData> loserCharacters
        ) {
}
