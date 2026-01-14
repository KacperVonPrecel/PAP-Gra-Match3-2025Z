package pap.project.game.model;

import org.springframework.lang.NonNull;
import pap.project.game.model.communication.PlayerCharactersState;

public record CharacterCombatResult(
        @NonNull PlayerCharactersState firstPlayerCharacterState,
        @NonNull PlayerCharactersState secondPlayerCharacterState,
        long attackingCharacterId,
        boolean gameEnded
) {}