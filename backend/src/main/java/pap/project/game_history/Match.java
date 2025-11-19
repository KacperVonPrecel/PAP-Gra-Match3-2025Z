package pap.project.game_history;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.OptionalLong;

@Entity
public class Match {
    @Id
    @GeneratedValue
    private Long id;

    private Long winnerId;
    private Long loserId;
    private Long finishTime;

    private Integer winnerEloChange;
    private Integer loserEloChange;

    protected Match() {}

    public Match(
            @NonNull Long winnerId,
            @NonNull Long loserId,
            @NonNull Long finishTime,
            @NonNull Integer winnerEloChange,
            @NonNull Integer loserEloChange)
    {
        this.winnerId = Objects.requireNonNull(winnerId);
        this.loserId = Objects.requireNonNull(loserId);
        this.finishTime = Objects.requireNonNull(finishTime);
        this.winnerEloChange = Objects.requireNonNull(winnerEloChange);
        this.loserEloChange = Objects.requireNonNull(loserEloChange);
    }

    public @NonNull OptionalLong getId()
    {
        return id == null ? OptionalLong.empty() : OptionalLong.of(id);
    }

    public @NonNull Long getWinnerId()
    {
        return winnerId;
    }

    public @NonNull Long getLoserId()
    {
     return loserId;
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
