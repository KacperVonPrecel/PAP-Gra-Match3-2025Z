package pap.project.game_history;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pap.project.game_history.controller.model.load.LoadRequest;
import pap.project.game_history.controller.model.load.MatchDTO;
import pap.project.game_history.controller.model.process.ProcessResult;
import pap.project.users.User;
import pap.project.users.UserRepository;

@Service
public class EndGameService {

    private static final String LOG_PREFIX = "endGameService";
    private static final Logger LOG = LoggerFactory.getLogger(EndGameService.class);

    private static final int ELO_DOWN = -10;
    private static final int ELO_UP = 20;
    private static final int CURRENCY_WINNER = 500;
    private static final int CURRENCY_LOSER = 200;

    private static final int FIRST_PAGE = 1;

    private final @NonNull MatchRepository matchRepository;
    private final @NonNull UserRepository userRepository;

    public EndGameService(@NonNull final MatchRepository matchRepository, @NonNull final UserRepository userRepository)
    {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    /**
     * @return information if match was successfully saved
     */
    @Transactional
    public @NonNull ProcessResult processEndGame(@NonNull String logPrefix, long winnerId, long loserId, long finishTime)
    {
        User winner = userRepository.findById(winnerId).get();
        User loser = userRepository.findById(loserId).get();
        logPrefix += "-" + LOG_PREFIX;
        final Match match = new Match(
                winner,
                loser,
                finishTime,
                ELO_UP,
                ELO_DOWN
        );
        try
        {
            final int eloUp = winner.getEloPoints() + ELO_UP;
            final int eloDown = loser.getEloPoints() + ELO_DOWN;
            final int currencyWinner = winner.getCurrency() + CURRENCY_WINNER;
            final int currencyLoser = loser.getCurrency() + CURRENCY_LOSER;
            this.userRepository.updateUserAfterEndGame(eloUp, currencyWinner, winnerId);
            this.userRepository.updateUserAfterEndGame(eloDown, currencyLoser, loserId);
            LOG.info("%s users %d and %d updated in database".formatted(logPrefix, winnerId, loserId));
        } catch (PersistenceException exp)
        {
            LOG.warn("%s error updating users: %d adn %d in database".formatted(logPrefix, winnerId, loserId), exp);
            return ProcessResult.FAILED;
        }
        try
        {
            matchRepository.save(match);
            LOG.info("%s match %s saved to database".formatted(logPrefix, match.getId()));
            return ProcessResult.SUCCESS;
        } catch (PersistenceException exp)
        {
            LOG.warn("%s match id: %s  error saving to database".formatted(logPrefix, match.getId()), exp);
            return ProcessResult.FAILED;
        }
    }

    public @NonNull Page<MatchDTO> loadMatches(@NonNull String logPrefix, @NonNull LoadRequest loadRequest)
    {
        Pageable pageable = PageRequest.of(FIRST_PAGE, loadRequest.size());

        Page<Match> matchPage = matchRepository.
    }

}
