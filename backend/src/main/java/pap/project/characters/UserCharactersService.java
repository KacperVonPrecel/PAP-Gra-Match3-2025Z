package pap.project.characters;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.characters.model.CharacterType;
import pap.project.characters.model.controller.CharacterData;

import java.util.*;

@Service
public class UserCharactersService
{
    /**
     * @return unmodifiable list.
     */
    public @NonNull List<CharacterData> createCharactersData(@NonNull List<UserCharacter> characters)
    {
        return characters.stream().map(this::createCharacterData).toList();
    }

    public @NonNull CharacterData createEmptyCharacterData(@NonNull CharacterType type)
    {
        final Character stats = type.character;
        return new CharacterData(type, stats.getDamage(1), stats.getHealth(1), 1, stats.getRequiredCopiesForNextLevel(1), 0);
    }

    public @NonNull CharacterData createCharacterData(@NonNull UserCharacter character)
    {
        final CharacterType type = character.getCharacterType();
        final int level = character.getLevel();
        final Character stats = type.character;
        return new CharacterData(type, stats.getDamage(level), stats.getHealth(level), level,
                stats.getRequiredCopiesForNextLevel(level), character.getCopiesCount());
    }
}
