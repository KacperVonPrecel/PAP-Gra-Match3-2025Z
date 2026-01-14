package pap.project.match3.model;

/**
 * @param elo gained/losed by player. If it negative, it. Otherwise, it will be non-negative.
 * @param money gained by player. It always has positive value.
 */
public record PlayerStatChange(
        int elo,
        int money
) { }
