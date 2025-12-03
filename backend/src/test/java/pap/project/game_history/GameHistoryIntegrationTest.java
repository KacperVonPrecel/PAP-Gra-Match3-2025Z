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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

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

    @MockitoSpyBean
    private UserStatsRepository userStatsRepository;

    private final long finishTime = 1000166400;

    @BeforeEach
    void init ()
    {
        userStatsRepository.deleteAll();
        matchRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void test_load_matches_no_more_records() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        final User user2 = new User("test-user2", "test-user2@gmail.com", "password2");
        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        final UserStats userStats1 = new UserStats(user1.getId().orElseThrow());
        final UserStats userStats2 = new UserStats(user2.getId().orElseThrow());
        userStatsRepository.saveAllAndFlush(List.of(userStats1, userStats2));

        long i = 0;
        for(i = 0; i < 9; i++)
        {
            matchRepository.saveAndFlush(new Match(
                    user1.getId().orElseThrow(),
                    user2.getId().orElseThrow(),
                    finishTime + (i * 10000),
                    20,
                    -10
            ));
        }

        final Match newestMatch = new Match(
                user1.getId().orElseThrow(),
                user2.getId().orElseThrow(),
                finishTime + 100000,
                20,
                -10);
        matchRepository.saveAndFlush(newestMatch);
        final long newestMatchId = newestMatch.getId().orElseThrow();


        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                        .param("size", "10")
                        .param("userId", String.valueOf(user1.getId().orElseThrow()))
                        .param("latestRecordId", String.valueOf(newestMatchId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moreToLoad").isBoolean())
                .andExpect(jsonPath("$.moreToLoad").value(false))

                .andExpect(jsonPath("$.matches").isArray())
                .andExpect(jsonPath("$.matches").isNotEmpty())
                .andExpect(jsonPath("$.matches", hasSize(10)))

                .andExpect(jsonPath("$.matches[0].playerId").value(user1.getId().orElseThrow()))
                .andExpect(jsonPath("$.matches[0].playerUsername").value("test-user1"))
                .andExpect(jsonPath("$.matches[0].opponentsUsername").value("test-user2"))
                .andExpect(jsonPath("$.matches[0].playerEloPoints").value(100))
                .andExpect(jsonPath("$.matches[0].opponentsEloPoints").value(100))
                .andExpect(jsonPath("$.matches[0].isPlayerWinner").value(true));
    }

    @Test
    @WithMockUser
    public void test_load_matches_are_more_records() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        final User user2 = new User("test-user2", "test-user2@gmail.com", "password2");
        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        final UserStats userStats1 = new UserStats(user1.getId().orElseThrow());
        final UserStats userStats2 = new UserStats(user2.getId().orElseThrow());
        userStatsRepository.saveAllAndFlush(List.of(userStats1, userStats2));

        long i = 0;
        for(i = 0; i < 4; i++)
        {
            matchRepository.saveAndFlush(new Match(
                    user1.getId().orElseThrow(),
                    user2.getId().orElseThrow(),
                    finishTime + (i * 10000),
                    20,
                    -10
            ));
        }

        final Match matchFive = new Match(
                user1.getId().orElseThrow(),
                user2.getId().orElseThrow(),
                finishTime + 50000,
                20,
                -10);
        matchRepository.saveAndFlush(matchFive);

        for(i = 6; i < 14; i++)
        {
            matchRepository.saveAndFlush(new Match(
                    user1.getId().orElseThrow(),
                    user2.getId().orElseThrow(),
                    finishTime + (i * 10000),
                    20,
                    -10
            ));
        }

        final Match matchFourteen = new Match(
                user1.getId().orElseThrow(),
                user2.getId().orElseThrow(),
                finishTime + 140000,
                20,
                -10);
        matchRepository.saveAndFlush(matchFourteen);
        final long matchFifteenId = matchFourteen.getId().orElseThrow();

        matchRepository.saveAndFlush(new Match(
                user1.getId().orElseThrow(),
                user2.getId().orElseThrow(),
                finishTime + 150000,
                20,
                -10
        ));


        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                        .param("size", "10")
                        .param("userId", String.valueOf(user1.getId().orElseThrow()))
                        .param("latestRecordId", String.valueOf(matchFifteenId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moreToLoad").isBoolean())
                .andExpect(jsonPath("$.moreToLoad").value(true))

                .andExpect(jsonPath("$.matches").isArray())
                .andExpect(jsonPath("$.matches").isNotEmpty())
                .andExpect(jsonPath("$.matches", hasSize(10)))
                .andExpect(jsonPath("$.matches[9].finishTime").value(matchFive.getFinishTime()))
                .andExpect(jsonPath("$.matches[0].finishTime").value(matchFourteen.getFinishTime()));
    }

    @Test
    @WithMockUser
    public void test_load_matches_wrong_size() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        userRepository.saveAndFlush(user1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                    .param("size", "9")
                    .param("userId",  String.valueOf(user1.getId().orElseThrow()))
                    .param("latestRecordId", String.valueOf(1))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                        .param("size", "101")
                        .param("userId",  String.valueOf(user1.getId().orElseThrow()))
                        .param("latestRecordId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    public void test_load_matches_user_not_logged() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        userRepository.saveAndFlush(user1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                        .param("size", "10")
                        .param("userId",  String.valueOf(user1.getId().orElseThrow()))
                        .param("latestRecordId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void test_load_matches_user_not_found_in_DB() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                        .param("size", "10")
                        .param("userId",  "1")
                        .param("latestRecordId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void test_load_matches_latest_match_not_found_in_DB() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        userRepository.saveAndFlush(user1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                        .param("size", "10")
                        .param("userId",  String.valueOf(user1.getId().orElseThrow()))
                        .param("latestRecordId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    // XXXK tests for user which is not logged
    // invalid input for controller, for example too large size etc. - if this test don't pass probably it's missing @Valid (I'm not sure if it required) annotation on controller method params.
    // for returning empty list of matches
    // saving more than 10 matches in history, than check if load keep size limit. Then use bigger size check if it loaded
    // check if latestRecordTime (or latestId after change) works, and not return invalid records
    // and maybe something more if u have more ideas.




}
