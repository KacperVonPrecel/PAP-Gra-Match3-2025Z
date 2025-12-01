package pap.project.user_stats;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import pap.project.users.User;

import java.util.OptionalLong;

@Entity
@Table (
        name = "user_stats"
)
public class UserStats {
    private static final int STARTING_CURRENCY = 1000;
    private static final int STARTING_ELO_POINTS = 100;
    private static final int STARTING_TOTAL_GAMES = 0;
    private static final int STARTING_TOTAL_WINS = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "elo_points", nullable = false)
    private int eloPoints = STARTING_ELO_POINTS;
    @Column(name = "total_games", nullable = false)
    private int matchPlayed = STARTING_TOTAL_GAMES;
    @Column(name = "total_wins", nullable = false)
    private int matchWon = STARTING_TOTAL_WINS;
    @Column(name = "currency", nullable = false)
    private int currency = STARTING_CURRENCY;

    protected UserStats() {}

    public UserStats(long userId) {
        this.userId = userId;
    }

    public @NonNull OptionalLong getId()
    {
        return id != null ? OptionalLong.of(id) : OptionalLong.empty();
    }

    public @NonNull OptionalLong getUserId()
    {
        return user != null ? OptionalLong.of(userId) : OptionalLong.empty();
    }

    public User getUser()
    {
        return user;
    }
    public int getEloPoints()
    {
        return eloPoints;
    }

    public int getMatchPlayed()
    {
        return matchPlayed;
    }

    public int getMatchWon()
    {
        return matchWon;
    }

    public int getCurrency()
    {
        return currency;
    }
}
