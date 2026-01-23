package pap.project.user_stats;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import pap.project.user_stats.model.RankingEntry;
import pap.project.characters.model.CharacterType;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long>
{
    @NonNull Optional<UserStats> findUserStatsById(long userId);

    @Modifying (clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserStats u SET u.matchPlayed = ?1, u.matchWon = ?2, u.eloPoints = ?3, u.currency = ?4 WHERE u.id = ?5")
    void updateUserStatsAfterGameEnd(int matchPlayed, int matchWon, int eloPoints, int currency, long id);

    @Modifying (clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserStats u SET u.currency = ?1 WHERE u.id = ?2")
    void updateUserStatsAfterDrawing(int currency, long id);

    @Modifying (clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserStats u SET u.activeTeam = ?1 WHERE u.id = ?2")
    void updateUserStatsActiveTeam(@NonNull List<CharacterType> activeTeamIds, long id);

    @Query("SELECT COUNT(u) + 1 FROM UserStats u WHERE u.eloPoints > (SELECT s.eloPoints FROM UserStats s WHERE s.id = ?1)")
    int calculateRankPositionById(long userId);

    @Query("SELECT new pap.project.user_stats.model.RankingEntry(u.id, u.user.username, u.eloPoints) FROM UserStats u ORDER BY u.eloPoints DESC")
    @NonNull
    Page<RankingEntry> getGlobalRanking(@NonNull Pageable pageable);
}
