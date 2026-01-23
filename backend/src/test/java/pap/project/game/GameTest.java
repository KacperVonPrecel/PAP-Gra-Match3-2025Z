package pap.project.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pap.project.game.match3.model.Match3MoveResult;
import pap.project.game.match3.model.MoveRequest;
import pap.project.game.model.CharacterCombatResult;
import pap.project.game.model.communication.GameState;
import pap.project.game.model.communication.PlayerData;
import pap.project.game.model.communication.GameCharacter;
import pap.project.characters.model.CharacterType;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameTest {

    private PlayerData p1 = new PlayerData(1L, "player1", 1000, java.util.List.of(new GameCharacter(11L, CharacterType.TRASH_MAN, 10, 1)));
    private PlayerData p2 = new PlayerData(2L, "player2", 1000, java.util.List.of(new GameCharacter(21L, CharacterType.TRASH_MAN, 10, 1)));

    @Test
    public void test_playTurn_wrong_player_returns_null() throws Exception {
        Game game = new Game(p1, p2);

        Match3MoveResult mockResult = mock(Match3MoveResult.class);
        // use reflection to replace match3Board to a mock that returns null
        var match3BoardMock = mock(pap.project.game.match3.Match3Board.class);
        when(match3BoardMock.makeMove(any())).thenReturn(null);
        setPrivateField(game, "match3Board", match3BoardMock);

        GameState res = game.playTurn(2L, new MoveRequest(new pap.project.game.match3.model.Position(0,0), new pap.project.game.match3.model.Position(0,1)));
        assertNull(res);
    }

    @Test
    public void test_playTurn_valid_move_delegates_to_combat_and_returns_game_state() throws Exception {
        Game game = new Game(p1, p2);

        var match3BoardMock = mock(pap.project.game.match3.Match3Board.class);
        Match3MoveResult mockMoveRes = new Match3MoveResult(null, Map.of());
        when(match3BoardMock.makeMove(any())).thenReturn(mockMoveRes);
        setPrivateField(game, "match3Board", match3BoardMock);

        // replace charactersCombat with mock
        CharactersCombat combatMock = mock(CharactersCombat.class);
        when(combatMock.process(anyBoolean(), any())).thenReturn(new CharacterCombatResult(new pap.project.game.model.communication.PlayerCharactersState(Map.of()), new pap.project.game.model.communication.PlayerCharactersState(Map.of()), 0L, false));
        setPrivateField(game, "charactersCombat", combatMock);

        GameState s = game.playTurn(1L, new MoveRequest(new pap.project.game.match3.model.Position(0,0), new pap.project.game.match3.model.Position(0,1)));
        assertNotNull(s);
    }

    @Test
    public void test_playTurn_combat_end() throws Exception {
        Game game = new Game(p1, p2);

        var match3BoardMock = mock(pap.project.game.match3.Match3Board.class);
        Match3MoveResult mockMoveRes = new Match3MoveResult(null, Map.of());
        when(match3BoardMock.makeMove(any())).thenReturn(mockMoveRes);
        setPrivateField(game, "match3Board", match3BoardMock);

        CharactersCombat combatMock = mock(CharactersCombat.class);
        when(combatMock.process(anyBoolean(), any())).thenReturn(new CharacterCombatResult(new pap.project.game.model.communication.PlayerCharactersState(Map.of()), new pap.project.game.model.communication.PlayerCharactersState(Map.of()), 0L, true));
        setPrivateField(game, "charactersCombat", combatMock);

        GameState s = game.playTurn(1L, new MoveRequest(new pap.project.game.match3.model.Position(0,0), new pap.project.game.match3.model.Position(0,1)));
        assertNotNull(s);
        assertNotNull(s.gameEndData());
    }

    // reflection helper
    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
