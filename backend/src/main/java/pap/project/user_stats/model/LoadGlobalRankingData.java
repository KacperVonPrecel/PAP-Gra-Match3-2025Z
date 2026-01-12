package pap.project.user_stats.model;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

public record LoadGlobalRankingData(
        @NonNull List<RankingEntry> loadedRankingEntries,
        boolean isMoreToLoad)
{
}
