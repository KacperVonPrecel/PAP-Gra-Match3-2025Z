package pap.project.game_history;

import org.springframework.lang.NonNull;
import pap.project.game_history.model.MatchFromHistoryData;

import java.util.Collections;
import java.util.List;

public class LoadHistoryMatchesData {
    private final @NonNull List<MatchFromHistoryData> matches;
    private final boolean isMoreToLoad;

    public LoadHistoryMatchesData(@NonNull List<MatchFromHistoryData> matches, boolean moreToLoad)
    {
        this.matches = Collections.unmodifiableList(matches);
        this.isMoreToLoad = moreToLoad;
    }

    /**
     * @return unmodifiable list
     */
    public @NonNull List<MatchFromHistoryData> getMatches()
    {
        return matches;
    }

    public boolean isMoreToLoad()
    {
        return isMoreToLoad;
    }
}
