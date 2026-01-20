package pap.project.user_data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.game_history.MatchCharactersRepository;
import pap.project.game_history.MatchRepository;
import pap.project.user_data.model.controller.*;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserAuthDetails;
import pap.project.users.UserRepository;
import pap.project.characters.UserCharacter;
import pap.project.characters.UserCharactersRepository;
import pap.project.characters.model.CharacterType;
import pap.project.characters.model.controller.CharacterData;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@ActiveProfiles("test")
public class UserDataIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private UserCharactersRepository userCharactersRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MatchCharactersRepository matchCharactersRepository;

    @Autowired
    private ObjectMapper mapper;

    private User savedUser;

    @BeforeEach
    public void init()
    {
        matchRepository.deleteAll();
        matchCharactersRepository.deleteAll();
        userStatsRepository.deleteAll();
        userCharactersRepository.deleteAll();
        userRepository.deleteAll();

        final User user = new User("test-user", "test@example.com", "password");
        UserStats stats = new UserStats(user);
        stats.setActiveTeam(List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.TRASH_MAN, CharacterType.SACRED_CAT));
        user.setUserStats(stats);

        this.savedUser = userRepository.saveAndFlush(user);

        final List<UserCharacter> userCharacters = List.of(
                new UserCharacter(CharacterType.AMETHYST_ENCHANTRESS, user.getId().orElseThrow(), 1, 1),
                new UserCharacter(CharacterType.TRASH_MAN, user.getId().orElseThrow(), 10, 11),
                new UserCharacter(CharacterType.SACRED_CAT, user.getId().orElseThrow(), 5, 2));
        userCharactersRepository.saveAllAndFlush(userCharacters);
    }

    @Test
    public void test_get_user_data_user_logged() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();

        final List<CharacterData> ownedCharacters = List.of(
                new CharacterData(CharacterType.AMETHYST_ENCHANTRESS, 150, 1000, 1, OptionalInt.of(10), 1),
                new CharacterData(CharacterType.TRASH_MAN, 420, 1900, 10, OptionalInt.of(100), 11),
                new CharacterData(CharacterType.SACRED_CAT, 270, 1400, 5, OptionalInt.of(50), 2)
        );

        final List<CharacterData> lockedCharacters = List.of(
                new CharacterData(CharacterType.RUBY_HORNED_DAME, 150, 1000, 1, OptionalInt.of(10), 0),
                new CharacterData(CharacterType.HONEY_TRIGGER, 40, 1000, 1, OptionalInt.of(10), 0),
                new CharacterData(CharacterType.EXPERIENCED_SWORDMAN, 150, 1000, 1, OptionalInt.of(10), 0),
                new CharacterData(CharacterType.EMERALD_CORE_KNIGHT, 150, 1000, 1, OptionalInt.of(10), 0)
        );

        final List<CharacterType> activeTeam = List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.TRASH_MAN, CharacterType.SACRED_CAT);

        final UserAuthDetails user = new UserAuthDetails("test-user", "password", userId);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/data").with((user(user))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new UserDataResponse(
                        userId,
                        ownedCharacters,
                        1000,
                        1,
                        lockedCharacters,
                        activeTeam
                ))));
    }

    @Test
    @WithAnonymousUser
    public void test_get_user_data_user_not_logged() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/data"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_draw_characters_success() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", userId);

        DrawCharacterRequest request = new DrawCharacterRequest(pap.project.user_data.model.controller.DrawType.COMMON, 1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/draw_characters")
                        .with(user(userAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isNotEmpty())
                .andExpect(jsonPath("$.results[0].amount").value(1));

        UserStats stats = userStatsRepository.findUserStatsById(userId).orElseThrow();
        assert stats.getCurrency() == 975;
    }

    @Test
    public void test_draw_characters_not_enough_money() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", userId);

        UserStats stats = userStatsRepository.findUserStatsById(userId).orElseThrow();
        ReflectionTestUtils.setField(stats, "currency", 0);
        userStatsRepository.saveAndFlush(stats);

        DrawCharacterRequest request = new DrawCharacterRequest(pap.project.user_data.model.controller.DrawType.COMMON, 1);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/user/draw_characters")
                            .with(user(userAuth))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            assert e.getCause() instanceof RuntimeException;
        }
    }

    @Test
    public void test_upgrade_character_success() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", userId);

        UserCharacter character = userCharactersRepository.findByUserIdAndCharacterTypeIn(
                userId, java.util.Set.of(CharacterType.AMETHYST_ENCHANTRESS)).getFirst();
        character.setCopiesCount(20);
        userCharactersRepository.saveAndFlush(character);

        UpgradeCharacterRequest request = new UpgradeCharacterRequest(CharacterType.AMETHYST_ENCHANTRESS);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/upgrade_character")
                        .with(user(userAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characterData").isNotEmpty())
                .andExpect(jsonPath("$.characterData.characterType").value("AMETHYST_ENCHANTRESS"))
                .andExpect(jsonPath("$.characterData.level").value(2));

        UserCharacter upgradedChar = userCharactersRepository.findById(character.getId().orElseThrow());
        assertEquals(2, upgradedChar.getLevel());
        assertEquals(10, upgradedChar.getCopiesCount());
    }

    @Test
    public void test_set_active_team_success() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", userId);

        List<CharacterType> newTeam = List.of(CharacterType.SACRED_CAT, CharacterType.AMETHYST_ENCHANTRESS, CharacterType.TRASH_MAN);
        SetActiveTeamRequest request = new SetActiveTeamRequest(newTeam);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/set_team")
                        .with(user(userAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        UserStats stats = userStatsRepository.findUserStatsById(userId).orElseThrow();
        assertEquals(newTeam, stats.getActiveTeam());
        assertEquals(3, stats.getActiveTeam().size());
    }

    @Test
    public void test_set_active_team_invalid_not_owned() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", userId);

        List<CharacterType> invalidTeam = List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.TRASH_MAN, CharacterType.HONEY_TRIGGER);

        SetActiveTeamRequest request = new SetActiveTeamRequest(invalidTeam);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/user/set_team")
                            .with(user(userAuth))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            assertInstanceOf(IllegalArgumentException.class, e.getCause());
        }
    }

    @Test
    public void test_set_active_team_invalid_duplicates() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", userId);

        List<CharacterType> invalidTeam = List.of(CharacterType.AMETHYST_ENCHANTRESS, CharacterType.TRASH_MAN, CharacterType.TRASH_MAN);

        SetActiveTeamRequest request = new SetActiveTeamRequest(invalidTeam);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/user/set_team")
                            .with(user(userAuth))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            assertInstanceOf(IllegalArgumentException.class, e.getCause());
        }
    }


    @Test
    public void test_get_user_stats_me() throws Exception
    {
        final long userId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/user_stats")
                        .with(user(userAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test-user"))
                .andExpect(jsonPath("$.elo").value(100))
                .andExpect(jsonPath("$.wins").value(0))
                .andExpect(jsonPath("$.loses").value(0));
    }

    @Test
    public void test_get_user_stats_other_user() throws Exception
    {
        User otherUser = new User("test-user2", "test-user2@gmail.com", "password");
        UserStats otherStats = new UserStats(otherUser);
        ReflectionTestUtils.setField(otherStats, "eloPoints", 150);
        otherUser.setUserStats(otherStats);
        userRepository.saveAndFlush(otherUser);

        long otherId = otherUser.getId().orElseThrow();

        final long myId = savedUser.getId().orElseThrow();
        final UserAuthDetails userAuth = new UserAuthDetails("test-user", "password", myId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/user_stats")
                        .param("userId", String.valueOf(otherId))
                        .with(user(userAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test-user2"))
                .andExpect(jsonPath("$.elo").value(150))
                .andExpect(jsonPath("$.wins").value(0))
                .andExpect(jsonPath("$.loses").value(0));
    }
}
