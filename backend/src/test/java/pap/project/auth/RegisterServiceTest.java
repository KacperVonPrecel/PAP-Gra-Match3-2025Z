package pap.project.auth;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pap.project.auth.model.RegisterResult;
import pap.project.auth.model.controller.register.RegisterRequest;
import pap.project.users.User;
import pap.project.users.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest
{
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterService registerService;

    private final RegisterRequest registerRequest = new RegisterRequest("test-user", "test-user@gmail.com", "password");

    @Test
    void test_register_user_repeated_username()
    {
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(true);
        final RegisterResult result = registerService.registerUser("", registerRequest);
        assertEquals(RegisterResult.USERNAME_REPEATED, result);
    }

    @Test
    void test_register_user_repeated_email()
    {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);
        final RegisterResult result = registerService.registerUser("", registerRequest);
        assertEquals(RegisterResult.EMAIL_REPEATED, result);
    }

    @Test
    void test_register_user_error_while_saving()
    {
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("password");
        when(userRepository.save(any())).thenThrow(PersistenceException.class);
        final RegisterResult result = registerService.registerUser("", registerRequest);
        assertEquals(RegisterResult.DATABASE_ERROR, result);
    }

    @Test
    void test_register_user_registered()
    {
        //XXX repair

        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("password");
        when(userRepository.save(any())).thenReturn(User.createUserForTests(1, registerRequest.username(), registerRequest.email(), "password"));
        final RegisterResult result = registerService.registerUser("", registerRequest);
        assertEquals(RegisterResult.REGISTERED, result);
    }
}
