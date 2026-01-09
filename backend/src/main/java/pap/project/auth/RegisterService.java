package pap.project.auth;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pap.project.auth.model.RegisterResult;
import pap.project.auth.model.controller.register.RegisterRequest;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserRepository;

@Service
public class RegisterService
{
    private static final String LOG_PREFIX = "registerService";
    private static final Logger LOG = LoggerFactory.getLogger(RegisterService.class);

    private final @NonNull UserRepository userRepository;
    private final @NonNull PasswordEncoder passwordEncoder;
    private final @NonNull UserStatsRepository userStatsRepository;

    public RegisterService(@NonNull UserRepository userRepository, @NonNull PasswordEncoder passwordEncoder, @NonNull UserStatsRepository userStatsRepository)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userStatsRepository = userStatsRepository;
    }

    /**
     * @return information if user was successfully registered.
     */
    @Transactional
    public @NonNull RegisterResult registerUser(@NonNull String logPrefix, @NonNull RegisterRequest registerRequest)
    {
        logPrefix += "-" + LOG_PREFIX;
        if (userRepository.existsByUsername(registerRequest.username()))
        {
            LOG.info("%s user with this username exists".formatted(logPrefix));
            return RegisterResult.USERNAME_REPEATED;
        }
        if (userRepository.existsByEmail(registerRequest.email()))
        {
            LOG.info("%s user with this email exists".formatted(logPrefix));
            return RegisterResult.EMAIL_REPEATED;
        }
        final String encodedPassword = passwordEncoder.encode(registerRequest.password());
        LOG.info("%s encoded password %s".formatted(logPrefix, encodedPassword));
        final User userToSave = new User(registerRequest.username(), registerRequest.email(), encodedPassword);
        try
        {
            userRepository.saveAndFlush(userToSave);
            final UserStats userStatsToSave = new UserStats(userToSave.getId().orElseThrow());
            userStatsRepository.save(userStatsToSave);
            LOG.info("%s user saved to database".formatted(logPrefix));
            return RegisterResult.REGISTERED;
        } catch (PersistenceException wyj)
        {
            // XXX chyba łapanie wyjątku jest złe, bo chyba zostanie to zcommitowane do bazy danych mimo jednego błędu zapisu.
            LOG.warn("%s error saving to database".formatted(logPrefix), wyj);
            return RegisterResult.DATABASE_ERROR;
        }
    }
}
