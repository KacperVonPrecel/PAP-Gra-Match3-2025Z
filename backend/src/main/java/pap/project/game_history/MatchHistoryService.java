package pap.project.game_history;

import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pap.project.game_history.model.MatchFromHistoryData;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchHistoryService
{
    private final @NonNull MatchRepository matchRepository;
    private final @NonNull UserStatsRepository userStatsRepository;
    private final @NonNull UserRepository userRepository;

    public MatchHistoryService(@NonNull MatchRepository matchRepository, @NonNull UserStatsRepository userStatsRepository, @NonNull UserRepository userRepository)
    {
        this.matchRepository = matchRepository;
        this.userStatsRepository = userStatsRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param userId of user for which load history
     * @param latestRecordId latest match id from which to load records
     * @param size maximum number of records which should be returned by this function
     */
    public @Nullable LoadHistoryMatchesData loadMatches(long userId, long latestRecordId, int size)
    {
        if(!userRepository.existsById(userId)) return null;
        if(!matchRepository.existsById(latestRecordId)) return null;
        List<Match> loadedMatches = matchRepository.findMatchesBeforeRecordId(userId, latestRecordId, PageRequest.of(0, size + 1));
        boolean isMoreToLoad = loadedMatches.size() == size + 1;
        if (isMoreToLoad)
        {
            loadedMatches.removeLast();
        }

        List<MatchFromHistoryData> mappedMatches = loadedMatches.stream().map(match ->
        {
            boolean isPlayerWinner = match.getWinnerId() == userId;
            final User player = (isPlayerWinner) ? match.getWinner() : match.getLoser();
            final User opponent = (isPlayerWinner) ? match.getLoser() : match.getWinner();
            final long playerId = player.getId().orElseThrow();
            final long opponentId = opponent.getId().orElseThrow();

            return new MatchFromHistoryData(
                    playerId,
                    player.getUsername(),
                    opponentId,
                    opponent.getUsername(),
                    match.getFinishTime(),
                    (isPlayerWinner) ? match.getWinnerEloChange() : match.getLoserEloChange(),
                    (isPlayerWinner) ? match.getLoserEloChange() : match.getWinnerEloChange(),
                    userStatsRepository.findUserStatsByUserId(playerId).orElseThrow().getEloPoints(),
                    userStatsRepository.findUserStatsByUserId(opponentId).orElseThrow().getEloPoints(),
                    isPlayerWinner
            );
        }).toList();

        return new LoadHistoryMatchesData(mappedMatches, isMoreToLoad);
    }
}
