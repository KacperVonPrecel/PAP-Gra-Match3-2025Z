package pap.project.game_history;

import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.game_history.model.MatchFromHistoryData;
import pap.project.users.User;

import java.util.List;

@Service
public class MatchHistoryService
{
    private final @NonNull MatchRepository matchRepository;

    public MatchHistoryService(@NonNull MatchRepository matchRepository)
    {
        this.matchRepository = matchRepository;
    }

    /**
     * @param userId of user for which load history
     * @param latestRecordTime XXXK it should be last id of match loaded instead of time
     * @param size maximum number of records which should be returned by this function
     */
    public @NonNull List<MatchFromHistoryData> loadMatches(long userId, long latestRecordTime, int size)
    {
        // XXXK Should load loadHistoryRequest.size() + 1, and check if records count == loadHistoryRequest.size() + 1. If it equals it should remove last element from list and
        // return info about there is more records to load.
        return matchRepository.findMatchesBeforeFinishTime(userId, latestRecordTime, PageRequest.of(0, size))
                .stream().map(match ->
        {
            boolean isPlayerWinner = match.getWinnerId() == userId;
            final User player = (isPlayerWinner) ? match.getWinner() : match.getLoser();
            final User opponent = (isPlayerWinner) ? match.getLoser() : match.getWinner();

            return new MatchFromHistoryData(
                    player.getId().orElseThrow(),
                    player.getUsername(),
                    opponent.getId().orElseThrow(),
                    opponent.getUsername(),
                    match.getFinishTime(),
                    (isPlayerWinner) ? match.getWinnerEloChange() : match.getLoserEloChange(),
                    (isPlayerWinner) ? match.getLoserEloChange() : match.getWinnerEloChange(),
                    player.getEloPoints(),
                    opponent.getEloPoints(),
                    isPlayerWinner
            );
        }).toList();
    }
}
