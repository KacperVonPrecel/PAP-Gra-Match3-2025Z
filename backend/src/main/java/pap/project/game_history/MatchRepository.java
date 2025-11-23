package pap.project.game_history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>
{
    @NonNull Optional<Match> findByMatchId(@NonNull Long matchId);
    boolean existsByMatchId(@NonNull Long matchId);

    @Query("""
        SELECT m
        FROM Match m
        JOIN m.winner w
        JOIN m.loser l
        WHERE m.finishTime < :time
           AND (w.id = :userId OR l.id = :userId)
        """)
    Page<Match> findMatchesBeforeFinishTime(@Param("userId") long userId,@Param("time") long time, Pageable pageable);

}
