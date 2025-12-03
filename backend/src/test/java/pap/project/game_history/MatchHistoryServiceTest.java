package pap.project.game_history;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.game_history.model.MatchFromHistoryData;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MatchHistoryServiceTest
{
    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserStatsRepository userStatsRepository;

    @InjectMocks
    private MatchHistoryService matchHistoryService;

    private final long finishTime = 1000166400;

    @Test
    void test_load_matches_successful()
    {
        final User playerOne = User.createUserForTests(
                1,
                "test-user-1",
                "test-user-1@gmail.com",
                "password"
        );

        final User playerTwo = User.createUserForTests(
                2,
                "test-user-2",
                "test-user-2@gmail.com",
                "password"
        );

        final UserStats userStatsOne = new UserStats(1);
        final UserStats userStatsTwo = new UserStats(2);

        final List<Match> matches = List.of(Match.createMatchForTest(playerOne, playerTwo, finishTime, 20, -10));
        Mockito.when(matchRepository.findMatchesBeforeRecordId(anyLong(), anyLong(), any())).thenReturn(matches);

        Mockito.when(userRepository.existsById(1)).thenReturn(true);
        Mockito.when(userStatsRepository.findUserStatsByUserId(1)).thenReturn(Optional.of(userStatsOne));
        Mockito.when(userStatsRepository.findUserStatsByUserId(2)).thenReturn(Optional.of(userStatsTwo));

        final LoadHistoryMatchesData matchesListData = matchHistoryService.loadMatches(1, finishTime, 10);
        final List<MatchFromHistoryData> matchesList = matchesListData.getMatches();
        assertEquals(1, matchesList.size());
        final MatchFromHistoryData matchFromHistoryData = matchesList.getFirst();
        assertEquals(playerOne.getId().orElseThrow(), matchFromHistoryData.playerId());
        assertEquals(playerTwo.getId().orElseThrow(), matchFromHistoryData.opponentsId());
        assertEquals("test-user-1", matchFromHistoryData.playerUsername());
        assertEquals("test-user-2", matchFromHistoryData.opponentsUsername());
        assertEquals(finishTime, matchFromHistoryData.finishTime());
        assertEquals(20, matchFromHistoryData.playerEloChange());
        assertEquals(-10, matchFromHistoryData.opponentsEloChange());
        assertTrue(matchFromHistoryData.isPlayerWinner());
    }
}
