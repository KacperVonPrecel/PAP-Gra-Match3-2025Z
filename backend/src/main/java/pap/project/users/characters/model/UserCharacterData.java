package pap.project.users.characters.model;

import org.springframework.lang.NonNull;

public record UserCharacterData(
        @NonNull CharacterType characterType,
//       XXX maybe commented data should be in frontend?
//        int damage,
//        int health,
        int level,
//        int requiredCopiesForNextLevel,
        int currentCopiesCount
) { }
