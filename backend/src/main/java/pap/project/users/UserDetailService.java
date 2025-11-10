package pap.project.users;

import org.h2.engine.UserBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService
{
    private final @NonNull UserRepository userRepository;

    public UserDetailService(@NonNull UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException
    {
        final Optional<User> foundedUser = userRepository.findByUsername(username);
        if (foundedUser.isEmpty())
            throw new UsernameNotFoundException("User not founded: " + username);
        final User user = foundedUser.get();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getHashedPassword())
                .build();
    }
}
