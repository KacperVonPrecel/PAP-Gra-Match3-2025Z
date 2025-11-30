package pap.project.users.characters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCharactersRepository extends JpaRepository<UserCharacter, Long>
{
    @NonNull List<UserCharacter> findAllByUserId(long userId);
}