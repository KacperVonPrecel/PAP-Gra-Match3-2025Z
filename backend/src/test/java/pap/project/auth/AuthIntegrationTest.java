package pap.project.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.auth.model.controller.login.LoginRequest;
import pap.project.auth.model.controller.register.RegisterError;
import pap.project.auth.model.controller.register.RegisterErrorResponse;
import pap.project.auth.model.controller.register.RegisterRequest;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@ActiveProfiles("test")
public class AuthIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private UserRepository userRepository;

    @MockitoSpyBean
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void init()
    {
        userRepository.deleteAll();
    }

    /** One test if mapper return enums in proper format, to be able using this mapper in test checks, instead of checking text JSON in each test. */
    @Test
    public void test_mapper_enum_return() throws JsonProcessingException
    {
        assertEquals("{\"error\":\"USERNAME_TAKEN\"}", mapper.writeValueAsString(new RegisterErrorResponse(RegisterError.USERNAME_TAKEN)));
        assertEquals("{\"error\":\"EMAIL_TAKEN\"}", mapper.writeValueAsString(new RegisterErrorResponse(RegisterError.EMAIL_TAKEN)));
        assertEquals("{\"error\":\"INTERNAL_SERVER_ERROR\"}", mapper.writeValueAsString(new RegisterErrorResponse(RegisterError.INTERNAL_SERVER_ERROR)));
    }

    @Test
    @WithAnonymousUser
    public void test_login_success() throws Exception
    {
        userRepository.save(new User("testuser", "test-user@gmail.com", passwordEncoder.encode("password123")));
        final LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

    }

    @Test
    @WithAnonymousUser
    public void test_login_invalid_credentials() throws Exception
    {
        final LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid login data"));
    }

    @ParameterizedTest
    @MethodSource("invalid_login_payloads")
    void test_login_invalid_request(String payload) throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    private static @NonNull Stream<String> invalid_login_payloads() throws JsonProcessingException
    {
        final ObjectMapper mapper = new ObjectMapper();
        final String validUsername = "test-user";
        final String validPassword = "password123";

        final String shortUsername = "a".repeat(LoginRequest.MIN_USERNAME_LENGTH - 1);
        final String longUsername  = "a".repeat(LoginRequest.MAX_USERNAME_LENGTH + 1);

        final String shortPassword = "a".repeat(LoginRequest.MIN_PASSWORD_LENGTH - 1);
        final String longPassword  = "a".repeat(LoginRequest.MAX_PASSWORD_LENGTH + 1);

        return Stream.of(
                "",
                "{}",
                "{\"username\":\"" + validUsername + "\"}",
                "{\"password\":\"" + validPassword + "\"}",

                mapper.writeValueAsString(new LoginRequest(shortUsername, validPassword)),
                mapper.writeValueAsString(new LoginRequest(longUsername, validPassword)),

                mapper.writeValueAsString(new LoginRequest(validUsername, shortPassword)),
                mapper.writeValueAsString(new LoginRequest(validUsername, longPassword))
        );
    }


    @Test
    @WithAnonymousUser
    public void test_register_success() throws Exception
    {
        final RegisterRequest registerRequest = new RegisterRequest("test-user", "email@example.com", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(userRepository).existsByUsername("test-user");
        verify(userRepository).existsByEmail("email@example.com");
        verify(userRepository).save(argThat(user -> user.getUsername().equals("test-user")
                && user.getEmail().equals("email@example.com")
                && user.getHashedPassword().equals("encodedPassword123")
        ));
    }

    @Test
    @WithAnonymousUser
    public void test_register_username_taken() throws Exception
    {
        final RegisterRequest registerRequest = new RegisterRequest("test-user", "email@example.com", "password123");
        userRepository.save(new User("test-user", "email2@example.com", passwordEncoder.encode("password123")));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().json(mapper.writeValueAsString(new RegisterErrorResponse(RegisterError.USERNAME_TAKEN))));
    }


    @Test
    @WithAnonymousUser
    public void test_register_email_taken() throws Exception
    {
        final RegisterRequest registerRequest = new RegisterRequest("test-user", "email@example.com", "password123");
        userRepository.save(new User("test-user2", "email@example.com", passwordEncoder.encode("password123")));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().json(mapper.writeValueAsString(new RegisterErrorResponse(RegisterError.EMAIL_TAKEN))));
    }

    @ParameterizedTest
    @MethodSource("invalid_register_payloads")
    @WithAnonymousUser
    void test_register_invalid_request(String payload) throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    private static @NonNull Stream<String> invalid_register_payloads() throws JsonProcessingException
    {
        final ObjectMapper mapper = new ObjectMapper();
        final String validUsername = "test-user";
        final String validEmail = "test.email@example.com";
        final String validPassword = "password123";

        final String shortUsername = "a".repeat(LoginRequest.MIN_USERNAME_LENGTH - 1);
        final String longUsername  = "a".repeat(LoginRequest.MAX_USERNAME_LENGTH + 1);

        final String invalidEmail = "email";
        final String longEmail = "a".repeat(RegisterRequest.MAX_EMAIL_LENGTH - 11) + "@example.com";

        final String shortPassword = "a".repeat(LoginRequest.MIN_PASSWORD_LENGTH - 1);
        final String longPassword  = "a".repeat(LoginRequest.MAX_PASSWORD_LENGTH + 1);

        return Stream.of(
                "",
                "{}",
                "{\"username\":\"" + validUsername + "\"}",
                "{\"password\":\"" + validPassword + "\"}",
                "{\"email\":\"" + validEmail + "\"}",

                "{\"username\":\"" + validUsername + "\", \"password\":\"" + validPassword + "\"}",
                "{\"email\":\"" + validEmail + "\", \"password\":\"" + validPassword + "\"}",
                "{\"username\":\"" + validUsername + "\", \"email\":\"" + validEmail + "\"}",


                mapper.writeValueAsString(new RegisterRequest(shortUsername, validEmail, validPassword)),
                mapper.writeValueAsString(new RegisterRequest(longUsername, validEmail, validPassword)),

                mapper.writeValueAsString(new RegisterRequest(validUsername, longEmail, validPassword)),
                mapper.writeValueAsString(new RegisterRequest(validUsername, invalidEmail, validPassword)),

                mapper.writeValueAsString(new RegisterRequest(validUsername, validEmail, shortPassword)),
                mapper.writeValueAsString(new RegisterRequest(validUsername, validEmail, longPassword))

        );
    }

}