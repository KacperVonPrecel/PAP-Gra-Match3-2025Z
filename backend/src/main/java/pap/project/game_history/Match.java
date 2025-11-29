package pap.project.game_history;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import pap.project.users.User;
import java.util.Objects;
import java.util.OptionalLong;

@Entity
@Table (
        name = "games_history"
)
public class Match {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "winner_id")
    private long winnerId;
    @Column(name = "loser_id")
    private long loserId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "winner_id", insertable = false, updatable = false)
    private User winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "loser_id", insertable = false, updatable = false)
    private User loser;

    @Column(name = "finish_time")
    private long finishTime;

    @Column(name = "winner_elo_change")
    private int winnerEloChange;
    @Column(name = "loser_elo_change")
    private int loserEloChange;

    public static @NonNull Match createMatchForTest(
            @NonNull User winner,
            @NonNull User loser,
            long finishTime,
            int winnerEloChange,
            int loserEloChange
    )
    {
        final Match match = new Match(winner.getId().orElseThrow(), loser.getId().orElseThrow(), finishTime, winnerEloChange, loserEloChange);
        match.winner = winner;
        match.loser = loser;
        return match;
    }
    protected Match() {}

    public Match(
            long winnerId,
            long loserId,
            long finishTime,
            int winnerEloChange,
            int loserEloChange)
    {
        this.winnerId = winnerId;
        this.loserId = loserId;
        this.finishTime = finishTime;
        this.winnerEloChange = winnerEloChange;
        this.loserEloChange = loserEloChange;
    }

    public @NonNull OptionalLong getId()
    {
        return id != null ? OptionalLong.of(id) : OptionalLong.empty();
    }

    public long getWinnerId()
    {
        return winnerId;
    }

    public long getLoserId()
    {
        return loserId;
    }

    public @NonNull User getWinner()
    {
        return winner;
    }

    public @NonNull User getLoser()
    {
        return loser;
    }

    public @NonNull Long getFinishTime()
    {
        return finishTime;
    }

    public @NonNull Integer getWinnerEloChange()
    {
        return winnerEloChange;
    }

    public @NonNull Integer getLoserEloChange()
    {
        return loserEloChange;
    }

}
