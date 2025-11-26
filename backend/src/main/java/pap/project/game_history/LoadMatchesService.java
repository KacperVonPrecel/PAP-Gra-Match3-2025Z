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
import java.util.OptionalLong;

@Service
public class LoadMatchesService {
    private static final int FIRST_PAGE = 0;

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
            boolean isPlayerWinner = match.getWinner().getId().equals(OptionalLong.of(loadRequest.userId()));
            final User player = (isPlayerWinner) ? match.getWinner() : match.getLoser();
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
