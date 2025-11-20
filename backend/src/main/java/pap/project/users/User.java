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
    /**
     * Setting strategy equal {@link GenerationType#IDENTITY} to stop hibernate from generating gaps in DB.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String hashedPassword;

    protected User() {}

    public User(@NonNull String username, @NonNull String email, @NonNull String hashedPassword)
    {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
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

    public @NonNull String getEmail()
    {
        return Objects.requireNonNull(email);
    }

    public @NonNull String getHashedPassword()
    {
        return Objects.requireNonNull(hashedPassword);
    }
}
