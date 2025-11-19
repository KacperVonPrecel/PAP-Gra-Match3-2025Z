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

    @ManyToOne
    @JoinColumn (name = "winner_id")
    private User winner;

    @ManyToOne
    @JoinColumn (name = "loser_id")
    private User loser;

    private Long finishTime;

    private Integer winnerEloChange;
    private Integer loserEloChange;

    protected Match() {}

    public Match(
            @NonNull User winner,
            @NonNull User loser,
            @NonNull Long finishTime,
            @NonNull Integer winnerEloChange,
            @NonNull Integer loserEloChange)
    {
        this.winner = Objects.requireNonNull(winner);
        this.loser = Objects.requireNonNull(loser);
        this.finishTime = Objects.requireNonNull(finishTime);
        this.winnerEloChange = Objects.requireNonNull(winnerEloChange);
        this.loserEloChange = Objects.requireNonNull(loserEloChange);
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

    public  @NonNull Long getFinishTime()
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
