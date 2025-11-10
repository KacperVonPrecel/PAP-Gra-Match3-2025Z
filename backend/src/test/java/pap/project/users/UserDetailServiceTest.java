package pap.project.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceTest
{
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailService userDetailService;
    @Test
    void test_load_user_by_username_user_exists()
    {
        final User user = new User("test-user", "password");

        when(userRepository.findByUsername("test-user"))
                .thenReturn(Optional.of(user));

        final UserDetails userDetails = userDetailService.loadUserByUsername("test-user");

        assertEquals("test-user", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void test_load_user_by_username_user_not_exists()
    {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailService.loadUserByUsername("not-existing-user"),
                "User not founded: not-existing-user");
    }

}
