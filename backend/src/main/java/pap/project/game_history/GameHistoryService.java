package pap.project.game_history;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pap.project.game_history.controller.model.save.SaveResult;
import pap.project.game_history.controller.model.save.SaveRequest;
import pap.project.users.User;
import pap.project.users.UserRepository;

@Service
public class GameHistoryService {

    private static final String LOG_PREFIX = "-gameHistoryService";
    private static final Logger LOG = LoggerFactory.getLogger(GameHistoryService.class);

    private static final Integer ELO_DOWN = -10;
    private static final Integer ELO_UP = 20;
    private static final Integer CURRENCY_WINNER = 500;
    private static final Integer CURRENCY_LOSER = 200;

    private final @NonNull MatchRepository matchRepository;
    private final UserRepository userRepository;

    public GameHistoryService(@NonNull MatchRepository matchRepository, UserRepository userRepository)
    {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    /**
     * @return information if match was successfully saved
     */
    @Transactional
    public @NonNull SaveResult saveMatch(@NonNull String logPrefix, @NonNull SaveRequest saveRequest)
    {
        logPrefix += "-" + LOG_PREFIX;
        final Match match = new Match(
                userRepository.findById(saveRequest.winnerId()).get(),
                userRepository.findById(saveRequest.loserId()).get(),
                saveRequest.finishTime(),
                ELO_UP,
                ELO_DOWN
        );
        try
        {
            updateMatch(saveRequest);
            LOG.info("%s users %d and %d updated in database".formatted(logPrefix, saveRequest.winnerId(), saveRequest.loserId()));
        } catch (PersistenceException exit)
        {
            LOG.warn("%s error updating users: %d adn %d in database".formatted(logPrefix, saveRequest.winnerId(), saveRequest.loserId()), exit);
            return SaveResult.FAILED;
        }
        try
        {
            matchRepository.save(match);
            LOG.info("%s match %s saved to database".formatted(logPrefix, match.getMatchId()));
            return SaveResult.SUCCESS;
        } catch (PersistenceException exit)
        {
            LOG.warn("%s match id: %s  error saving to database".formatted(logPrefix, match.getMatchId()), exit);
            return SaveResult.FAILED;
        }
    }

    private void updateMatch(@NonNull SaveRequest saveRequest)
    {
        User winner = userRepository.findById(saveRequest.winnerId()).get();
        User loser = userRepository.findById(saveRequest.loserId()).get();

        winner.setRank(winner.getRank() + ELO_UP);
        winner.incrementTotalGames();
        winner.incrementTotalWins();
        winner.setCurrency(winner.getCurrency() + CURRENCY_WINNER);

        loser.setRank(loser.getRank() + ELO_DOWN);
        loser.incrementTotalGames();
        loser.setCurrency(loser.getCurrency() + CURRENCY_LOSER);

    }
/**
 * "xxx do it after PROB"
 * public @NonNull Page<Match> loadMatches(@NonNull String logPrefix, @NonNull LoadRequest loadRequest)
 * {}
*/
}
