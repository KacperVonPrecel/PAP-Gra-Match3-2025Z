package pap.project.match3.game;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.match3.Match3Board;
import pap.project.match3.game.model.CharacterCombatResult;
import pap.project.match3.model.*;

import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.Semaphore;

/**
 * Once instance of this class can be used in multiple threads.
 */
public class Game
{
    public final @NonNull PlayerData firstPlayerData;
    public final @NonNull PlayerData secondPlayerData;

    private final @NonNull Match3Board match3Board;
    private final @NonNull CharactersCombat charactersCombat;

    private final @NonNull Semaphore gameSemaphore = new Semaphore(1);

    private boolean firstPlayerMove = true;

    public Game(@NonNull PlayerData firstPlayerData, @NonNull PlayerData secondPlayerData)
    {
        this.firstPlayerData = firstPlayerData;
        this.secondPlayerData = secondPlayerData;

        match3Board = new Match3Board();
        //XXX create character for combat
        charactersCombat = new CharactersCombat(null, null);
    }

    /**
     * This function is blocking, when there is other invocation of this method on the same object. It waits until
     * other caller finish executing this function.
     *
     * @param playerId id of player which send request.
     * @param moveRequest move which player made.
     * @return null if request was invalid.
     */
    public @Nullable GameState playTurn(long playerId, @NonNull MoveRequest moveRequest) throws InterruptedException
    {
        gameSemaphore.acquire();
        try
        {
            final long playerIdToMakeMove = getIdOfPlayerWhichShouldMakeMove();
            if (playerId != playerIdToMakeMove)
                return null;

            final Match3MoveResult match3Result = match3Board.makeMove(moveRequest);

            if (match3Result == null)
                return null;

            final CharacterCombatResult combatResult = charactersCombat.process(firstPlayerMove, match3Result.totalMatchedBlocks());

            firstPlayerMove = !firstPlayerMove;

            final long playerIdToTakeNextMove = getIdOfPlayerWhichShouldMakeMove();

            final Map<Long, PlayerCharactersState> playerCharacterState = Map.of(
                    firstPlayerData.playerId(), combatResult.firstPlayerCharacterState(),
                    secondPlayerData.playerId(), combatResult.secondPlayerCharacterState()
            );

            return new GameState(match3Result.newBoardState(), playerIdToTakeNextMove, playerCharacterState, OptionalLong.of(combatResult.attackingCharacterId()));
        } finally
        {
            gameSemaphore.release();
        }
    }

    /**
     * @return game state without board animation, and with empty {@link GameState#attackingCharacterId}
     */
    public @NonNull GameState getGameState()
    {
        final Map<Long, PlayerCharactersState> playerCharacterState = Map.of(
                firstPlayerData.playerId(), charactersCombat.getPlayerCharactersState(true),
                secondPlayerData.playerId(), charactersCombat.getPlayerCharactersState(false)
        );
        return new GameState(match3Board.getBoardState(), getIdOfPlayerWhichShouldMakeMove(), playerCharacterState, OptionalLong.empty());
    }

    private long getIdOfPlayerWhichShouldMakeMove()
    {
        return firstPlayerMove ? firstPlayerData.playerId() : secondPlayerData.playerId();
    }
}
