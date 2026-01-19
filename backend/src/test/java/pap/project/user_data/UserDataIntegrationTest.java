package pap.project.user_data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.game_history.MatchCharactersRepository;
import pap.project.game_history.MatchRepository;
import pap.project.user_data.model.controller.UserDataResponse;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
