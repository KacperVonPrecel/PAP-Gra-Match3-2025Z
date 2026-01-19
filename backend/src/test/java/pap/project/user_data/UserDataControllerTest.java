package pap.project.user_data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.controller.UserDataResponse;
import pap.project.user_stats.RankingService;
import pap.project.users.UserAuthDetails;
import pap.project.characters.UserCharacter;
import pap.project.characters.UserCharactersService;
import pap.project.characters.model.CharacterType;
import pap.project.characters.model.controller.CharacterData;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
}
