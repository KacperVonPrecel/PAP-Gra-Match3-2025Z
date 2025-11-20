package pap.project.game_history;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pap.project.game_history.controller.SaveRequest;
import pap.project.users.User;
import pap.project.users.UserRepository;

@Service
public class GameHistoryService {

    private static final String LOG_PREFIX = "-gameHistoryService";
    private static final Logger LOG = LoggerFactory.getLogger(GameHistoryService.class);

    private static final Integer ELO_DOWN = -10;
    private static final Integer ELO_UP = 20;

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
        final Match match = new Match(
                userRepository.findById(saveRequest.winnerId()).get(),
                userRepository.findById(saveRequest.loserId()).get(),
                saveRequest.finishTime(),
                ELO_UP,
                ELO_DOWN
        );
        try
        {
            matchRepository.save(match);
            LOG.info("%s match %s saved to database".formatted(LOG_PREFIX, match.getmatchId()));
            return SaveResult.SUCCESS;
        } catch (PersistenceException exit)
        {
            LOG.warn("%s match id: %s  error saving to database".formatted(LOG_PREFIX, match.getmatchId()), exit);
            return SaveResult.FAILED;
        }
    }

}
