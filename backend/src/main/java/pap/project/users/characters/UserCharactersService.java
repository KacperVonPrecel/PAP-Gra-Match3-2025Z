package pap.project.users.characters;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.users.characters.model.CharacterStats;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.characters.model.controller.CharacterData;

import java.util.*;

@Service
public class UserCharactersService
{
    /**
     * It is guaranteed that this map contains all characters type in it.
     * This map is unmodifiable.
     */
    private static final Map<CharacterType, CharacterStats> CHARACTERS_STATS;
    static
    {
        final Map<CharacterType, CharacterStats> map = new EnumMap<>(CharacterType.class);
        for (CharacterType type : CharacterType.values())
        {
            map.put(type, new CharacterStats() {
                @Override
                public int getDamage(int level)
                {
                    return 100 * level;
                }

                @Override
                public int getHealth(int level)
                {
                    return 100 * level;
                }

                @Override
                public @NonNull OptionalInt getRequiredCopiesForNextLevel(int level)
                {
                    return OptionalInt.of(10 * level);
                }
            });
        }
        CHARACTERS_STATS = Collections.unmodifiableMap(map);

        if (CHARACTERS_STATS.size() != CharacterType.values().length)
            throw new IllegalStateException("Characters stats doesn't have initialized all characters");
    }

    /**
     * @return unmodifiable list.
     */
    public @NonNull List<CharacterData> createCharactersData(@NonNull List<UserCharacter> characters)
    {
        return characters.stream().map(this::createCharacterData).toList();
    }

    public @NonNull CharacterData createEmptyCharacterData(@NonNull CharacterType type)
    {
        final CharacterStats stats = getCharacterStats(type);
        return new CharacterData(type, stats.getDamage(1), stats.getHealth(1), 1, stats.getRequiredCopiesForNextLevel(1), 0);
    }

    public @NonNull CharacterData createCharacterData(@NonNull UserCharacter character)
    {
        final CharacterType type = character.getCharacterType();
        final int level = character.getLevel();
        final CharacterStats stats = getCharacterStats(type);
        return new CharacterData(type, stats.getDamage(level), stats.getHealth(level), level,
                stats.getRequiredCopiesForNextLevel(level), character.getCopiesCount());
    }

    private @NonNull CharacterStats getCharacterStats(@NonNull CharacterType characterType)
    {
        return Objects.requireNonNull(CHARACTERS_STATS.get(characterType));
    }
}
