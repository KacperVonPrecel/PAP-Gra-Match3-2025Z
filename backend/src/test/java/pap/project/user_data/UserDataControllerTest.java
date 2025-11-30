package pap.project.user_data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.controller.StartingDataResponse;
import pap.project.users.UserAuthDetails;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersService;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.characters.model.controller.CharacterData;

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
    private Authentication authentication;

    @InjectMocks
    private UserDataController userDataController;

    @Test
    public void test_get_starting_data()
    {
        when(authentication.getPrincipal()).thenReturn(new UserAuthDetails("test-user", "password", 1));
        final UserData userData = new UserData(List.of(new UserCharacter(CharacterType.FIRST_CHARACTER, 1, 10, 10)), 100, 0, 0, 0);
        final List<CharacterData> charactersData = List.of(new CharacterData(CharacterType.FIRST_CHARACTER, 100, 100, 1, OptionalInt.empty(), 10));
        when(userDataService.getUserData(1)).thenReturn(userData);
        when(userCharactersService.createCharactersData(userData.userCharacters())).thenReturn(charactersData);

        final StartingDataResponse ret = userDataController.getStartingData(authentication);
        assertEquals(userData.currency(), ret.currency());
        assertEquals(charactersData, ret.characters());
    }
}
