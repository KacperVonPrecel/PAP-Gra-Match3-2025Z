package pap.project.match3;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pap.project.match3.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class Match3Service {
    private static final int DEFAULT_BOARD_SIZE = 5;

    private final Map<String, Match3Board> games = new ConcurrentHashMap<>();
    public final Queue<PlayerData> waitingPlayers = new ConcurrentLinkedQueue<>();

    /**
     * @return if null then game is not created, otherwise it means that game got created.
     */
    public @Nullable GameStartData joinOrCreateGame(@NonNull PlayerData player)
    {
        for (Map.Entry<String, Match3Board> entry : games.entrySet())
        {
            if (Arrays.stream(entry.getValue().getPlayerData()).anyMatch((playerData -> playerData.playerId() == player.playerId())))
            {
                final PlayerData[] players = entry.getValue().getPlayerData();

                return new GameStartData(
                        entry.getKey(),
                        entry.getValue().getGameState(),
                        new ConcurrentHashMap<>(Map.of(
                                players[0].playerId(), players[0],
                                players[1].playerId(), players[1]
                        ))
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

        final Match3Block[][] blocks = new Match3Block[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE];
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++)
        {
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++)
            {
                blocks[i][j] = new Match3Block();
            }
        }

        final Match3Board board = new Match3Board(blocks, MatchableShapeLibrary.ALL_SHAPES, new PlayerData[] {player, otherPlayer});
        games.put(gameId, board);

        return new GameStartData(
                gameId,
                board.getGameState(),
                new ConcurrentHashMap<>(Map.of(
                        player.playerId(), player,
                        otherPlayer.playerId(), otherPlayer
                ))
        );
    }

    public void exitQueue(@NonNull PlayerData player)
    {
        waitingPlayers.remove(player);
    }

    public @Nullable GameState playTurn(@NonNull String gameId, @NonNull MoveRequest moveRequest, long playerId)
    {
        if (games.containsKey(gameId))
            return games.get(gameId).playTurn(moveRequest, playerId);

        return null;
    }

    public @Nullable GameState getState(@NonNull String gameId)
    {
        if (games.containsKey(gameId))
            return games.get(gameId).getGameState();

        return null;
    }

    public @Nullable PlayerData[] getPlayers(@NonNull String gameId)
    {
        if (games.containsKey(gameId))
            return games.get(gameId).getPlayerData();

        return null;
    }

    public boolean hasGameEnded(@NonNull String gameId)
    {
        if (games.containsKey(gameId))
            return games.get(gameId).hasGameEnded();

        return false;
    }

    private @NonNull String generateId()
    {
        return UUID.randomUUID().toString();
    }
}
