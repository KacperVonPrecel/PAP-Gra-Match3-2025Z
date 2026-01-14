package pap.project.match3.model;

import org.springframework.lang.NonNull;
import pap.project.users.characters.model.CharacterType;

public record GameCharacter(
        long characterId,
        @NonNull CharacterType characterType,
        int damage,
        int maxHealth
) { }
