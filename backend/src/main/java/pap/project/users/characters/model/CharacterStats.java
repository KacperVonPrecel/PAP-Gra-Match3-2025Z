package pap.project.users.characters.model;


import org.springframework.lang.NonNull;

import java.util.OptionalInt;

/**
 * Function from this interface would be called in multiple threads.
 * This functions shouldn't take long to be executed.
 * Probably it should be simple multiplication or switch.
 *
 */
public interface CharacterStats
{
    // XXX Add to this functions throws declarations if level is invalid.
    // - Probably it shouldn't be InvalidArgumentException to require to handle this exception.

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
}
