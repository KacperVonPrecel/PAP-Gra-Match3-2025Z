package pap.project.user_data.model.controller;

import pap.project.characters.model.CharacterType;

public record DrawResultEntry(
        CharacterType characterType,
        int amount
) {
}
