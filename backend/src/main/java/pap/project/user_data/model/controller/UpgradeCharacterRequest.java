package pap.project.user_data.model.controller;

import org.springframework.lang.NonNull;
import pap.project.characters.model.CharacterType;

public record UpgradeCharacterRequest(
        @NonNull CharacterType characterType
) {
}
