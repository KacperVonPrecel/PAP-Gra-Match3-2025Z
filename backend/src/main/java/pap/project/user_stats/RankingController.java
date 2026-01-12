package pap.project.user_stats;


import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pap.project.user_data.UserDataService;
import pap.project.user_data.model.UserData;
import pap.project.user_stats.model.LoadGlobalRankingData;
import pap.project.user_stats.model.controller.LoadGlobalRankingResponse;
import pap.project.users.UserAuthDetails;

/**
 * <p>
 * Whole ranking system is in some way invalid. When some user was inside loading global ranking, and there happen some
 * change (for example game ended, and users ranking got updated) then the same user could possibly be loaded multiple times.
 * And getting user place in world ranking could be so much slow. There is multiple possible approaches (i think so) to solve this problem.
 * The one of possibilities is caching global ranking (with some time period, for example once per hour) and always returning cached ranking.
 * </p>
 * <p>
 * Maybe it is also shouldn't be made. And only show some top playes (for example top 1000), And show user only his position in global ranking.
 * And it will be returned only on some user request. Probably there also should be some form of caching and refreshing once per some period of time.
 * </p>
 */
@RestController
@RequestMapping("/api/ranking")
public class RankingController
{
    private final @NonNull RankingService rankingService;
    private final @NonNull UserDataService userDataService;

    public RankingController(@NonNull RankingService rankingService, @NonNull UserDataService userDataService)
    {
        this.rankingService = rankingService;
        this.userDataService = userDataService;
    }

    @GetMapping("global")
    public LoadGlobalRankingData loadGlobalRankingData(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size)
    {
//        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
//        final long userId = user.getUserId();
//        final UserData userData = userDataService.getUserData(userId);
        final LoadGlobalRankingData loadedEntries = rankingService.loadGlobalRankingEntries(pageNumber, size);
        return loadedEntries;
//        final int userPositionInRanking = rankingService.getUserPositionInRanking(userId);
//        return new LoadGlobalRankingResponse(loadedEntries, userPositionInRanking, userData.eloPoints());
    }
}
