package pap.project.game_history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.game_history.controller.model.load.LoadRequest;
import pap.project.game_history.controller.model.load.MatchProjectionForController;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

@Service
public class LoadMatchesService {
    private static final int FIRST_PAGE = 1;

    private final @NonNull MatchRepository matchRepository;
    private final @NonNull UserRepository userRepository;

    public LoadMatchesService(MatchRepository matchRepository, UserRepository userRepository)
    {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    public @NonNull List<MatchProjectionForController> loadMatches(@NonNull LoadRequest loadRequest)
    {
        final PageRequest pageReq = PageRequest.of(FIRST_PAGE, loadRequest.size(), Sort.by("finishTime").descending());

        Page<Match> matchPage = matchRepository.findMatchesBeforeFinishTime(loadRequest.userId(), loadRequest.latestRecordTime(), pageReq);

        return matchPage.map(match ->
        {
            final User player = userRepository.findById(loadRequest.userId()).get();
            boolean isPlayerWinner = match.getWinner() == player;
            final User opponent = (isPlayerWinner) ? match.getLoser() : match.getWinner();

            return new MatchProjectionForController(
                    player.getId(),
                    player.getUsername(),
                    opponent.getId(),
                    opponent.getUsername(),
                    match.getFinishTime(),
                    (isPlayerWinner) ? match.getWinnerEloChange() : match.getLoserEloChange(),
                    (isPlayerWinner) ? match.getLoserEloChange() : match.getWinnerEloChange(),
                    player.getEloPoints(),
                    opponent.getEloPoints(),
                    isPlayerWinner
            );
        }).getContent();
    }
}
