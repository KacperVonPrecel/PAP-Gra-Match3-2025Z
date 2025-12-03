package pap.project.game_history;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long>
{
    @Query("""
        SELECT m
        FROM Match m
        JOIN m.winner w
        JOIN m.loser l
        WHERE m.id <= :latestRecordId
           AND (w.id = :userId OR l.id = :userId)
        ORDER BY m.id DESC
        """)
    @NonNull List<Match> findMatchesBeforeRecordId(@Param("userId") long userId, @Param("latestRecordId") long latestRecordId, @NonNull Pageable pageable);
}
