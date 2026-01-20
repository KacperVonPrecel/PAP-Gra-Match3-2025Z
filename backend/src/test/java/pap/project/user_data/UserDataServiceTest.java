package pap.project.user_data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pap.project.characters.UserCharactersService;
import pap.project.characters.model.controller.CharacterData;
import pap.project.game.model.communication.PlayerStatChange;
import pap.project.game_history.Match;
import pap.project.game_history.MatchRepository;
import pap.project.game_history.model.HistoryCharacterData;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.UserSessionData;
import pap.project.user_data.model.controller.*;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.characters.UserCharacter;
import pap.project.characters.UserCharactersRepository;
import pap.project.characters.model.CharacterType;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static pap.project.users.User.createUserForTests;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceTest
{
    @Mock
    private UserStatsRepository userStatsRepository;

    @Mock
    private UserCharactersRepository userCharactersRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCharactersService userCharactersService;

    @InjectMocks
    private UserDataService userDataService;

    @Test
    public void test_get_user_data_no_user_data_in_memory()
    {
        final User mockedkUser = createUserForTests(10L, "test_user1", "test_user1@gmail.com", "password");
        final List<UserCharacter> userCharacters = List.of(new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, 1, 1, 1));
        when(userCharactersRepository.findAllByUserId(anyLong())).thenReturn(userCharacters);
        when(userStatsRepository.findUserStatsById(anyLong())).thenReturn(Optional.of(new UserStats(mockedkUser)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockedkUser));

        final UserData userData = userDataService.getUserData(10L);

        assertEquals(userCharacters, userData.userCharacters());
        assertEquals("test_user1", userData.username());

        verify(userCharactersRepository, times(1)).findAllByUserId(10L);
        verify(userStatsRepository, times(1)).findUserStatsById(10L);
        verify(userRepository, times(1)).findById(10L);
    }


    @Test
    public void test_get_user_data_get_data_from_memory_if_loaded_previously()
    {
        final User mockedUser = createUserForTests(10L, "test_user1", "test_user1@gmail.com", "password");
        final List<UserCharacter> userCharacters = List.of(new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, 1, 1, 1));
        when(userCharactersRepository.findAllByUserId(anyLong())).thenReturn(userCharacters);
        when(userStatsRepository.findUserStatsById(anyLong())).thenReturn(Optional.of(new UserStats(mockedUser)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockedUser));

        final UserData userData = userDataService.getUserData(10L);

        assertEquals(userCharacters, userData.userCharacters());
        verify(userCharactersRepository, times(1)).findAllByUserId(10L);

        final UserData userData2 = userDataService.getUserData(10L);
        assertEquals(userCharacters, userData2.userCharacters());

        verify(userCharactersRepository, times(1)).findAllByUserId(10L);
    }

    @Test
    public void test_process_game_end_no_user_data_in_memory()
    {
        final User mockedUser1 = createUserForTests(10L, "test_user1", "test_user1@gmail.com", "password");
        final User mockedUser2 = createUserForTests(20L, "test_user2", "test_user2@gmail.com", "password");

        final List<UserCharacter> userCharacters1 = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, 10L, 1, 1),
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, 10L, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, 10L, 1, 1)
        );
        when(userCharactersRepository.findAllByUserId(10L)).thenReturn(userCharacters1);
        when(userStatsRepository.findUserStatsById(10L)).thenReturn(Optional.of(new UserStats(mockedUser1)));
        when(userRepository.findById(10L)).thenReturn(Optional.of(mockedUser1));


        final List<UserCharacter> userCharacters2 = List.of(
                new UserCharacter(CharacterType.TRASH_MAN, 20L, 2, 1),
                new UserCharacter(CharacterType.SACRED_CAT, 20L, 2, 1),
                new UserCharacter(CharacterType.EMERALD_CORE_KNIGHT, 20L, 2, 1)
        );
        when(userCharactersRepository.findAllByUserId(20L)).thenReturn(userCharacters2);
        when(userStatsRepository.findUserStatsById(20L)).thenReturn(Optional.of(new UserStats(mockedUser2)));
        when(userRepository.findById(20L)).thenReturn(Optional.of(mockedUser2));

        final List<HistoryCharacterData> historyCharacterDataUser1 = List.of(
                new HistoryCharacterData(CharacterType.AMETHYST_ENCHANTRESS, 1),
                new HistoryCharacterData(CharacterType.RUBY_HORNED_DAME, 1),
                new HistoryCharacterData(CharacterType.HONEY_TRIGGER, 1)
        );

        final List<HistoryCharacterData> historyCharacterDataUser2 = List.of(
                new HistoryCharacterData(CharacterType.TRASH_MAN, 2),
                new HistoryCharacterData(CharacterType.SACRED_CAT, 2),
                new HistoryCharacterData(CharacterType.EMERALD_CORE_KNIGHT, 2)
        );

        Map<Long, PlayerStatChange> result = userDataService.processGameEnd(10L, 20L, 123, historyCharacterDataUser1, historyCharacterDataUser2);

        verify(userCharactersRepository, times(1)).findAllByUserId(10L);
        verify(userCharactersRepository, times(1)).findAllByUserId(20L);

        verify(userStatsRepository, times(1)).updateUserStatsAfterGameEnd(
                eq(1),
                eq(1),
                eq(120),
                eq(1500),
                eq(10L)
        );

        verify(userStatsRepository, times(1)).updateUserStatsAfterGameEnd(
                eq(1),
                eq(0),
                eq(90),
                eq(1200),
                eq(20L)
        );

        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(1)).save(matchCaptor.capture());

        Match savedMatch = matchCaptor.getValue();
        assertEquals(10L, savedMatch.getWinnerId());
        assertEquals(20L, savedMatch.getLoserId());
        assertEquals(123, savedMatch.getFinishTime());
        assertEquals(20, savedMatch.getWinnerEloChange());
        assertEquals(-10, savedMatch.getLoserEloChange());

        assertNotNull(savedMatch.getMatchCharacters());
        assertEquals(CharacterType.AMETHYST_ENCHANTRESS, savedMatch.getMatchCharacters().getWinnerFirstCharacterRecord().characterType());
        assertEquals(CharacterType.TRASH_MAN, savedMatch.getMatchCharacters().getLoserFirstCharacterRecord().characterType());

        assertNotNull(result);
        assertEquals(20, result.get(10L).elo());
        assertEquals(500, result.get(10L).money());
        assertEquals(-10, result.get(20L).elo());
        assertEquals(200, result.get(20L).money());
    }

    @Test
    public void test_process_game_end_if_user_data_in_memory()
    {
        final User mockedUser1 = createUserForTests(10L, "test_user1", "test_user1@gmail.com", "password");
        final User mockedUser2 = createUserForTests(20L, "test_user2", "test_user2@gmail.com", "password");

        final List<UserCharacter> userCharacters1 = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, 10L, 1, 1),
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, 10L, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, 10L, 1, 1)
        );

        final List<UserCharacter> userCharacters2 = List.of(
                new UserCharacter(CharacterType.TRASH_MAN, 20L, 2, 1),
                new UserCharacter(CharacterType.SACRED_CAT, 20L, 2, 1),
                new UserCharacter(CharacterType.EMERALD_CORE_KNIGHT, 20L, 2, 1)
        );

        UserSessionData winnerSession = new UserSessionData();
        UserData winnerData = new UserData(
                "test_user1",
                userCharacters1,
                List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.RUBY_HORNED_DAME, CharacterType.HONEY_TRIGGER),
                1000,
                100,
                0,
                0
        );
        winnerSession.setUserData(winnerData);

        UserSessionData loserSession = new UserSessionData();
        UserData loserData = new UserData(
                "test_user2",
                userCharacters2,
                List.of(CharacterType.TRASH_MAN, CharacterType.SACRED_CAT, CharacterType.EMERALD_CORE_KNIGHT),
                1000,
                100,
                0,
                0
        );
        loserSession.setUserData(loserData);

        ConcurrentHashMap<Long, UserSessionData> cacheMap = new ConcurrentHashMap<>();
        cacheMap.put(10L, winnerSession);
        cacheMap.put(20L, loserSession);

        ReflectionTestUtils.setField(userDataService, "userSessionData", cacheMap);

        final List<HistoryCharacterData> historyCharacterDataUser1 = List.of(
                new HistoryCharacterData(CharacterType.AMETHYST_ENCHANTRESS, 1),
                new HistoryCharacterData(CharacterType.RUBY_HORNED_DAME, 1),
                new HistoryCharacterData(CharacterType.HONEY_TRIGGER, 1)
        );

        final List<HistoryCharacterData> historyCharacterDataUser2 = List.of(
                new HistoryCharacterData(CharacterType.TRASH_MAN, 2),
                new HistoryCharacterData(CharacterType.SACRED_CAT, 2),
                new HistoryCharacterData(CharacterType.EMERALD_CORE_KNIGHT, 2)
        );

        Map<Long, PlayerStatChange> result = userDataService.processGameEnd(10L, 20L, 123, historyCharacterDataUser1, historyCharacterDataUser2);

        verify(userRepository, never()).findById(anyLong());
        verify(userCharactersRepository, never()).findAllByUserId(anyLong());
        verify(userStatsRepository, never()).findUserStatsById(anyLong());

        verify(userStatsRepository, times(1)).updateUserStatsAfterGameEnd(
                eq(1),
                eq(1),
                eq(120),
                eq(1500),
                eq(10L)
        );

        verify(userStatsRepository, times(1)).updateUserStatsAfterGameEnd(
                eq(1),
                eq(0),
                eq(90),
                eq(1200),
                eq(20L)
        );

        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(1)).save(matchCaptor.capture());

        Match savedMatch = matchCaptor.getValue();
        assertEquals(10L, savedMatch.getWinnerId());
        assertEquals(20L, savedMatch.getLoserId());
        assertEquals(123, savedMatch.getFinishTime());
        assertEquals(20, savedMatch.getWinnerEloChange());
        assertEquals(-10, savedMatch.getLoserEloChange());

        assertNotNull(savedMatch.getMatchCharacters());
        assertEquals(CharacterType.AMETHYST_ENCHANTRESS, savedMatch.getMatchCharacters().getWinnerFirstCharacterRecord().characterType());
        assertEquals(CharacterType.TRASH_MAN, savedMatch.getMatchCharacters().getLoserFirstCharacterRecord().characterType());

        assertNotNull(result);
        assertEquals(20, result.get(10L).elo());
        assertEquals(500, result.get(10L).money());
        assertEquals(-10, result.get(20L).elo());
        assertEquals(200, result.get(20L).money());
    }

    @Test
    public void test_draw_characters_logic_consistency_NEW_CHARACTERS()
    {
        long userId = 10L;
        int amountToDraw = 5;
        int costPerDraw = 25; // COMMON
        int expectedTotalCost = amountToDraw * costPerDraw;

        UserData userData = new UserData("test_user", new ArrayList<>(), null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        when(userCharactersRepository.findByUserIdAndCharacterTypeIn(eq(userId), anySet()))
                .thenReturn(new ArrayList<>());

        DrawCharacterResponse response = userDataService.drawCharacters(new DrawCharacterRequest(DrawType.COMMON, amountToDraw), userId);

        assertEquals(1000 - expectedTotalCost, sessionData.getUserData().currency());

        int totalDrawnAmount = response.results().stream().mapToInt(DrawResultEntry::amount).sum();
        assertEquals(amountToDraw, totalDrawnAmount);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserCharacter>> captor = ArgumentCaptor.forClass(List.class);
        verify(userCharactersRepository).saveAll(captor.capture());
        List<UserCharacter> savedCharacters = captor.getValue();

        assertEquals(savedCharacters.size(), response.results().size());

        for (UserCharacter savedChar : savedCharacters) {
            assertEquals(1, savedChar.getLevel());
            long copiesInResponse = response.results().stream()
                    .filter(r -> r.characterType() == savedChar.getCharacterType())
                    .findFirst().orElseThrow()
                    .amount();
            assertEquals(copiesInResponse, savedChar.getCopiesCount());
        }

        verify(userStatsRepository, times(1)).updateUserStatsAfterDrawing(
                eq(1000 - expectedTotalCost),
                eq(userId)
        );
    }

    @Test
    public void test_draw_characters_logic_consistency_EXISTING_CHARACTERS_COPIES()
    {
        long userId = 10L;
        int amountToDraw = 5;
        int initialCopies = 50;

        UserData userData = new UserData("test_user", new ArrayList<>(), null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        when(userCharactersRepository.findByUserIdAndCharacterTypeIn(eq(userId), anySet()))
                .thenAnswer(invocation -> {
                    Set<CharacterType> requestedTypes = invocation.getArgument(1);
                    return requestedTypes.stream()
                            .map(type -> new UserCharacter(type, userId, 5, initialCopies)) // np. Level 5, 50 kopii
                            .collect(Collectors.toList());
                });

        DrawCharacterResponse response = userDataService.drawCharacters(new DrawCharacterRequest(DrawType.COMMON, amountToDraw), userId);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserCharacter>> captor = ArgumentCaptor.forClass(List.class);
        verify(userCharactersRepository).saveAll(captor.capture());
        List<UserCharacter> savedCharacters = captor.getValue();

        for (UserCharacter savedChar : savedCharacters) {
            int drawnAmount = response.results().stream()
                    .filter(r -> r.characterType() == savedChar.getCharacterType())
                    .findFirst().orElseThrow()
                    .amount();

            assertEquals(initialCopies + drawnAmount, savedChar.getCopiesCount());
            assertEquals(5, savedChar.getLevel());
        }

        verify(userStatsRepository, times(1)).updateUserStatsAfterDrawing(
                eq(875),
                eq(userId)
        );
    }

    @Test
    public void test_draw_characters_not_enough_money()
    {
        long userId = 10L;
        int amountToDraw = 5;

        final User mockedUser = createUserForTests(userId, "test_user1", "test_user1@gmail.com", "password");
        UserStats mockedUserStats = new UserStats(mockedUser);
        ReflectionTestUtils.setField(mockedUserStats, "currency", 0);

        final List<UserCharacter> userCharacters = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 1),
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );
        when(userCharactersRepository.findAllByUserId(10L)).thenReturn(userCharacters);
        when(userStatsRepository.findUserStatsById(10L)).thenReturn(Optional.of(mockedUserStats));
        when(userRepository.findById(10L)).thenReturn(Optional.of(mockedUser));

        final DrawCharacterRequest drawCharacterRequest = new DrawCharacterRequest(DrawType.COMMON, amountToDraw);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userDataService.drawCharacters(drawCharacterRequest, userId));
        assertEquals("XXX", exception.getMessage());
    }

    @Test
    public void test_upgrade_character_successful_user_data_not_in_memory()
    {
        long userId = 10L;
        final User mockedUser = createUserForTests(userId, "test_user", "test_user1@gmail.com", "password");

        UserCharacter charToUpgrade = new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 10);

        final List<UserCharacter> userCharacters = List.of(
                charToUpgrade,
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );
        when(userCharactersRepository.findAllByUserId(10L)).thenReturn(userCharacters);
        when(userStatsRepository.findUserStatsById(10L)).thenReturn(Optional.of(new UserStats(mockedUser)));
        when(userRepository.findById(10L)).thenReturn(Optional.of(mockedUser));

        CharacterData level1Data = new CharacterData(CharacterType.AMETHYST_ENCHANTRESS, 150, 1000, 1, OptionalInt.of(10), 10);
        CharacterData level2Data = new CharacterData(CharacterType.AMETHYST_ENCHANTRESS, 180, 1100, 1, OptionalInt.of(20), 10);

        when(userCharactersService.createCharacterData(any()))
                .thenReturn(level1Data)
                .thenReturn(level2Data);

        final UpgradeCharacterRequest newRequest = new UpgradeCharacterRequest(CharacterType.AMETHYST_ENCHANTRESS);
        UpgradeCharacterResponse response = userDataService.upgradeCharacter(newRequest, userId);

        assertNotNull(response);
        assertEquals(180, response.characterData().damage());

        ArgumentCaptor<UserCharacter> captor = ArgumentCaptor.forClass(UserCharacter.class);
        verify(userCharactersRepository, times(1)).save(captor.capture());

        UserCharacter savedCharacter = captor.getValue();
        assertEquals(CharacterType.AMETHYST_ENCHANTRESS, savedCharacter.getCharacterType());
        assertEquals(2, savedCharacter.getLevel());
        assertEquals(0, savedCharacter.getCopiesCount());
    }

    @Test
    public void test_upgrade_character_successful_user_data_in_memory()
    {
        long userId = 10L;
        UserCharacter charToUpgrade = new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 10);

        final List<UserCharacter> userCharacters = List.of(
                charToUpgrade,
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );

        UserData userData = new UserData("test_user", userCharacters, null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        CharacterData level1Data = new CharacterData(CharacterType.AMETHYST_ENCHANTRESS, 150, 1000, 1, OptionalInt.of(10), 10);
        CharacterData level2Data = new CharacterData(CharacterType.AMETHYST_ENCHANTRESS, 180, 1100, 1, OptionalInt.of(20), 10);

        when(userCharactersService.createCharacterData(any()))
                .thenReturn(level1Data)
                .thenReturn(level2Data);

        final UpgradeCharacterRequest newRequest = new UpgradeCharacterRequest(CharacterType.AMETHYST_ENCHANTRESS);
        UpgradeCharacterResponse response = userDataService.upgradeCharacter(newRequest, userId);

        verify(userRepository, never()).findById(anyLong());
        verify(userCharactersRepository, never()).findAllByUserId(anyLong());
        verify(userStatsRepository, never()).findUserStatsById(anyLong());

        assertNotNull(response);
        assertEquals(180, response.characterData().damage());

        ArgumentCaptor<UserCharacter> captor = ArgumentCaptor.forClass(UserCharacter.class);
        verify(userCharactersRepository, times(1)).save(captor.capture());

        UserCharacter savedCharacter = captor.getValue();
        assertEquals(CharacterType.AMETHYST_ENCHANTRESS, savedCharacter.getCharacterType());
        assertEquals(2, savedCharacter.getLevel());
        assertEquals(0, savedCharacter.getCopiesCount());
    }

    @Test
    public void test_upgrade_character_not_enough_copies()
    {
        long userId = 10L;
        UserCharacter charToUpgrade = new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 8);

        final List<UserCharacter> userCharacters = List.of(
                charToUpgrade,
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );

        UserData userData = new UserData("test_user", userCharacters, null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        CharacterData level1Data = new CharacterData(CharacterType.AMETHYST_ENCHANTRESS, 150, 1000, 1, OptionalInt.of(10), 8);

        when(userCharactersService.createCharacterData(any()))
                .thenReturn(level1Data);

        final UpgradeCharacterRequest newRequest = new UpgradeCharacterRequest(CharacterType.AMETHYST_ENCHANTRESS);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDataService.upgradeCharacter(newRequest, userId));
        assertEquals("You don't have enough copies for this character", exception.getMessage());
    }

    @Test
    public void test_upgrade_character_not_owned_character()
    {
        long userId = 10L;
        final List<UserCharacter> userCharacters = List.of(
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );

        UserData userData = new UserData("test_user", userCharacters, null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        final UpgradeCharacterRequest newRequest = new UpgradeCharacterRequest(CharacterType.AMETHYST_ENCHANTRESS);
        assertThrows(Exception.class, () -> userDataService.upgradeCharacter(newRequest, userId));
    }

    @Test
    public void test_set_active_team_successful_user_data_in_memory()
    {
        long userId = 10L;
        final List<UserCharacter> userCharacters = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 10),
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );

        UserData userData = new UserData("test_user", userCharacters, null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        final List<CharacterType> newTeam = List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.RUBY_HORNED_DAME, CharacterType.HONEY_TRIGGER);

        final SetActiveTeamRequest request = new SetActiveTeamRequest(newTeam);
        final SetActiveTeamResponse response = userDataService.setActiveTeam(request, userId);

        assertNotNull(response);
        verify(userStatsRepository, times(1)).updateUserStatsActiveTeam(
                eq(newTeam),
                eq(userId)
        );
        assertNotNull(sessionData.getUserData().activeTeam());
        assertEquals(newTeam, sessionData.getUserData().activeTeam());
    }

    @Test
    public void test_set_active_team_successful_user_data_not_in_memory()
    {
        long userId = 10L;
        final User mockedUser = createUserForTests(userId, "test_user", "test_user1@gmail.com", "password");
        final List<UserCharacter> userCharacters = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 10),
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );
        when(userCharactersRepository.findAllByUserId(10L)).thenReturn(userCharacters);
        when(userStatsRepository.findUserStatsById(10L)).thenReturn(Optional.of(new UserStats(mockedUser)));
        when(userRepository.findById(10L)).thenReturn(Optional.of(mockedUser));

        final List<CharacterType> newTeam = List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.RUBY_HORNED_DAME, CharacterType.HONEY_TRIGGER);

        final SetActiveTeamRequest request = new SetActiveTeamRequest(newTeam);
        final SetActiveTeamResponse response = userDataService.setActiveTeam(request, userId);

        assertNotNull(response);
        verify(userStatsRepository, times(1)).updateUserStatsActiveTeam(
                eq(newTeam),
                eq(userId)
        );
    }

    @Test
    public void test_set_active_team_duplicate_characters()
    {
        long userId = 10L;
        final List<UserCharacter> userCharacters = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 10),
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );

        UserData userData = new UserData("test_user", userCharacters, null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        final List<CharacterType> newTeam = List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.AMETHYST_ENCHANTRESS, CharacterType.HONEY_TRIGGER);

        final SetActiveTeamRequest request = new SetActiveTeamRequest(newTeam);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDataService.setActiveTeam(request, userId));
        assertEquals("Team cannot contain any duplicate characters", exception.getMessage());
    }

    @Test
    public void test_set_active_team_not_owned_characters()
    {
        long userId = 10L;
        final List<UserCharacter> userCharacters = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, userId, 1, 10),
                new UserCharacter(CharacterType.RUBY_HORNED_DAME, userId, 1, 1),
                new UserCharacter(CharacterType.HONEY_TRIGGER, userId, 1, 1)
        );

        UserData userData = new UserData("test_user", userCharacters, null, 1000, 100, 0, 0);
        UserSessionData sessionData = new UserSessionData();
        sessionData.setUserData(userData);
        ReflectionTestUtils.setField(userDataService, "userSessionData", new ConcurrentHashMap<>(Map.of(userId, sessionData)));

        final List<CharacterType> newTeam = List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.EMERALD_CORE_KNIGHT, CharacterType.HONEY_TRIGGER);

        final SetActiveTeamRequest request = new SetActiveTeamRequest(newTeam);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDataService.setActiveTeam(request, userId));
        assertEquals("User doesn't have some of the selected characters", exception.getMessage());
    }

}
