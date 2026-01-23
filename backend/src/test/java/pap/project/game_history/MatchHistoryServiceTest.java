package pap.project.game_history;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.NonNull;
import org.springframework.test.util.ReflectionTestUtils;
import pap.project.characters.model.CharacterType;
import pap.project.game_history.model.HistoryCharacterData;
import pap.project.game_history.model.MatchFromHistoryData;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Mock
    private MatchCharactersRepository matchCharactersRepository;

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

        final Match mockedMatch = Match.createMatchForTest(playerOne, playerTwo, finishTime, 20, -10);
        Long matchId = 10L;
        ReflectionTestUtils.setField(mockedMatch, "id", matchId);

        final MatchCharacters mockedMatchCharacters = getMatchCharacters(mockedMatch);
        mockedMatch.setMatchCharacters(mockedMatchCharacters);

        final UserStats userStatsOne = new UserStats(playerOne);
        final UserStats userStatsTwo = new UserStats(playerTwo);

        final List<Match> matches = List.of(mockedMatch);
        Mockito.when(matchRepository.findMatchesBeforeRecordId(anyLong(), anyLong(), any())).thenReturn(matches);

        Mockito.when(userRepository.existsById(1)).thenReturn(true);
        Mockito.when(userStatsRepository.findUserStatsById(1)).thenReturn(Optional.of(userStatsOne));
        Mockito.when(userStatsRepository.findUserStatsById(2)).thenReturn(Optional.of(userStatsTwo));

        final LoadHistoryMatchesData matchesListData = matchHistoryService.loadMatches(1, finishTime, 10);
        assertNotNull(matchesListData);
        final List<MatchFromHistoryData> matchesList = matchesListData.matches();
        assertEquals(1, matchesList.size());
        final MatchFromHistoryData matchFromHistoryData = matchesList.getFirst();

        assertEquals(playerOne.getId().orElseThrow(), matchFromHistoryData.playerId());
        assertEquals(playerTwo.getId().orElseThrow(), matchFromHistoryData.opponentsId());
        assertEquals("test-user-1", matchFromHistoryData.playerUsername());
        assertEquals("test-user-2", matchFromHistoryData.opponentsUsername());
        assertEquals(finishTime, matchFromHistoryData.finishTime());
        assertEquals(20, matchFromHistoryData.playerEloChange());
        assertEquals(-10, matchFromHistoryData.opponentsEloChange());
        assertEquals(100, matchFromHistoryData.playerEloPoints());
        assertEquals(100, matchFromHistoryData.opponentsEloPoints());

        assertEquals(CharacterType.AMETHYST_ENCHANTRESS, matchFromHistoryData.playerCharacters().getFirst().getCharacterType());
        assertEquals(10, matchFromHistoryData.playerCharacters().getFirst().getLevel());
        assertEquals(CharacterType.RUBY_HORNED_DAME, matchFromHistoryData.playerCharacters().get(1).getCharacterType());
        assertEquals(12, matchFromHistoryData.playerCharacters().get(1).getLevel());
        assertEquals(CharacterType.HONEY_TRIGGER, matchFromHistoryData.playerCharacters().getLast().getCharacterType());
        assertEquals(9, matchFromHistoryData.playerCharacters().getLast().getLevel());

        assertEquals(CharacterType.TRASH_MAN, matchFromHistoryData.opponentCharacters().getFirst().getCharacterType());
        assertEquals(13, matchFromHistoryData.opponentCharacters().getFirst().getLevel());
        assertEquals(CharacterType.SACRED_CAT, matchFromHistoryData.opponentCharacters().get(1).getCharacterType());
        assertEquals(12, matchFromHistoryData.opponentCharacters().get(1).getLevel());
        assertEquals(CharacterType.EMERALD_CORE_KNIGHT, matchFromHistoryData.opponentCharacters().getLast().getCharacterType());
        assertEquals(11, matchFromHistoryData.opponentCharacters().getLast().getLevel());

        assertTrue(matchFromHistoryData.isPlayerWinner());
    }

    private static MatchCharacters getMatchCharacters(@NonNull Match mockedMatch) {
        final List<HistoryCharacterData> historyCharacterDataWinner = List.of(
                new HistoryCharacterData(CharacterType.AMETHYST_ENCHANTRESS, 10),
                new HistoryCharacterData(CharacterType.RUBY_HORNED_DAME, 12),
                new HistoryCharacterData(CharacterType.HONEY_TRIGGER, 9)
        );

        final List<HistoryCharacterData> historyCharacterDataLoser = List.of(
                new HistoryCharacterData(CharacterType.TRASH_MAN, 13),
                new HistoryCharacterData(CharacterType.SACRED_CAT, 12),
                new HistoryCharacterData(CharacterType.EMERALD_CORE_KNIGHT, 11)
        );

        return new MatchCharacters(mockedMatch, historyCharacterDataWinner, historyCharacterDataLoser);
    }
}

