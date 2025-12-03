package pap.project.game_history;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.game_history.model.MatchFromHistoryData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchHistoryHistoryTest
{
    @Mock
    private MatchHistoryService matchHistoryService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @InjectMocks
    private MatchHistoryController matchHistoryController;

    private final long finishTime = 1000166400;


    @Test
    void test_load_matches_success()
    {
        final List<MatchFromHistoryData> matches = List.of(
                new MatchFromHistoryData(
                    1,
                    "test-user1",
                    2,
                    "test-user2",
                    finishTime,
                    20,
                    -10,
                    100,
                    100,
                    true
                ),
                new MatchFromHistoryData(
                    1,
                    "test-user1",
                    2,
                    "test-user2",
                    finishTime + 10000,
                    20,
                    -10,
                    100,
                    100,
                    false
                )
        );

        final LoadHistoryMatchesData loadHistoryMatchesDataMock = new LoadHistoryMatchesData(matches, false);

        when(matchHistoryService.loadMatches(1, 2, 10)).thenReturn(loadHistoryMatchesDataMock);

        final LoadHistoryMatchesData loadHistoryMatchesData = matchHistoryController.load(1, 2, 10);

        assertFalse(loadHistoryMatchesData.isMoreToLoad());
        assertEquals(2, loadHistoryMatchesData.getMatches().size());
        final List<MatchFromHistoryData> loadedMatches = loadHistoryMatchesData.getMatches();
        assertEquals("test-user1", loadedMatches.getFirst().playerUsername());
        assertEquals("test-user1", loadedMatches.getLast().playerUsername());

        assertEquals("test-user2", loadedMatches.getFirst().opponentsUsername());
        assertEquals("test-user2", loadedMatches.getLast().opponentsUsername());

        assertEquals(finishTime, loadedMatches.getFirst().finishTime());
        assertEquals(finishTime + 10000, loadedMatches.getLast().finishTime());

        assertEquals(20, loadedMatches.getFirst().playerEloChange());
        assertEquals(20, loadedMatches.getLast().playerEloChange());
        assertEquals(-10, loadedMatches.getFirst().opponentsEloChange());
        assertEquals(-10, loadedMatches.getLast().opponentsEloChange());

        assertEquals(100, loadedMatches.getFirst().playerEloPoints());
        assertEquals(100, loadedMatches.getLast().playerEloPoints());
        assertEquals(100, loadedMatches.getFirst().opponentsEloPoints());
        assertEquals(100, loadedMatches.getLast().opponentsEloPoints());

        assertTrue(loadedMatches.getFirst().isPlayerWinner());
        assertFalse(loadedMatches.getLast().isPlayerWinner());
    }

}
