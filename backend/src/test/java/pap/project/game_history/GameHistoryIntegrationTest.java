package pap.project.game_history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.characters.model.CharacterType;
import pap.project.game_history.model.DataNotFoundException;
import pap.project.game_history.model.HistoryCharacterData;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchCharactersRepository matchCharactersRepository;

    @Autowired
    private UserStatsRepository userStatsRepository;

    private final long finishTime = 1000166400;

    @BeforeEach
    void init ()
    {
        userStatsRepository.deleteAll();
        matchRepository.deleteAll();
        userRepository.deleteAll();
        matchCharactersRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void test_load_matches_no_more_records() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        final User user2 = new User("test-user2", "test-user2@gmail.com", "password2");

        final UserStats userStats1 = new UserStats(user1);
        final UserStats userStats2 = new UserStats(user2);
        userStatsRepository.saveAllAndFlush(List.of(userStats1, userStats2));
        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        for(long i = 0; i < 10; i++)
        {
            final Match mockedMatch = new Match(
                    user1.getId().orElseThrow(),
                    user2.getId().orElseThrow(),
                    finishTime + (i * 10000),
                    20,
                    -10
            );
            matchCharactersRepository.saveAndFlush(getMatchCharacters(mockedMatch));
            matchRepository.saveAndFlush(mockedMatch);
        }

        final Match newestMatch = new Match(
                user1.getId().orElseThrow(),
                user2.getId().orElseThrow(),
                finishTime + 100000,
                20,
                -10);
        matchCharactersRepository.saveAndFlush(getMatchCharacters(newestMatch));
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
                .andExpect(jsonPath("$.matches[9].playerId").value(user1.getId().orElseThrow()))
                .andExpect(jsonPath("$.matches[9].opponentsId").value(user2.getId().orElseThrow()))
                .andExpect(jsonPath("$.matches[9].playerUsername").value("test-user1"))
                .andExpect(jsonPath("$.matches[9].opponentsUsername").value("test-user2"))
                .andExpect(jsonPath("$.matches[9].playerEloPoints").value(100))
                .andExpect(jsonPath("$.matches[9].opponentsEloPoints").value(100))
                .andExpect(jsonPath("$.matches[9].isPlayerWinner").value(true))
                .andExpect(jsonPath("$.matches[9].finishTime").value(finishTime))
                .andExpect(jsonPath("$.matches[9].playerCharacters").isArray())
                .andExpect(jsonPath("$.matches[9].playerCharacters", hasSize(3)))
                .andExpect(jsonPath("$.matches[9].opponentCharacters").isArray())
                .andExpect(jsonPath("$.matches[9].opponentCharacters", hasSize(3)))
                .andExpect(jsonPath("$.matches[9].playerCharacters[0].characterType").value(CharacterType.AMETHYST_ENCHANTRESS))
                .andExpect(jsonPath("$.matches[9].playerCharacters[0].level").value(10))
                .andExpect(jsonPath("$.matches[9].playerCharacters[1].characterType").value(CharacterType.RUBY_HORNED_DAME))
                .andExpect(jsonPath("$.matches[9].playerCharacters[1].level").value(12))
                .andExpect(jsonPath("$.matches[9].playerCharacters[2].characterType").value(CharacterType.HONEY_TRIGGER))
                .andExpect(jsonPath("$.matches[9].playerCharacters[2].level").value(9))
                .andExpect(jsonPath("$.matches[9].opponentCharacters[0].characterType").value(CharacterType.TRASH_MAN))
                .andExpect(jsonPath("$.matches[9].opponentCharacters[0].level").value(13))
                .andExpect(jsonPath("$.matches[9].opponentCharacters[1].characterType").value(CharacterType.SACRED_CAT))
                .andExpect(jsonPath("$.matches[9].opponentCharacters[1].level").value(12))
                .andExpect(jsonPath("$.matches[9].opponentCharacters[2].characterType").value(CharacterType.EMERALD_CORE_KNIGHT))
                .andExpect(jsonPath("$.matches[9].opponentCharacters[2].level").value(11))

                .andExpect(jsonPath("$.matches[5].playerId").value(user1.getId().orElseThrow()))
                .andExpect(jsonPath("$.matches[5].opponentsId").value(user2.getId().orElseThrow()))
                .andExpect(jsonPath("$.matches[5].playerUsername").value("test-user1"))
                .andExpect(jsonPath("$.matches[5].opponentsUsername").value("test-user2"))
                .andExpect(jsonPath("$.matches[5].playerEloPoints").value(100))
                .andExpect(jsonPath("$.matches[5].opponentsEloPoints").value(100))
                .andExpect(jsonPath("$.matches[5].isPlayerWinner").value(true))
                .andExpect(jsonPath("$.matches[5].finishTime").value(finishTime + 6 * 10000))
                .andExpect(jsonPath("$.matches[5].playerCharacters").isArray())
                .andExpect(jsonPath("$.matches[5].playerCharacters", hasSize(3)))
                .andExpect(jsonPath("$.matches[5].opponentCharacters").isArray())
                .andExpect(jsonPath("$.matches[5].opponentCharacters", hasSize(3)))
                .andExpect(jsonPath("$.matches[5].playerCharacters[0].characterType").value(CharacterType.AMETHYST_ENCHANTRESS))
                .andExpect(jsonPath("$.matches[5].playerCharacters[0].level").value(10))
                .andExpect(jsonPath("$.matches[5].playerCharacters[1].characterType").value(CharacterType.RUBY_HORNED_DAME))
                .andExpect(jsonPath("$.matches[5].playerCharacters[1].level").value(12))
                .andExpect(jsonPath("$.matches[5].playerCharacters[2].characterType").value(CharacterType.HONEY_TRIGGER))
                .andExpect(jsonPath("$.matches[5].playerCharacters[2].level").value(9))
                .andExpect(jsonPath("$.matches[5].opponentCharacters[0].characterType").value(CharacterType.TRASH_MAN))
                .andExpect(jsonPath("$.matches[5].opponentCharacters[0].level").value(13))
                .andExpect(jsonPath("$.matches[5].opponentCharacters[1].characterType").value(CharacterType.SACRED_CAT))
                .andExpect(jsonPath("$.matches[5].opponentCharacters[1].level").value(12))
                .andExpect(jsonPath("$.matches[5].opponentCharacters[2].characterType").value(CharacterType.EMERALD_CORE_KNIGHT))
                .andExpect(jsonPath("$.matches[5].opponentCharacters[2].level").value(11));
    }

    @Test
    @WithMockUser
    public void test_load_matches_are_more_records() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        final User user2 = new User("test-user2", "test-user2@gmail.com", "password2");

        final UserStats userStats1 = new UserStats(user1);
        final UserStats userStats2 = new UserStats(user2);
        userStatsRepository.saveAllAndFlush(List.of(userStats1, userStats2));
        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        for(long i = 0; i < 5; i++)
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

        for(long i = 6; i < 14; i++)
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
    public void test_load_matches_are_empty() throws Exception
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password1");
        userRepository.saveAndFlush(user1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match_history/load")
                    .param("size", "12")
                    .param("userId",  String.valueOf(user1.getId().orElseThrow()))
                    .param("latestRecordId", String.valueOf(1L))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matches").isEmpty());
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
                .andExpect(status().isForbidden());
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

    private static MatchCharacters getMatchCharacters(@NonNull Match mockedMatch) {
        final List<HistoryCharacterData> historyCharacterDataPlayer = List.of(
                new HistoryCharacterData(CharacterType.AMETHYST_ENCHANTRESS, 10),
                new HistoryCharacterData(CharacterType.RUBY_HORNED_DAME, 12),
                new HistoryCharacterData(CharacterType.HONEY_TRIGGER, 9)
        );

        final List<HistoryCharacterData> historyCharacterDataOpponent = List.of(
                new HistoryCharacterData(CharacterType.TRASH_MAN, 13),
                new HistoryCharacterData(CharacterType.SACRED_CAT, 12),
                new HistoryCharacterData(CharacterType.EMERALD_CORE_KNIGHT, 11)
        );

        return new MatchCharacters(mockedMatch, historyCharacterDataPlayer, historyCharacterDataOpponent);
    }

    // XXXK tests for user which is not logged
    // invalid input for controller, for example too large size etc. - if this test don't pass probably it's missing @Valid (I'm not sure if it required) annotation on controller method params.
    // for returning empty list of matches
    // saving more than 10 matches in history, than check if load keep size limit. Then use bigger size check if it loaded
    // check if latestRecordTime (or latestId after change) works, and not return invalid records
    // and maybe something more if u have more ideas.




}
