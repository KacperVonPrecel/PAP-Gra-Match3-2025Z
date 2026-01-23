package pap.project.user_stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pap.project.characters.model.CharacterType;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class UserStatsRepositoryTest
{
    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init()
    {
        userStatsRepository.deleteAll();
        userRepository.deleteAll();

        final User user1 = new User("test-user1", "test-user1@gmail.com", "password");
        final User user2 = new User( "test-user2", "test-user2@gmail.com", "password");
        userRepository.saveAll(List.of(user1, user2));

        final UserStats userStats1 = new UserStats(user1);
        final UserStats userStats2 = new UserStats(user2);

        List<CharacterType> activeTeam = List.of(
                CharacterType.TRASH_MAN,
                CharacterType.SACRED_CAT,
                CharacterType.AMETHYST_ENCHANTRESS
        );
        userStats1.setActiveTeam(activeTeam);
        userStats2.setActiveTeam(activeTeam);

        userStatsRepository.saveAll(List.of(userStats1, userStats2));
    }

    @Test
    void test_find_user_stats_by_id()
    {
        final User user3 = new User( "test-user3", "test-user3@gmail.com", "password");
        userRepository.save(user3);

        final UserStats userStats3 = new UserStats(user3);
        userStatsRepository.save(userStats3);
        final UserStats founded = userStatsRepository.findUserStatsById(userStats3.getId()).orElseThrow();
        assertEquals(userStats3.getId(), founded.getId());
        assertEquals(userStats3.getCurrency(), founded.getCurrency());
        assertEquals(userStats3.getMatchWon(), founded.getMatchWon());
        assertEquals(userStats3.getMatchPlayed(), founded.getMatchPlayed());
        assertEquals(userStats3.getEloPoints(), founded.getEloPoints());
    }

    @Test
    void test_find_user_stats_by_userId()
    {
        final long user1Id = userRepository.findByUsername("test-user1").orElseThrow().getId().orElseThrow();
        final long user2Id = userRepository.findByUsername("test-user2").orElseThrow().getId().orElseThrow();

        final UserStats founded1 = userStatsRepository.findUserStatsById(user1Id).orElseThrow();
        final UserStats founded2 = userStatsRepository.findUserStatsById(user2Id).orElseThrow();

        assertEquals(100, founded1.getEloPoints());
        assertEquals(100, founded2.getEloPoints());
        assertEquals(0, founded1.getMatchPlayed());
        assertEquals(0, founded2.getMatchPlayed());
        assertEquals(0, founded1.getMatchWon());
        assertEquals(0, founded2.getMatchWon());
        assertEquals(1000, founded1.getCurrency());
        assertEquals(1000, founded2.getCurrency());
    }

    @Test
    void test_update_user_stats_after_game_end()
    {
        final long user1Id = userRepository.findByUsername("test-user1").orElseThrow().getId().orElseThrow();
        final long user2Id = userRepository.findByUsername("test-user2").orElseThrow().getId().orElseThrow();

        userStatsRepository.updateUserStatsAfterGameEnd(10, 5, 120, 1200, user1Id);
        userStatsRepository.updateUserStatsAfterGameEnd(20, 11, 130, 900, user2Id);

        final UserStats founded1 = userStatsRepository.findUserStatsById(user1Id).orElseThrow();
        final UserStats founded2 = userStatsRepository.findUserStatsById(user2Id).orElseThrow();

        assertEquals(120, founded1.getEloPoints());
        assertEquals(130, founded2.getEloPoints());
        assertEquals(10, founded1.getMatchPlayed());
        assertEquals(20, founded2.getMatchPlayed());
        assertEquals(5, founded1.getMatchWon());
        assertEquals(11, founded2.getMatchWon());
        assertEquals(1200, founded1.getCurrency());
        assertEquals(900, founded2.getCurrency());
    }

    @Test
    void test_update_user_stats_active_team()
    {
        final long userId = userRepository.findByUsername("test-user1").orElseThrow().getId().orElseThrow();

        List<CharacterType> newTeam = List.of(
                CharacterType.HONEY_TRIGGER,
                CharacterType.SACRED_CAT,
                CharacterType.EMERALD_CORE_KNIGHT
        );

        UserStats userStats = userStatsRepository.findUserStatsById(userId).orElseThrow();

        assertEquals(CharacterType.TRASH_MAN, userStats.getActiveTeam().getFirst());
        assertEquals(CharacterType.SACRED_CAT, userStats.getActiveTeam().get(1));
        assertEquals(CharacterType.AMETHYST_ENCHANTRESS, userStats.getActiveTeam().getLast());

        userStatsRepository.updateUserStatsActiveTeam(newTeam, userId);

        userStats = userStatsRepository.findUserStatsById(userId).orElseThrow();
        assertEquals(newTeam, userStats.getActiveTeam());
    }
}
