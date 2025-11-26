package pap.project.game_history;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.game_history.controller.model.load.LoadRequest;
import pap.project.game_history.controller.model.load.MatchProjectionForController;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class GameHistoryIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private MatchRepository matchRepository;

    @MockitoSpyBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final long MOCK_FINISH_TIME = 1000166400;

    @BeforeEach
    void init ()
    {
        matchRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void test_load_matches_success() throws Exception
    {
        userRepository.save(new User("test-user1", "test-user1@gmail.com", passwordEncoder.encode("password1")));
        userRepository.save(new User("test-user2", "test-user2@gmail.com", passwordEncoder.encode("password2")));

        matchRepository.save(
                new Match(
                    userRepository.findByUsername("test-user1").get(),
                    userRepository.findByUsername("test-user2").get(),
                    MOCK_FINISH_TIME,
                    20,
                    -10
                ));

        matchRepository.save(
                new Match(
                        userRepository.findByUsername("test-user2").get(),
                        userRepository.findByUsername("test-user1").get(),
                        MOCK_FINISH_TIME + 10000,
                        10,
                        -5
                ));

        final long userOneId = userRepository.findByUsername("test-user1").get().getId().getAsLong();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/load_matches/load")
                        .param("size", "10")
                        .param("userId", String.valueOf(userOneId))
                        .param("latestRecordTime", String.valueOf(MOCK_FINISH_TIME + 5000))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerId").value(userOneId))
                .andExpect(jsonPath("$[0].playerUsername").value("test-user1"))
                .andExpect(jsonPath("$[0].opponentsUsername").value("test-user2"))
                .andExpect(jsonPath("$[0].playerEloPoints").value(100))
                .andExpect(jsonPath("$[0].opponentsEloPoints").value(100));
    }

    @Test
    @WithMockUser
    public void test_load_matches_failure() throws Exception
    {
        userRepository.save(new User("test-user1", "test-user1@gmail.com", passwordEncoder.encode("password1")));
        final long userOneId = userRepository.findByUsername("test-user1").get().getId().getAsLong();

        when(matchRepository.findMatchesBeforeFinishTime(anyLong(), anyLong(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/load_matches/load")
                    .param("size", "10")
                    .param("userId", String.valueOf(userOneId))
                    .param("latestRecordTime", String.valueOf(MOCK_FINISH_TIME + 5000))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }


}
