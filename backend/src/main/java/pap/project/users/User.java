package pap.project.users;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.OptionalLong;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        }
)
public class User
{
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String hashedPassword;

    protected User() {}

    public User(@NonNull String username, @NonNull String hashedPassword)
    {
        this.username = Objects.requireNonNull(username);
        this.hashedPassword = Objects.requireNonNull(hashedPassword);
    }

    public @NonNull OptionalLong getId()
    {
        return id != null ? OptionalLong.of(id) : OptionalLong.empty();
    }

    public @NonNull String getUsername()
    {
        return Objects.requireNonNull(username);
    }

    public @NonNull String getHashedPassword()
    {
        return Objects.requireNonNull(hashedPassword);
    }
}
