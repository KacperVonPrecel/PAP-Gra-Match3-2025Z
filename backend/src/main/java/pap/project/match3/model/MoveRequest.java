package pap.project.match3.model;

public record MoveRequest(
        Position source,
        Position target
) { }