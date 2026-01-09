package pap.project.user_stats;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.user_stats.model.LoadGlobalRankingData;
import pap.project.user_stats.model.RankingEntry;

@Service
public class RankingService
{
    private final @NonNull UserStatsRepository userStatsRepository;

    public RankingService(@NonNull UserStatsRepository userStatsRepository)
    {
        this.userStatsRepository = userStatsRepository;
    }

    public LoadGlobalRankingData loadGlobalRankingEntries(int pageNumber, int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        final Page<RankingEntry> loadedRankingEntries = userStatsRepository.getGlobalRanking(pageable);
        return new LoadGlobalRankingData(loadedRankingEntries, loadedRankingEntries.hasNext());
    }

    public int getUserPositionInRanking(long userId)
    {
        return userStatsRepository.calculateRankPositionByUserId(userId);
    }
}
