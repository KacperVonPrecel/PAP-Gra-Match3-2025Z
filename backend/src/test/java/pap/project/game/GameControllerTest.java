package pap.project.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import pap.project.game.model.communication.GameStartData;
import pap.project.game.model.communication.PlayerData;
import pap.project.game.model.communication.PlayerStartData;
import pap.project.user_data.UserDataService;
import pap.project.user_stats.UserStatsRepository;
import pap.project.characters.UserCharactersService;
import pap.project.users.UserAuthDetails;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameControllerTest {

    private GameService gameService;
    private UserDataService userDataService;
    private UserStatsRepository userStatsRepository;
    private UserCharactersService userCharactersService;
    private SimpMessagingTemplate messaging;

    private GameController controller;

    @BeforeEach
    public void setup() {
        gameService = mock(GameService.class);
        userDataService = mock(UserDataService.class);
        userStatsRepository = mock(UserStatsRepository.class);
        userCharactersService = mock(UserCharactersService.class);
        messaging = mock(SimpMessagingTemplate.class);

        controller = new GameController(gameService, userDataService, userStatsRepository, userCharactersService, messaging);
    }

    @Test
    public void test_join_when_game_created_sends_notifications() throws Exception {
        PlayerData p1 = new PlayerData(1L, "a", 100, List.of());
        PlayerData p2 = new PlayerData(2L, "b", 100, List.of());
        GameStartData gsd = new GameStartData("game1", null, List.of(new PlayerStartData(1L, p1), new PlayerStartData(2L, p2)));

        Method notify = GameController.class.getDeclaredMethod("notifyGameStarted", GameStartData.class);
        notify.setAccessible(true);
        notify.invoke(controller, gsd);

        verify(messaging, atLeastOnce()).convertAndSendToUser(anyString(), eq("/queue/gameStart"), eq(gsd));
    }

    @Test
    public void test_notify_game_started_sends_to_all_players() throws Exception {
        PlayerData p1 = new PlayerData(1L, "a", 100, List.of());
        PlayerData p2 = new PlayerData(2L, "b", 100, List.of());
        GameStartData gsd = new GameStartData("game1", null, List.of(new PlayerStartData(1L, p1), new PlayerStartData(2L, p2)));

        Method notify = GameController.class.getDeclaredMethod("notifyGameStarted", GameStartData.class);
        notify.setAccessible(true);
        notify.invoke(controller, gsd);

        verify(messaging, times(2)).convertAndSendToUser(anyString(), eq("/queue/gameStart"), eq(gsd));
    }
}
