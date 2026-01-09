package pap.project.user_stats.model;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

public record LoadGlobalRankingData(
        @NonNull Page<RankingEntry> loadedRankingEntries,
        boolean isMoreToLoad)
{
}
