package pap.project.user_data.model.controller;

import org.springframework.lang.NonNull;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.model.CharacterType;

public record UpgradeCharacterRequest(
        @NonNull CharacterType characterType
) {
}
