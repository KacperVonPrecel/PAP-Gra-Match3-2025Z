package pap.project.user_stats.model.controller;

import org.springframework.lang.NonNull;
import pap.project.user_stats.model.LoadGlobalRankingData;

public record LoadGlobalRankingResponse(
        @NonNull LoadGlobalRankingData rankingData,
        int userPosition,
        int userEloPoints
) {
}
