package pap.project.game;

import org.junit.jupiter.api.Test;
import pap.project.game.model.communication.GameStartData;
import pap.project.game.model.communication.PlayerData;
import pap.project.game.model.communication.GameCharacter;
import pap.project.characters.model.CharacterType;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Test
    public void test_joinOrCreateGame_first_adds_to_queue_second_creates_game() {
        GameService service = new GameService();

        PlayerData p1 = new PlayerData(1L, "p1", 100, java.util.List.of(new GameCharacter(11L, CharacterType.TRASH_MAN, 10, 1)));
        PlayerData p2 = new PlayerData(2L, "p2", 100, java.util.List.of(new GameCharacter(21L, CharacterType.TRASH_MAN, 10, 1)));

        assertNull(service.joinOrCreateGame(p1));
        GameStartData start = service.joinOrCreateGame(p2);
        assertNotNull(start);
        assertEquals(2, start.playerData().size());
    }

    @Test
    public void test_exitQueue_removes_player() {
        GameService service = new GameService();
        PlayerData p = new PlayerData(11L, "p11", 100, java.util.List.of(new GameCharacter(111L, CharacterType.TRASH_MAN, 10, 1)));
        assertNull(service.joinOrCreateGame(p));
        service.exitQueue(p);
        assertNull(service.joinOrCreateGame(p)); // queued again -> returns null
    }

    @Test
    public void test_exitQueue_non_queued() {
        GameService service = new GameService();
        PlayerData p = new PlayerData(99L, "p99", 100, java.util.List.of(new GameCharacter(199L, CharacterType.TRASH_MAN, 10, 1)));
        service.exitQueue(p);
        assertNull(service.joinOrCreateGame(p));
    }
}
