package pap.project.match3.game;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pap.project.match3.model.*;

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
        for (Map.Entry<String, Game> entry : games.entrySet())
        {
            if (entry.getValue().firstPlayerData.playerId() == player.playerId() || entry.getValue().secondPlayerData.playerId() == player.playerId())
            {
                return new GameStartData(
                        entry.getKey(),
                        entry.getValue().getGameState(),
                        Map.of(
                                entry.getValue().firstPlayerData.playerId(), entry.getValue().firstPlayerData,
                                entry.getValue().secondPlayerData.playerId(), entry.getValue().secondPlayerData
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
                Map.of(
                        player.playerId(), player,
                        otherPlayer.playerId(), otherPlayer
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
                return games.get(gameId).playTurn(playerId, moveRequest);
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
        if (games.containsKey(gameId))
            return games.get(gameId).getGameState();

        return null;
    }
// XXX
//    public @Nullable PlayerData[] getPlayers(@NonNull String gameId)
//    {
//        if (games.containsKey(gameId))
//            return games.get(gameId).getPlayerData();
//
//        return null;
//    }

//    public boolean hasGameEnded(@NonNull String gameId)
//    {
//        if (games.containsKey(gameId))
//            return games.get(gameId).hasGameEnded();
//
//        return false;
//    }

    private @NonNull String generateId()
    {
        return UUID.randomUUID().toString();
    }
}
