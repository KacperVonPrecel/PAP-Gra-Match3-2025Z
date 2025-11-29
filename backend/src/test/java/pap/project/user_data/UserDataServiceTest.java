package pap.project.user_data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.user_data.model.UserData;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersRepository;
import pap.project.users.characters.model.CharacterType;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceTest
{
    @Mock
    private UserCharactersRepository userCharactersRepository;

    @InjectMocks
    private UserDataService userDataService;

    @Test
    public void test_get_user_data()
    {
        final List<UserCharacter> userCharacters = List.of(new UserCharacter(CharacterType.FIRST_CHARACTER, 1, 1, 1));
        when(userCharactersRepository.findAllByUserId(anyLong())).thenReturn(userCharacters);


        final UserData userData = userDataService.getUserData(1);

        assertEquals(userCharacters, userData.userCharacters());
        verify(userCharactersRepository, times(1)).findAllByUserId(1);
    }


    @Test
    public void test_get_user_data_get_data_from_memory_if_loaded_previously()
    {
        final List<UserCharacter> userCharacters = List.of(new UserCharacter(CharacterType.FIRST_CHARACTER, 1, 1, 1));
        when(userCharactersRepository.findAllByUserId(anyLong())).thenReturn(userCharacters);


        final UserData userData = userDataService.getUserData(1);

        assertEquals(userCharacters, userData.userCharacters());
        verify(userCharactersRepository, times(1)).findAllByUserId(1);

        final UserData userData2 = userDataService.getUserData(1);
        assertEquals(userCharacters, userData2.userCharacters());

        verify(userCharactersRepository, times(1)).findAllByUserId(1);

    }
}
