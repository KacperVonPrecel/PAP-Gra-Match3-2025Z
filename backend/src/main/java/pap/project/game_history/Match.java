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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "winner_id", referencedColumnName = "id")
    private User winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "loser_id", referencedColumnName = "id")
    private User loser;

    private long finishTime;

    private int winnerEloChange;
    private int loserEloChange;

    protected Match() {}

    public Match(
            @NonNull User winner,
            @NonNull User loser,
            long finishTime,
            int winnerEloChange,
            int loserEloChange)
    {
        this.winner = Objects.requireNonNull(winner);
        this.loser = Objects.requireNonNull(loser);
        this.finishTime = finishTime;
        this.winnerEloChange = winnerEloChange;
        this.loserEloChange = loserEloChange;
    }

    public @NonNull OptionalLong getId()
    {
        return id == null ? OptionalLong.empty() : OptionalLong.of(id);
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
