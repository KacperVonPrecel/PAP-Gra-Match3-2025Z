package pap.project.game.model.communication;

import org.springframework.lang.NonNull;

import java.util.List;

public record PlayerState(
        long playerId,
        @NonNull List<CharacterHealth> playerCharactersState
) {
}
