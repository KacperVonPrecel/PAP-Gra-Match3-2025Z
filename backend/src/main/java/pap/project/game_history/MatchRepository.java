package pap.project.game_history;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>
{
    @NonNull Optional<Match> findByMatchId(@NonNull Long matchId);
    boolean existsByMatchId(@NonNull Long matchId);

}
