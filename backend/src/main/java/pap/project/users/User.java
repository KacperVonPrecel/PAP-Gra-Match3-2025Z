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
    private static final Integer STARTING_ELO_POINTS = 100;
    private static final Integer STARTING_TOTAL_GAMES = 0;
    private static final Integer STARTING_TOTAL_WINS = 0;

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String email;
    private String hashedPassword;

    private int eloPoints;
    private int totalGames;
    private int totalWins;
    private int currency;

    protected User() {}

    public User(@NonNull String username, @NonNull String email, @NonNull String hashedPassword)
    {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.hashedPassword = Objects.requireNonNull(hashedPassword);
        this.eloPoints = STARTING_ELO_POINTS;
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

    public @NonNull Integer getEloPoints() {
        return eloPoints;
    }

    public @NonNull Integer getTotalGames()
    {
        return totalGames;
    }

    public @NonNull Integer getTotalWins()
    {
        return totalWins;
    }

    public @NonNull Integer getCurrency()
    {
        return currency;
    }
}
