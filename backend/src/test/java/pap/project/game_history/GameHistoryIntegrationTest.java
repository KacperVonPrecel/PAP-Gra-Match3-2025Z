package pap.project.game_history;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.users.User;
import pap.project.users.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@ActiveProfiles("test")
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

    private final long finishTime = 1000166400;

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
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        final User user2 = new User("test-user2", "test-user2@gmail.com", "password2");
        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        matchRepository.save(
                new Match(
                        user1.getId().orElseThrow(),
                        user2.getId().orElseThrow(),
                        finishTime,
                        20,
                        -10
                ));

        matchRepository.save(
                new Match(
                        user2.getId().orElseThrow(),
                        user1.getId().orElseThrow(),
                        finishTime + 10000,
                        10,
                        -5
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                        .param("size", "10")
                        .param("userId", String.valueOf(user1.getId().orElseThrow()))
                        .param("latestRecordTime", String.valueOf(finishTime + 5000))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerId").value(user1.getId().orElseThrow()))
                .andExpect(jsonPath("$[0].playerUsername").value("test-user1"))
                .andExpect(jsonPath("$[0].opponentsUsername").value("test-user2"))
                .andExpect(jsonPath("$[0].playerEloPoints").value(100))
                .andExpect(jsonPath("$[0].opponentsEloPoints").value(100));
    }

    @Test
    @WithMockUser
    public void test_load_matches_failure() throws Exception
    {
        final User user = new User("test-user1", "test-user1@gmail.com", "password1");
        userRepository.saveAndFlush(user);

        when(matchRepository.findMatchesBeforeFinishTime(anyLong(), anyLong(), any(Pageable.class)))
                .thenThrow(new PersistenceException("Database error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                    .param("size", "10")
                    .param("userId", String.valueOf(user.getId().orElseThrow()))
                    .param("latestRecordTime", String.valueOf(finishTime + 5000))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // XXXK tests for user which is not logged
    // invalid input for controller, for example too large size etc. - if this test don't pass probably it's missing @Valid (I'm not sure if it required) annotation on controller method params.
    // for returning empty list of matches
    // saving more than 10 matches in history, than check if load keep size limit. Then use bigger size check if it loaded
    // check if latestRecordTime (or latestId after change) works, and not return invalid records
    // and maybe something more if u have more ideas.




}
