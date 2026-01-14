package pap.project.match3.model;

import org.springframework.lang.NonNull;

import java.util.Map;

public record PlayerCharactersState(
        @NonNull Map<Long, Integer> charactersHealth
) { }
