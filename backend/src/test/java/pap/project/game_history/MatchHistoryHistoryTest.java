package pap.project.game_history;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import pap.project.characters.model.CharacterType;
import pap.project.game_history.model.HistoryCharacterData;
import pap.project.game_history.model.MatchFromHistoryData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchHistoryHistoryTest
{
    @Mock
    private MatchHistoryService matchHistoryService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MatchHistoryController matchHistoryController;

    private final long finishTime = 1000166400;


    @Test
    void test_load_matches_success()
    {
        final LoadHistoryMatchesData loadHistoryMatchesDataMock = getLoadHistoryMatchesData();

        when(matchHistoryService.loadMatches(1, 20L, 10)).thenReturn(loadHistoryMatchesDataMock);

        final LoadHistoryMatchesData loadHistoryMatchesData = matchHistoryController.load(authentication, 100L, 20L, 10);

        assertFalse(loadHistoryMatchesData.moreToLoad());
        assertEquals(2, loadHistoryMatchesData.matches().size());
        final List<MatchFromHistoryData> loadedMatches = loadHistoryMatchesData.matches();

        assertEquals(10L, loadedMatches.getFirst().matchId());
        assertEquals(100L, loadedMatches.getFirst().playerId());
        assertEquals(120L, loadedMatches.getFirst().opponentsId());

        assertEquals(20L, loadedMatches.getLast().matchId());
        assertEquals(100L, loadedMatches.getLast().playerId());
        assertEquals(120L, loadedMatches.getLast().opponentsId());

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

        assertEquals(CharacterType.AMETHYST_ENCHANTRESS, loadedMatches.getFirst().playerCharacters().getFirst().characterType());
        assertEquals(10, loadedMatches.getFirst().playerCharacters().getFirst().level());
        assertEquals(CharacterType.RUBY_HORNED_DAME, loadedMatches.getFirst().playerCharacters().get(1).characterType());
        assertEquals(12, loadedMatches.getFirst().playerCharacters().get(1).level());
        assertEquals(CharacterType.HONEY_TRIGGER, loadedMatches.getFirst().playerCharacters().getLast().characterType());
        assertEquals(9, loadedMatches.getFirst().playerCharacters().getLast().level());

        assertEquals(CharacterType.TRASH_MAN, loadedMatches.getLast().opponentCharacters().getFirst().characterType());
        assertEquals(13, loadedMatches.getLast().opponentCharacters().getFirst().level());
        assertEquals(CharacterType.SACRED_CAT, loadedMatches.getLast().opponentCharacters().get(1).characterType());
        assertEquals(12, loadedMatches.getLast().opponentCharacters().get(1).level());
        assertEquals(CharacterType.EMERALD_CORE_KNIGHT, loadedMatches.getLast().opponentCharacters().getLast().characterType());
        assertEquals(11, loadedMatches.getLast().opponentCharacters().getLast().level());

        assertTrue(loadedMatches.getFirst().isPlayerWinner());
        assertFalse(loadedMatches.getLast().isPlayerWinner());
    }

    private LoadHistoryMatchesData getLoadHistoryMatchesData() {
        final List<MatchFromHistoryData> matches = List.of(
                new MatchFromHistoryData(
                    10L,
                    100L,
                    "test-user1",
                    120L,
                    "test-user2",
                    finishTime,
                    20,
                    -10,
                    100,
                    100,
                    List.of(
                            new HistoryCharacterData(CharacterType.AMETHYST_ENCHANTRESS, 10),
                            new HistoryCharacterData(CharacterType.RUBY_HORNED_DAME, 12),
                            new HistoryCharacterData(CharacterType.HONEY_TRIGGER, 9)
                    ),
                    List.of(
                            new HistoryCharacterData(CharacterType.TRASH_MAN, 13),
                            new HistoryCharacterData(CharacterType.SACRED_CAT, 12),
                            new HistoryCharacterData(CharacterType.EMERALD_CORE_KNIGHT, 11)
                    ),
                    true

                ),
                new MatchFromHistoryData(
                    20L,
                    100L,
                    "test-user1",
                    120L,
                    "test-user2",
                    finishTime + 10000,
                    20,
                    -10,
                    100,
                    100,
                    List.of(
                            new HistoryCharacterData(CharacterType.AMETHYST_ENCHANTRESS, 10),
                            new HistoryCharacterData(CharacterType.RUBY_HORNED_DAME, 12),
                            new HistoryCharacterData(CharacterType.HONEY_TRIGGER, 9)
                    ),
                    List.of(
                            new HistoryCharacterData(CharacterType.TRASH_MAN, 13),
                            new HistoryCharacterData(CharacterType.SACRED_CAT, 12),
                            new HistoryCharacterData(CharacterType.EMERALD_CORE_KNIGHT, 11)
                    ),
                    false
                )
        );

        return new LoadHistoryMatchesData(matches, false);
    }

}
