package pap.project.game_history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchCharactersRepository extends JpaRepository<MatchCharacters, Long>
{
    @NonNull MatchCharacters findByMatchId(@NonNull Long matchId);
}
