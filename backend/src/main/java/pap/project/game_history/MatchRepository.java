package pap.project.game_history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long>
{
    @Query("""
        SELECT m
        FROM Match m
        JOIN m.winner w
        JOIN m.loser l
        WHERE m.finishTime <= :time
           AND (w.id = :userId OR l.id = :userId)
        """)
    Page<Match> findMatchesBeforeFinishTime(@Param("userId") long userId,@Param("time") long time, Pageable pageable);
}
