package pap.project.user_stats;

import jakarta.persistence.*;
import pap.project.users.User;

import java.util.ArrayList;
import java.util.Collections;
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

//    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn (name = "user_id", insertable = false, updatable = false)
//    private User user;

    @Column(name = "elo_points", nullable = false)
    private int eloPoints = STARTING_ELO_POINTS;
    @Column(name = "total_games", nullable = false)
    private int matchPlayed = STARTING_TOTAL_GAMES;
    @Column(name = "total_wins", nullable = false)
    private int matchWon = STARTING_TOTAL_WINS;
    @Column(name = "currency", nullable = false)
    private int currency = STARTING_CURRENCY;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_active_team", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "active_team_ids", nullable = false)
    private List<Long> activeTeamIds = new ArrayList<>();

    protected UserStats() {}

    public UserStats(long userId) {
        this.id = userId;
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

    public List<Long> getActiveTeamIds()
    {
//        return Collections.emptyList();
        return activeTeamIds;
    }

}
