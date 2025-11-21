package pap.project.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    @NonNull Optional<User> findByUsername(@NonNull String username);
    boolean existsByUsername(@NonNull String username);
    boolean existsByEmail(@NonNull String email);

    @Modifying
    @Query("update User u set u.totalGames = u.totalGames + 1, u.totalWins = u.totalWins + 1, u.eloPoints = ?1, u.currency = ?2 where u.id = ?3")
    void updateUserAfterEndGame(int eloPointsChange, int currencyChange, long id);

}
