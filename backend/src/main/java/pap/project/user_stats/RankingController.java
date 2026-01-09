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

//    @GetMapping("global")
//    public LoadGlobalRankingResponse loadGlobalRankingData(
//            @NonNull Authentication authentication,
//            @RequestParam(defaultValue = "0") int pageNumber,
//            @RequestParam(defaultValue = "10") int pageSize)
//    {
//        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
//        final long userId = user.getUserId();
//        final UserData userData = userDataService.getUserData(userId);
//        final LoadGlobalRankingData loadedEntries = rankingService.loadGlobalRankingEntries(pageNumber, pageSize);
//        final int userPositionInRanking = rankingService.getUserPositionInRanking(userId);
//        return new LoadGlobalRankingResponse(loadedEntries, userPositionInRanking, userData.eloPoints());
//    }
}
