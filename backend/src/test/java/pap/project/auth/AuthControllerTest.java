package pap.project.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.SecurityContextRepository;
import pap.project.auth.model.RegisterResult;
import pap.project.auth.model.controller.login.LoginRequest;
import pap.project.auth.model.controller.register.RegisterError;
import pap.project.auth.model.controller.register.RegisterErrorResponse;
import pap.project.auth.model.controller.register.RegisterRequest;
import pap.project.auth.model.controller.register.RegisterResponse;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest
{

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RegisterService registerService;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @InjectMocks
    private AuthController authController;

    private final LoginRequest loginRequest = new LoginRequest("test-user", "password");
    private final RegisterRequest request = new RegisterRequest("test-user", "test-user@gmail.com", "password");

    @Test
    void test_login_succes()
    {
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        final ResponseEntity<?> response = authController.login(loginRequest, httpRequest, httpResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(securityContextRepository, times(1))
                .saveContext(any(), eq(httpRequest), eq(httpResponse));
    }

    @Test
    void test_login_failure_invalid_credentials()
    {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        final ResponseEntity<?> response = authController.login(loginRequest, httpRequest, httpResponse);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid login data", response.getBody());
        verify(securityContextRepository, never()).saveContext(any(), any(), any());
    }

    @Test
    void test_register_success()
    {
        when(registerService.registerUser(anyString(), eq(request)))
                .thenReturn(RegisterResult.REGISTERED);

        final ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        assertInstanceOf(RegisterResponse.class, response.getBody());
    }

    @Test
    void test_register_username_repeated()
    {
        final RegisterRequest request = new RegisterRequest("test-user", "test-user@gmail.com", "password");

        when(registerService.registerUser(anyString(), eq(request)))
                .thenReturn(RegisterResult.USERNAME_REPEATED);

        final ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.hasBody());
        assertInstanceOf(RegisterErrorResponse.class, response.getBody());
        final RegisterErrorResponse responseBody = Objects.requireNonNull((RegisterErrorResponse) response.getBody());
        assertEquals(RegisterError.USERNAME_TAKEN, responseBody.error());
    }

    @Test
    void test_register_email_repeated()
    {
        final RegisterRequest request = new RegisterRequest("test-user", "test-user@gmail.com", "password");

        when(registerService.registerUser(anyString(), eq(request)))
                .thenReturn(RegisterResult.EMAIL_REPEATED);

        final ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.hasBody());
        assertInstanceOf(RegisterErrorResponse.class, response.getBody());
        final RegisterErrorResponse responseBody = Objects.requireNonNull((RegisterErrorResponse) response.getBody());
        assertEquals(RegisterError.EMAIL_TAKEN, responseBody.error());
    }

    @Test
    void test_register_database_error()
    {
        final RegisterRequest request = new RegisterRequest("test-user", "test-user@gmail.com","password");

        when(registerService.registerUser(anyString(), eq(request)))
                .thenReturn(RegisterResult.DATABASE_ERROR);

        final ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.hasBody());
        assertInstanceOf(RegisterErrorResponse.class, response.getBody());
        final RegisterErrorResponse responseBody = Objects.requireNonNull((RegisterErrorResponse) response.getBody());
        assertEquals(RegisterError.INTERNAL_SERVER_ERROR, responseBody.error());
    }
}
