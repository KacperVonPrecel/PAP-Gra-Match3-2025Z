package pap.project.game_history;

import org.springframework.lang.NonNull;
import pap.project.game_history.model.MatchFromHistoryData;

import java.util.List;

public record LoadHistoryMatchesData (
        @NonNull List<MatchFromHistoryData> matches,
        boolean moreToLoad
) {
    public LoadHistoryMatchesData {
        matches = List.copyOf(matches);
    }
}
