package pap.project.match3.game.model;

import org.springframework.lang.NonNull;
import pap.project.match3.model.PlayerCharactersState;

public record CharacterCombatResult(
        @NonNull PlayerCharactersState firstPlayerCharacterState,
        @NonNull PlayerCharactersState secondPlayerCharacterState,
        long attackingCharacterId
) {}