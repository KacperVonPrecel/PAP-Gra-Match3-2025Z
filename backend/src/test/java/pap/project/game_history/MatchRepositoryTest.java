package pap.project.game_history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class MatchRepositoryTest
{
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    private final long finishTime = 1000166400;

    @BeforeEach
    void init()
    {
        final User user1 = new User("test-user1", "test-user1@gmail.com", "password");
        final User user2 = new User( "test-user2", "test-user2@gmail.com", "password");
        final User user3 = new User( "test-user3", "test-user3@gmail.com", "password");
        userRepository.saveAll(List.of(user1, user2, user3));

        final Match match1 = Match.createMatchForTest(user1, user2, finishTime, 20, -10);
        final Match match2 = Match.createMatchForTest(user3, user1, finishTime + 10000, 10, -5);
        final Match match3 = Match.createMatchForTest(user2, user3, finishTime + 20000, 30, -15);
        matchRepository.saveAll(List.of(match1, match2, match3));
    }

    @Test
    void test_find_matches_before_recordId()
    {
        final User user4 = new User("test-user4", "test-user4@gmail.com", "password");
        final User user5 = new User( "test-user5", "test-user5@gmail.com", "password");
        userRepository.saveAll(List.of(user4, user5));
        final Match match4 = Match.createMatchForTest(user4, user5, finishTime + 30000, 10, -5);
        matchRepository.save(match4);
        long player_id = userRepository.findByUsername("test-user1").orElseThrow().getId().orElseThrow();
        final List<Match> founded = matchRepository.findMatchesBeforeRecordId(player_id, match4.getId().orElseThrow(), PageRequest.of(0, 10));
        assert(founded.size() == 2);
        assertEquals("test-user1", founded.getFirst().getLoser().getUsername());
        assertEquals("test-user3", founded.getFirst().getWinner().getUsername());
        assertEquals(finishTime + 10000, founded.getFirst().getFinishTime());
        assertEquals(10, founded.getFirst().getWinnerEloChange());
        assertEquals(-5, founded.getFirst().getLoserEloChange());

        assertEquals("test-user2", founded.getLast().getLoser().getUsername());
        assertEquals("test-user1", founded.getLast().getWinner().getUsername());
        assertEquals(finishTime, founded.getLast().getFinishTime());
        assertEquals(20, founded.getLast().getWinnerEloChange());
        assertEquals(-10, founded.getLast().getLoserEloChange());
    }
}
