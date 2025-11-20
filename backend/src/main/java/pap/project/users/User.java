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
    private static final Integer STARTING_CURRENCY = 1000;
    private static final Integer STARTING_RANK = 100;
    private static final Integer STARTING_TOTAL_GAMES = 0;
    private static final Integer STARTING_TOTAL_WINS = 0;

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String email;
    private String hashedPassword;

    private Integer rank;
    private Integer totalGames;
    private Integer totalWins;
    private Integer currency;

    protected User() {}

    public User(@NonNull String username, @NonNull String email, @NonNull String hashedPassword)
    {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.hashedPassword = Objects.requireNonNull(hashedPassword);
        this.rank = STARTING_RANK;
        this.totalGames = STARTING_TOTAL_GAMES;
        this.totalWins = STARTING_TOTAL_WINS;
        this.currency = STARTING_CURRENCY;
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

    public @NonNull Integer getRank() {
        return Objects.requireNonNull(rank);
    }

    public @NonNull Integer getTotalGames()
    {
        return Objects.requireNonNull(totalGames);
    }

    public @NonNull Integer getTotalWins()
    {
        return Objects.requireNonNull(totalWins);
    }

    public @NonNull Integer getCurrency()
    {
        return Objects.requireNonNull(currency);
    }
}
