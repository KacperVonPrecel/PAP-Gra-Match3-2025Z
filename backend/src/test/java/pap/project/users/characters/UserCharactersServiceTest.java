package pap.project.users.characters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.users.characters.model.CharacterType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserCharactersServiceTest
{
    @Mock
    private UserCharactersRepository userCharactersRepository;

    @InjectMocks
    private UserCharactersService userCharactersService;

    @Test
    public void test_create_character_data_works_for_all_characters_type()
    {
        final List<UserCharacter> characters = Arrays.stream(CharacterType.values()).map(type -> new UserCharacter(type, 1, 1, 1)).toList();
        final var res = userCharactersService.createCharactersData(characters);
        assertNotNull(res);
        assertEquals(characters.size(), res.size());
    }
}
