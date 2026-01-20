package pap.project.user_data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.controller.*;
import pap.project.user_stats.RankingService;
import pap.project.users.UserAuthDetails;
import pap.project.characters.UserCharacter;
import pap.project.characters.UserCharactersService;
import pap.project.characters.model.CharacterType;
import pap.project.characters.model.controller.CharacterData;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDataControllerTest
{
    @Mock
    private UserDataService userDataService;
    @Mock
    private UserCharactersService userCharactersService;
    @Mock
    private RankingService rankingService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserDataController userDataController;

    @Test
    public void test_get_starting_data()
    {
        when(authentication.getPrincipal()).thenReturn(new UserAuthDetails("test-user", "password", 1));
        final UserData userData = new UserData(
                "test-user",
                List.of(
                        new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, 1, 10, 10),
                        new UserCharacter(CharacterType.EMERALD_CORE_KNIGHT, 1, 11, 12),
                        new UserCharacter(CharacterType.SACRED_CAT, 1, 9, 9)
                        ),
                List.of(
                        CharacterType.AMETHYST_ENCHANTRESS,
                        CharacterType.EMERALD_CORE_KNIGHT,
                        CharacterType.SACRED_CAT
                ),
                100,
                0,
                0,
                0);
        final List<CharacterData> charactersData =
                List.of(
                        new CharacterData(CharacterType.AMETHYST_ENCHANTRESS, 100, 100, 10, OptionalInt.empty(), 10),
                        new CharacterData(CharacterType.EMERALD_CORE_KNIGHT, 100, 100, 11, OptionalInt.empty(), 12),
                        new CharacterData(CharacterType.SACRED_CAT, 100, 100, 9, OptionalInt.empty(), 9)
                );
        when(userDataService.getUserData(1)).thenReturn(userData);
        when(userCharactersService.createCharactersData(userData.userCharacters())).thenReturn(charactersData);

        final UserDataResponse ret = userDataController.getStartingData(authentication);
        assertEquals(userData.currency(), ret.currency());
        assertEquals(charactersData, ret.characters());
    }

    @Test
    void test_draw_characters() {
        long userId = 10L;
        DrawCharacterRequest request = new DrawCharacterRequest(DrawType.COMMON, 10);
        DrawCharacterResponse expectedResponse = new DrawCharacterResponse(List.of());

        mockAuthenticatedUser(userId);
        when(userDataService.drawCharacters(request, userId)).thenReturn(expectedResponse);

        DrawCharacterResponse result = userDataController.drawCharacters(authentication, request);

        assertEquals(expectedResponse, result);
        verify(userDataService).drawCharacters(request, userId);
    }

    @Test
    void test_upgrade_character() {
        long userId = 20L;
        UpgradeCharacterRequest request = new UpgradeCharacterRequest(CharacterType.AMETHYST_ENCHANTRESS);
        UpgradeCharacterResponse expectedResponse = new UpgradeCharacterResponse(null);

        mockAuthenticatedUser(userId);
        when(userDataService.upgradeCharacter(request, userId)).thenReturn(expectedResponse);

        UpgradeCharacterResponse result = userDataController.upgradeCharacter(authentication, request);

        assertEquals(expectedResponse, result);
        verify(userDataService).upgradeCharacter(request, userId);
    }

    @Test
    void test_set_active_team() {
        long userId = 30L;
        SetActiveTeamRequest request = new SetActiveTeamRequest(List.of(CharacterType.TRASH_MAN));
        SetActiveTeamResponse expectedResponse = new SetActiveTeamResponse();

        mockAuthenticatedUser(userId);
        when(userDataService.setActiveTeam(request, userId)).thenReturn(expectedResponse);

        SetActiveTeamResponse result = userDataController.setActiveTeam(authentication, request);

        assertEquals(expectedResponse, result);
        verify(userDataService).setActiveTeam(request, userId);
    }

    @Test
    void test_user_stats_current_logged_user_param_null() {
        long loggedUserId = 40L;

        UserData mockedUserData = mock(UserData.class);
        when(mockedUserData.username()).thenReturn("test-user");
        when(mockedUserData.eloPoints()).thenReturn(1500);
        when(mockedUserData.matchWon()).thenReturn(10);
        when(mockedUserData.matchPlayed()).thenReturn(15);

        mockAuthenticatedUser(loggedUserId);
        when(userDataService.getUserData(loggedUserId)).thenReturn(mockedUserData);

        UserStatsResponse result = userDataController.userStats(authentication, null);

        verify(authentication, times(1)).getPrincipal();
        verify(userDataService).getUserData(loggedUserId);

        assertEquals("test-user", result.username());
        assertEquals(1500, result.elo());
        assertEquals(10, result.wins());
        assertEquals(5, result.loses());
    }

    @Test
    void test_user_stats_other_user_param_provided() {
        long requestedUserId = 99L;

        UserData mockedUserData = mock(UserData.class);
        when(mockedUserData.username()).thenReturn("test-user");
        when(mockedUserData.eloPoints()).thenReturn(1000);
        when(mockedUserData.matchWon()).thenReturn(0);
        when(mockedUserData.matchPlayed()).thenReturn(0);

        when(userDataService.getUserData(requestedUserId)).thenReturn(mockedUserData);

        UserStatsResponse result = userDataController.userStats(authentication, requestedUserId);

        verify(authentication, never()).getPrincipal();
        verify(userDataService).getUserData(requestedUserId);

        assertEquals("test-user", result.username());
    }

    private void mockAuthenticatedUser(long userId) {
        UserAuthDetails userAuthDetails = new UserAuthDetails("test-user", "password", userId);
        when(authentication.getPrincipal()).thenReturn(userAuthDetails);
    }
}
