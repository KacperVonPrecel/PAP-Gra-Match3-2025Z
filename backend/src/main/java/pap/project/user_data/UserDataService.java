package pap.project.user_data;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.user_data.model.controller.StartingDataResponse;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersService;
import pap.project.users.characters.model.CharacterStats;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.characters.model.controller.CharacterData;

import java.util.*;

@Service
public class UserDataService
{
    //XXX it isn't valid place for it.
    //XXX create another service/ or public method to be able to mock it for tests
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

    private final @NonNull UserCharactersService userCharactersService;

    public UserDataService(@NonNull UserCharactersService userCharactersService)
    {
        this.userCharactersService = userCharactersService;
    }

    public @NonNull StartingDataResponse getUserData(long userId)
    {
        final List<UserCharacter> userCharacters = userCharactersService.getUserCharacters(userId);
        final int money = 100; //XXX load from DB.
        return new StartingDataResponse(createCharactersData(userCharacters), money);
    }

    /**
     * @return unmodifiable list.
     */
    private @NonNull List<CharacterData> createCharactersData(@NonNull List<UserCharacter> characters)
    {
        return characters.stream().map(this::createCharacterData).toList();
    }

    private @NonNull CharacterData createCharacterData(@NonNull UserCharacter character)
    {
        final CharacterType type = character.getCharacterType();
        final int level = character.getLevel();
        final CharacterStats stats = CHARACTERS_STATS.get(type);
        return new CharacterData(type, stats.getDamage(level), stats.getHealth(level), level,
                stats.getRequiredCopiesForNextLevel(level), character.getCopiesCount());
    }
}
