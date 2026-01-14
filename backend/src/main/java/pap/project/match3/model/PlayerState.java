package pap.project.match3.model;

import java.util.Map;

public record PlayerState(
        Map<Long, Integer> charactersHealth
) { }
