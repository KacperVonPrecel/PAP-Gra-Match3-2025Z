package pap.project.characters;


import org.springframework.lang.NonNull;

import java.util.OptionalInt;

/**
 * Function from this interface would be called in multiple threads.
 * This functions shouldn't take long to be executed.
 * Probably it should be simple multiplication or switch.
 * This method's should be stateless.
 */
public interface Character
{

    /**
     * @return positive value.
     */
    int getDamage(int level);
    /**
     * @return positive value.
     */
    int getHealth(int level);
    /**
     * @return if it is empty it means that character reach maximum level.
     *         If it has value it positive.
     */
    @NonNull OptionalInt getRequiredCopiesForNextLevel(int level);

    @NonNull CharacterInGame createCharacterInGame(long characterId, int level);
}
