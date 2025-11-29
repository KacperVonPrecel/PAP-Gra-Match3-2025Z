package pap.project.users;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

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
    private static final int STARTING_CURRENCY = 1000;
    private static final int STARTING_ELO_POINTS = 100;
    private static final int STARTING_TOTAL_GAMES = 0;
    private static final int STARTING_TOTAL_WINS = 0;


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
    @Column(name = "elo_points", nullable = false)
    private int eloPoints = STARTING_ELO_POINTS;
    @Column(name = "total_games", nullable = false)
    private int totalGames = STARTING_TOTAL_GAMES;
    @Column(name = "total_wins", nullable = false)
    private int totalWins = STARTING_TOTAL_WINS;
    @Column(name = "currency", nullable = false)
    private int currency = STARTING_CURRENCY;

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
//        this.eloPoints = STARTING_ELO_POINTS;
//        this.totalGames = STARTING_TOTAL_GAMES;
//        this.totalWins = STARTING_TOTAL_WINS;
//        this.currency = STARTING_CURRENCY;
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

    public int getEloPoints() {
        return eloPoints;
    }

    public int getTotalGames()
    {
        return totalGames;
    }

    public int getTotalWins()
    {
        return totalWins;
    }

    public int getCurrency()
    {
        return currency;
    }

    public void setEloPoints(@NonNull Integer eloPoints)
    {
        this.eloPoints = eloPoints;
    }

}