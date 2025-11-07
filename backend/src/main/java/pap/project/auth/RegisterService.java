package pap.project.auth;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pap.project.auth.model.RegisterResult;
import pap.project.auth.model.controller.register.RegisterRequest;
import pap.project.users.User;
import pap.project.users.UserRepository;

@Service
public class RegisterService
{
    private static final String LOG_PREFIX = "registerService";
    private static final Logger LOG = LoggerFactory.getLogger(RegisterService.class);

    private final @NonNull UserRepository userRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public RegisterService(@NonNull UserRepository userRepository, @NonNull PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @return information if user was successfully registered.
     */
    public @NonNull RegisterResult registerUser(@NonNull String logPrefix, @NonNull RegisterRequest registerRequest)
    {
        logPrefix += "-" + LOG_PREFIX;
        if (userRepository.existsByUsername(registerRequest.username()))
        {
            LOG.info("%s user with this username exists".formatted(logPrefix));
            return RegisterResult.USERNAME_REPEATED;
        }
        final String encodedPassword = passwordEncoder.encode(registerRequest.password());
        LOG.info("%s encoded password %s".formatted(logPrefix, encodedPassword));
        final User userToSave = new User(registerRequest.username(), encodedPassword);
        try
        {
            userRepository.save(userToSave);
            LOG.info("%s user saved to database".formatted(logPrefix));
            return RegisterResult.REGISTERED;
        } catch (PersistenceException wyj)
        {
            LOG.warn("%s error saving to database".formatted(logPrefix), wyj);
            return RegisterResult.DATABASE_ERROR;
        }
    }
}
