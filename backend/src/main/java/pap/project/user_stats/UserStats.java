package pap.project.user_stats;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.users.User;
import pap.project.characters.model.CharacterType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (
        name = "user_stats",
        indexes = {
                @Index(name = "idx_user_stats_elo_points", columnList = "elo_points, total_wins")
        }
)
public class UserStats {
    private static final int STARTING_CURRENCY = 1000;
    private static final int STARTING_ELO_POINTS = 100;
    private static final int STARTING_TOTAL_GAMES = 0;
    private static final int STARTING_TOTAL_WINS = 0;

    @Id
    @Column (name = "user_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "elo_points", nullable = false)
    private int eloPoints = STARTING_ELO_POINTS;
    @Column(name = "total_games", nullable = false)
    private int matchPlayed = STARTING_TOTAL_GAMES;
    @Column(name = "total_wins", nullable = false)
    private int matchWon = STARTING_TOTAL_WINS;
    @Column(name = "currency", nullable = false)
    private int currency = STARTING_CURRENCY;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<CharacterType> activeTeam = new ArrayList<>();

    protected UserStats() {}

    public UserStats(@NonNull User user) {
        this.user = user;
    }

    public long getId()
    {
        return id;
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

    public @Nullable List<CharacterType> getActiveTeam()
    {
        return activeTeam.size() != 3 ? null : activeTeam;
    }

}
