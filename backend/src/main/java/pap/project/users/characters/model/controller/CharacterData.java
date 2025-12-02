package pap.project.users.characters.model.controller;

import org.springframework.lang.NonNull;
import pap.project.users.characters.model.CharacterType;

import java.util.OptionalInt;

/**
 * If it will be easier damage, health and requiredCopiesForNextLevel can be not send and be stored in frontend.
 * @param damage positive value.
 * @param health positive value.
 * @param level positive value.
 * @param requiredCopiesForNextLevel cannot be negative. If it is empty it means that character has reached max level.
 * @param currentCopiesCount cannot be negative. It can be bigger than {@link #requiredCopiesForNextLevel}.
 */
public record CharacterData(
        @NonNull CharacterType characterType,
        int damage,
        int health,
        int level,
        @NonNull OptionalInt requiredCopiesForNextLevel,
        int currentCopiesCount
        ) {
}
