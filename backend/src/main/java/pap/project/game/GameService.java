package pap.project.game;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pap.project.game.match3.model.MoveRequest;
import pap.project.game.model.communication.GameStartData;
import pap.project.game.model.communication.GameState;
import pap.project.game.model.communication.PlayerData;
import pap.project.game.model.communication.XXX3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameService
{
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    public final Queue<PlayerData> waitingPlayers = new ConcurrentLinkedQueue<>();

    /**
     * @return if null then game is not created, otherwise it means that game got created.
     */
    public @Nullable GameStartData joinOrCreateGame(@NonNull PlayerData player)
    {
        System.out.println("xxx" + player.playerId());
        for (Map.Entry<String, Game> entry : games.entrySet())
        {
            if (entry.getValue().firstPlayerData.playerId() == player.playerId() || entry.getValue().secondPlayerData.playerId() == player.playerId())
            {
                return new GameStartData(
                        entry.getKey(),
                        entry.getValue().getGameState(),
                        List.of(
                                new XXX3(entry.getValue().firstPlayerData.playerId(), entry.getValue().firstPlayerData),
                                new XXX3(entry.getValue().secondPlayerData.playerId(), entry.getValue().secondPlayerData)
                        )
                );
            }
        }

        if (waitingPlayers.contains(player))
            return null;

        if (waitingPlayers.isEmpty())
        {
            waitingPlayers.add(player);
            return null;
        }

        final PlayerData otherPlayer = waitingPlayers.poll();
        final String gameId = generateId();

        final Game game = new Game(player, otherPlayer);
        games.put(gameId, game);

        return new GameStartData(
                gameId,
                game.getGameState(),
                List.of(
                        new XXX3(player.playerId(), player),
                        new XXX3(otherPlayer.playerId(), otherPlayer)
                )
        );
    }

    public void exitQueue(@NonNull PlayerData player)
    {
        waitingPlayers.remove(player);
    }

    public @Nullable GameState playTurn(@NonNull String gameId, @NonNull MoveRequest moveRequest, long playerId)
    {
        if (games.containsKey(gameId))
        {
            try
            {
                final GameState gameState = games.get(gameId).playTurn(playerId, moveRequest);;
                if (gameState != null && gameState.gameEndData() != null)
                    games.remove(gameId);
                return gameState;
            } catch (InterruptedException e)
            {
                Thread.interrupted();
                return null;
            }
        }


        return null;
    }

    public @Nullable GameState getState(@NonNull String gameId)
    {
        System.out.println("xxxasdasdasd");
        if (games.containsKey(gameId))
            return games.get(gameId).getGameState();

        return null;
    }

    private @NonNull String generateId()
    {
        return UUID.randomUUID().toString();
    }
}
