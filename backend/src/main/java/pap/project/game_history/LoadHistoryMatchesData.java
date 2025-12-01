package pap.project.game_history;

import pap.project.game_history.model.MatchFromHistoryData;

import java.util.List;

public class LoadHistoryMatchesData {
    private final List<MatchFromHistoryData> matches;
    private final boolean isMoreToLoad;

    public LoadHistoryMatchesData(List<MatchFromHistoryData> matches, boolean moreToLoad)
    {
        this.matches = matches;
        this.isMoreToLoad = moreToLoad;
    }

    public List<MatchFromHistoryData> getMatches()
    {
        return matches;
    }

    public boolean isMoreToLoad()
    {
        return isMoreToLoad;
    }
}
