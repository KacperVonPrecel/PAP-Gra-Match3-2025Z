package pap.project.game_history;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.game_history.controller.model.load.LoadRequest;
import pap.project.game_history.controller.model.process.ProcessResult;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EndGameServiceTest
{
    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private EndGameService endGameService;

    private static final long MOCK_FINISH_TIME = 1000166400;

    @Test
    void test_process_end_game_successful()
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
        final ProcessResult result = endGameService.processEndGame(1L, 2L, MOCK_FINISH_TIME);
        assertEquals(ProcessResult.SUCCESS, result);
    }

    @Test
    void test_process_end_game_failure_while_updating()
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
        doThrow(new PersistenceException()).when(userRepository).updateUserAfterEndGame(anyInt(), anyInt(), anyLong());
        final ProcessResult result = endGameService.processEndGame(1L, 2L, MOCK_FINISH_TIME);
        assertEquals(ProcessResult.FAILED, result);
    }

    @Test
    void test_process_end_game_failure_while_saving_to_database()
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
        doThrow(new PersistenceException()).when(matchRepository).save(any(Match.class));
        final ProcessResult result = endGameService.processEndGame(1L, 2L, MOCK_FINISH_TIME);
        assertEquals(ProcessResult.FAILED, result);
    }

}
