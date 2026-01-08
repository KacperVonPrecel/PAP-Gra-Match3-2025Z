package pap.project.match3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pap.project.match3.model.BoardState;
import pap.project.match3.model.GameState;
import pap.project.match3.model.MoveRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Match3Service {
    private final Map<Integer, Match3Board> games = new ConcurrentHashMap<>();

    private Logger logger = LogManager.getLogger(Match3Service.class);

    public int startNewGame()
    {
        int gameId = generateId();

        final Match3Block[][] blocks = new Match3Block[5][5];
        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                blocks[i][j] = new Match3Block();
            }
        }

        // TODO: Move this somewhere else
        final MatchableShape.RelativeCoordinates[] threeInLineHorizontal = {
            new MatchableShape.RelativeCoordinates(1, 0),
            new MatchableShape.RelativeCoordinates(2, 0),
        };

        final MatchableShape.RelativeCoordinates[] threeInLineVertical = {
                new MatchableShape.RelativeCoordinates(0, 1),
                new MatchableShape.RelativeCoordinates(0, 2),
        };

        final MatchableShape[] matchableShapes = new MatchableShape[] {
            new MatchableShape(threeInLineHorizontal),
            new MatchableShape(threeInLineVertical),
        };

        final Match3Board board = new Match3Board(blocks, matchableShapes);

        games.put(gameId, board);

        return gameId;
    }

    public void endGame(int gameId)
    {
        games.remove(gameId);
    }

    private int generateId()
    {
        return 0; // TODO: id generation
    }

    public @Nullable GameState playTurn(int gameId, @NonNull MoveRequest moveRequest)
    {
        if  (games.containsKey(gameId))
        {
            BoardState boardState = games.get(gameId).playTurn(moveRequest);

            return new GameState(boardState);
        }

        return null;
    }

    public @Nullable GameState getState(int gameId)
    {
        if (games.containsKey(gameId))
        {
            BoardState boardState = games.get(gameId).getState();

            return new GameState(boardState);
        }

        return null;
    }
}
