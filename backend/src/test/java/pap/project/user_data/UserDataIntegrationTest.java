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
import pap.project.user_data.model.controller.UserDataResponse;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.User;
import pap.project.users.UserAuthDetails;
import pap.project.users.UserRepository;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersRepository;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.characters.model.controller.CharacterData;

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
    private ObjectMapper mapper;

    @BeforeEach
    public void init()
    {
        userStatsRepository.deleteAll();
        userCharactersRepository.deleteAll();
        userRepository.deleteAll();
        final User user = new User("test-user", "test@example.com", "password");
        userRepository.saveAndFlush(user);
        userStatsRepository.saveAndFlush(new UserStats(user.getId().orElseThrow()));
        final List<UserCharacter> userCharacters = List.of(new UserCharacter(CharacterType.FIRST_CHARACTER, user.getId().orElseThrow(), 1, 1),
                new UserCharacter(CharacterType.SECOND_CHARACTER, user.getId().orElseThrow(), 10, 11));
        userCharactersRepository.saveAllAndFlush(userCharacters);
    }

    @Test
    public void test_get_user_data_user_logged() throws Exception
    {
        //XXX repair

        final UserAuthDetails user = new UserAuthDetails("test-user", "password", 1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/data").with((user(user))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new UserDataResponse(
                        List.of(new CharacterData(CharacterType.FIRST_CHARACTER, 100, 100, 1, OptionalInt.of(10), 1),
                                new CharacterData(CharacterType.SECOND_CHARACTER, 1000, 1000, 10, OptionalInt.of(100), 11)),
                        100
                ))));
    }

    @Test
    @WithAnonymousUser
    public void test_get_user_data_user_not_logged() throws Exception
    {
        // XXX some in test is no cleaning of match repo
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/data"))
                .andExpect(status().isForbidden());
    }
}
