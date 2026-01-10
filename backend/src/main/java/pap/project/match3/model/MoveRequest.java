package pap.project.match3.model;

import org.springframework.lang.NonNull;

public record MoveRequest(
        @NonNull Position source,
        @NonNull Position target
) { }