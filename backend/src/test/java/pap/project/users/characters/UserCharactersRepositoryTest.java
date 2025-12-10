package pap.project.users.characters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = {"pap.project.users"})
@EnableJpaRepositories(basePackages = {"pap.project.users"})
public class UserCharactersRepositoryTest
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCharactersRepository userCharactersRepository;


    @BeforeEach
    public void init()
    {
        userRepository.deleteAll();
        userCharactersRepository.deleteAll();
    }

    @Test
    public void test_foreign_key_violation_if_user_not_exists()
    {
        assertThrows(DataIntegrityViolationException.class, () -> userCharactersRepository.saveAndFlush(new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, 1, 1, 1)));
    }

    @Test
    public void test_insert_if_user_exists()
    {
        final User user = new User("test-user", "test-user@example.com", "hashedPassword");
        userRepository.saveAndFlush(user);
        assertTrue(user.getId().isPresent());
        assertDoesNotThrow(() -> userCharactersRepository.saveAndFlush(new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, user.getId().getAsLong(), 1, 1)));
    }

    @Test
    public void test_unique_key_violation_on_the_inserting_same_character_for_user()
    {
        final User user = new User("test-user", "test-user@example.com", "hashedPassword");
        userRepository.save(user);
        assertTrue(user.getId().isPresent());
        assertDoesNotThrow(() -> userCharactersRepository.saveAndFlush(new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, user.getId().getAsLong(), 1, 1)));
        assertThrows(DataIntegrityViolationException.class, () -> userCharactersRepository.saveAndFlush(new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, user.getId().getAsLong(), 1, 1)));
    }


    @Test
    public void test_find_all_users_characters_by_user_id_not_existing_user()
    {
        assertFalse(userRepository.existsById(1L));
        final List<UserCharacter> userCharacters = userCharactersRepository.findAllByUserId(1);
        assertNotNull(userCharacters);
        assertEquals(0, userCharacters.size());
    }

    @Test
    public void test_find_all_users_characters_by_user_id_empty_list()
    {
        final User user = new User("test-user", "test-user@example.com", "hashedPassword");
        userRepository.saveAndFlush(user);
        assertTrue(user.getId().isPresent());

        final List<UserCharacter> userCharacters = userCharactersRepository.findAllByUserId(user.getId().getAsLong());
        assertNotNull(userCharacters);
        assertEquals(0, userCharacters.size());
    }


    @Test
    public void test_find_all_users_characters_by_user_id_not_empty_list()
    {
        final User user = new User("test-user", "test-user@example.com", "hashedPassword");
        userRepository.saveAndFlush(user);
        assertTrue(user.getId().isPresent());

        assertDoesNotThrow(() -> userCharactersRepository.saveAndFlush(new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, user.getId().getAsLong(), 1, 1)));
        assertDoesNotThrow(() -> userCharactersRepository.saveAndFlush(new UserCharacter(CharacterType.TRASH_MAN, user.getId().getAsLong(), 1, 1)));

        final List<UserCharacter> userCharacters = userCharactersRepository.findAllByUserId(user.getId().getAsLong());
        assertNotNull(userCharacters);
        assertEquals(2, userCharacters.size());
    }
}
