package pap.project.user_stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long>
{
    @NonNull Optional<UserStats> findUserStatsById(long id);
    @NonNull Optional<UserStats> findUserStatsByUserId(long userId);

    @Modifying (clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserStats u SET u.matchPlayed = ?1, u.matchWon = ?2, u.eloPoints = ?3, u.currency = ?4 WHERE u.userId = ?5")
    void updateUserStatsAfterGameEnd(int matchPlayed, int matchWon, int eloPoints, int currency, long id);
}
