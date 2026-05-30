package pap.project.game.model.communication;

public record PlayerStartData(
        long playerId,
        PlayerData playerData
) {
}
