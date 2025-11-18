package pap.project.users.characters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.users.characters.model.CharacterType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCharactersServiceTest
{
    @Mock
    private UserCharactersRepository userCharactersRepository;

    @InjectMocks
    private UserCharactersService userCharactersService;


    @Test
    public void test_get_user_characters()
    {
        final List<UserCharacter> userCharacters = new ArrayList<>();
        userCharacters.add(new UserCharacter(CharacterType.FIRST_CHARACTER, 1, 12, 10));
        userCharacters.add(new UserCharacter(CharacterType.SECOND_CHARACTER, 1, 13, 2));
        when(userCharactersRepository.findAllByUserId(anyLong())).thenReturn(userCharacters);
        final List<UserCharacter> returnedUserCharacters = userCharactersService.getUserCharacters(1);
        assertEquals(userCharacters, returnedUserCharacters);
    }
}
