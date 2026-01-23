package pap.project.user_stats;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pap.project.characters.UserCharactersRepository;
import pap.project.user_data.UserDataService;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@ActiveProfiles("test")
public class RankingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RankingService rankingService;

    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCharactersRepository  userCharactersRepository;

    @Autowired
    private UserDataService userDataService;

    @BeforeEach
    void init() {
        userCharactersRepository.deleteAll();
        userStatsRepository.deleteAll();
        userRepository.deleteAll();

        IntStream.range(0, 15).forEach(i -> {
            User u = new User("user" + i, "user" + i + "@email.com", "password");
            UserStats s = new UserStats(u);
            ReflectionTestUtils.setField(s, "eloPoints", 1000 + i); // Różne punkty ELO
            u.setUserStats(s);
            userRepository.save(u);
        });
    }

    @Test
    @WithMockUser
    void test_load_global_ranking_no_params() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ranking/global")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loadedRankingEntries", hasSize(10)))
                .andExpect(jsonPath("$.isMoreToLoad").value(true));
    }

    @Test
    @WithMockUser
    void test_load_global_ranking_with_params() throws Exception {
        int page = 0;
        int size = 25;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ranking/global")
                        .param("pageNumber", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loadedRankingEntries", hasSize(15)))
                .andExpect(jsonPath("$.isMoreToLoad").value(false));
    }

    @Test
    @WithMockUser
    void test_load_global_ranking_with_wrong_params() throws Exception {
        Exception exceptionSize = assertThrows(Exception.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/ranking/global")
                    .param("pageNumber", "0")
                    .param("size", "0") // BŁĄD
                    .contentType(MediaType.APPLICATION_JSON));
        });

        Throwable rootCauseSize = exceptionSize.getCause();
        assertInstanceOf(IllegalArgumentException.class, rootCauseSize);
        assertEquals("Page size must not be less than one", rootCauseSize.getMessage());


        Exception exceptionPage = assertThrows(Exception.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/ranking/global")
                    .param("pageNumber", "-1") // BŁĄD
                    .param("size", "1")
                    .contentType(MediaType.APPLICATION_JSON));
        });

        Throwable rootCausePage = exceptionPage.getCause();
        assertInstanceOf(IllegalArgumentException.class, rootCausePage);
        assertEquals("Page index must not be less than zero", rootCausePage.getMessage());
    }
}
