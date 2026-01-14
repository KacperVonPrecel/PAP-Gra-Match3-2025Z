package pap.project.user_data.model.controller;

import jakarta.validation.constraints.NotNull;
import pap.project.characters.model.controller.CharacterData;

public record UpgradeCharacterResponse(
        @NotNull CharacterData characterData
) {
}
