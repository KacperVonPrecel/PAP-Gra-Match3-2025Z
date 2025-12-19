package pap.project.game_history.model;

import org.springframework.lang.NonNull;
import pap.project.users.characters.model.CharacterType;

public record HistoryCharacterData(
        @NonNull CharacterType characterType,
        int level
        ) {
}
