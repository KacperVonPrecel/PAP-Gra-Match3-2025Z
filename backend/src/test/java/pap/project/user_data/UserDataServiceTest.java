package pap.project.user_data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.game_history.MatchRepository;
import pap.project.user_data.model.UserData;
import pap.project.users.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private UserCharactersRepository userCharactersRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private UserDataService userDataService;

    @Test
    public void test_get_user_data_no_user_data_in_memory()
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

    @Test
    public void test_process_game_end_no_user_data_in_memory()
    {
        final List<UserCharacter> userCharacters1 = List.of(new UserCharacter(CharacterType.FIRST_CHARACTER, 1, 1, 1));
        when(userCharactersRepository.findAllByUserId(1)).thenReturn(userCharacters1);

        final List<UserCharacter> userCharacters2 = List.of(new UserCharacter(CharacterType.FIRST_CHARACTER, 2, 1, 1));
        when(userCharactersRepository.findAllByUserId(2)).thenReturn(userCharacters2);

        userDataService.processGameEnd(1, 2, 123);

        verify(userCharactersRepository, times(1)).findAllByUserId(1);
        verify(userCharactersRepository, times(1)).findAllByUserId(2);

        // XXX end this test, after making loading user data from db
    }

    // XXX make tests for proccessGameEnd after makeing loading user data from db
}
