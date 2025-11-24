package pap.project.game_history;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pap.project.game_history.controller.model.load.LoadRequest;
import pap.project.game_history.controller.model.load.MatchProjectionForController;
import pap.project.game_history.controller.model.process.ProcessResult;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoadMatchesServiceTest
{
    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private LoadMatchesService loadMatchesService;

    @InjectMocks
    private EndGameService endGameService;

    private static final long MOCK_FINISH_TIME = 1000166400;

    private LoadRequest loadRequest = new LoadRequest(20, 1L, MOCK_FINISH_TIME);

    @Test
    void test_load_matches_successful()
    {
        User playerOne = new User(
                "test-user-1",
                "test-user-1@gmail.com",
                "password"
        );
        User playerTwo = new User(
                "test-user-2",
                "test-user-2@gmail.com",
                "password"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(playerOne));
        when(userRepository.findById(2L)).thenReturn(Optional.of(playerTwo));

        doAnswer(i -> {
            playerOne.setEloPoints(120);
            playerTwo.setEloPoints(90);
            return null;
        }).when(userRepository).updateUserAfterEndGame(anyInt(), anyInt(), anyLong());

        final ProcessResult result = endGameService.processEndGame(1L, 2L, MOCK_FINISH_TIME);
        assertEquals(ProcessResult.SUCCESS, result);

        List<Match> matches = new ArrayList<>();
        matches.add(new Match(playerOne, playerTwo, MOCK_FINISH_TIME, 20, -10));
        Page<Match> pagedMatches = new PageImpl(matches);
        Mockito.when(matchRepository.findMatchesBeforeFinishTime(anyLong(), anyLong(), any())).thenReturn(pagedMatches);

        Match match = matches.getFirst();

        List<MatchProjectionForController> matchesList = loadMatchesService.loadMatches(loadRequest);
        assertEquals(1, matchesList.size());
        MatchProjectionForController matchProjectionForController = matchesList.get(0);
        assertEquals(matchProjectionForController.playerId(), match.getWinner().getId());
        assertEquals(matchProjectionForController.opponentsId(), match.getLoser().getId());
        assertEquals("test-user-1", matchProjectionForController.playerUserName());
        assertEquals("test-user-2", matchProjectionForController.opponentsUserName());
        assertEquals(MOCK_FINISH_TIME, matchProjectionForController.finishTime());
        assertEquals(20, matchProjectionForController.playerEloChange());
        assertEquals(-10, matchProjectionForController.opponentsEloChange());
        assertEquals(120, matchProjectionForController.playerEloPoints());
        assertEquals(90, matchProjectionForController.opponentsEloPoints());
        assertTrue(matchProjectionForController.isPlayerWinner());
    }

}
