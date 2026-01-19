package pap.project.users;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import pap.project.user_stats.UserStats;

import java.util.Objects;
import java.util.OptionalLong;



@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
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

    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "hashed_password")
    private String hashedPassword;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserStats userStats;

    /**
     * Only for use in tests if it necessary to have userId.
     */
    public static @NonNull User createUserForTests(long id, @NonNull String username, @NonNull String email, @NonNull String hashedPassword)
    {
        final User user = new User(username, email, hashedPassword);
        user.id = id;
        return user;
    }

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

    public @NonNull UserStats getUserStats()
    {
    return userStats;
    }

    public void setUserStats(UserStats userStats)
    {
        this.userStats = userStats;
    }

}