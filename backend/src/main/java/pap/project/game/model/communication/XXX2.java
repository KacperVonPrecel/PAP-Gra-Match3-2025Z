package pap.project.game.model.communication;

import org.springframework.lang.NonNull;

import java.util.List;

public record XXX2(
        long playerId,
        @NonNull List<XXX1> playerCharactersState
) {
}
