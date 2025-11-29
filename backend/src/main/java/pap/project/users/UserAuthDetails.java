package pap.project.users;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserAuthDetails implements UserDetails
{
    private final @NonNull String username;
    private final @NonNull String hashedPassword;
    private final long userId;

    public UserAuthDetails(@NonNull String username, @NonNull String hashedPassword, long userId)
    {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.userId = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of();
    }

    @Override
    public @NonNull String getPassword()
    {
        return hashedPassword;
    }

    @Override
    public @NonNull String getUsername()
    {
        return username;
    }

    public long getUserId()
    {
        return userId;
    }
}
