package pap.project.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest
{
    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void init()
    {
        final User user = new User("test-user", "test-user@gmail.com", "password");
        userRepository.save(user);
    }

    @Test
    void test_exists_by_username()
    {
        assertTrue(userRepository.existsByUsername("test-user"));
        assertFalse(userRepository.existsByUsername("not-existing-user"));
    }

    @Test
    void test_exists_by_email()
    {
        assertTrue(userRepository.existsByEmail("test-user@gmail.com"));
        assertFalse(userRepository.existsByEmail("not-exisiting-user@gmail.com"));
    }

    @Test
    void test_get_by_username()
    {
        final Optional<User> founded = userRepository.findByUsername("test-user");
        assertTrue(founded.isPresent());

        assertEquals("test-user", founded.get().getUsername());
        assertEquals("password", founded.get().getHashedPassword());

        final Optional<User> notFounded = userRepository.findByUsername("not-existing-user");
        assertTrue(notFounded.isEmpty());
    }

    //XXXK add tests for updateUserAfterEndGame - it should be other file after adding additional table for user data.
}
