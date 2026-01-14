package pap.project.game.model.communication;

import org.springframework.lang.NonNull;

import java.util.Map;

public record PlayerCharactersState(
        @NonNull Map<Long, Integer> charactersHealth
) { }
