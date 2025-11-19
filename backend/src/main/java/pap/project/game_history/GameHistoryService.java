package pap.project.game_history;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import pap.project.users.User;

@Service
public class GameHistoryService {

    private static final String LOG_PREFIX = "-gameHistoryService";
    private static final Logger LOG = LoggerFactory.getLogger(GameHistoryService.class);

    private final @NonNull MatchRepository matchRepository;

    public GameHistoryService(@NonNull MatchRepository matchRepository)
    {
        this.matchRepository = matchRepository;
    }

    /**
     * @return information if match was successfully saved
     */
    public @NonNull SaveResult saveMatch(
            @NonNull User winner,
            @NonNull User loser,
            @NonNull Long finishTime,
            @NonNull Integer winnerEloChange,
            @NonNull Integer loserEloChange
        )
    {
        final Match match = new Match(
                winner,
                loser,
                finishTime,
                winnerEloChange,
                loserEloChange
        );
        try
        {
            matchRepository.save(match);
            LOG.info("%s match %s saved to database".formatted(LOG_PREFIX, match.getId()));
            return SaveResult.SUCCESS;
        } catch (PersistenceException exit)
        {
            LOG.warn("%s match id: %s  error saving to database".formatted(LOG_PREFIX, match.getId()), exit);
            return SaveResult.FAILED;
        }
    }

}
