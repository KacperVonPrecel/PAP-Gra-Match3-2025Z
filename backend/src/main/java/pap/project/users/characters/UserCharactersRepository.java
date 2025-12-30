package pap.project.users.characters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import pap.project.users.characters.model.CharacterType;

import java.util.List;
import java.util.Set;

@Repository
public interface UserCharactersRepository extends JpaRepository<UserCharacter, Long>
{
    @NonNull List<UserCharacter> findAllByUserId(long userId);
    @NonNull UserCharacter findById(long id);
    List<UserCharacter> findByUserIdAndCharacterTypeIn(long userId, Set<CharacterType> types);
}