package pap.project.user_stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import pap.project.user_stats.model.LoadGlobalRankingData;
import pap.project.user_stats.model.RankingEntry;
import pap.project.users.User;
import pap.project.users.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RankingServiceTest {
    @Mock
    private UserStatsRepository userStatsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RankingService rankingService;

    @Test
    void test_load_global_ranking_entries_returns_data() {
        int pageNumber = 0;
        int pageSize = 5;

        User mockUser1 = new User("test-user1", "test-user1@gmail.com", "password");
        User mockUser2 = new User("test-user2", "test-user2@gmail.com", "password");
        ReflectionTestUtils.setField(mockUser1, "id", 10L);
        ReflectionTestUtils.setField(mockUser2, "id", 10L);

        UserStats mockUser1Stats = new UserStats(mockUser1);
        UserStats mockUser2Stats = new UserStats(mockUser2);

        RankingEntry entry1 = new RankingEntry(10L,"test-user", mockUser1Stats.getEloPoints());
        RankingEntry entry2 = new RankingEntry(10L,"test-user", mockUser2Stats.getEloPoints());
        List<RankingEntry> content = List.of(entry1, entry2);

        Page<RankingEntry> pageResult = new PageImpl<>(content);

        when(userStatsRepository.getGlobalRanking(any(Pageable.class))).thenReturn(pageResult);

        LoadGlobalRankingData result = rankingService.loadGlobalRankingEntries(pageNumber, pageSize);

        assertNotNull(result);
        assertEquals(2, result.loadedRankingEntries().size());
        assertFalse(result.isMoreToLoad());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userStatsRepository).getGlobalRanking(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(pageNumber, capturedPageable.getPageNumber());
        assertEquals(pageSize, capturedPageable.getPageSize());
    }

    @Test
    void test_load_global_ranking_entries_has_next_page() {
        int pageNumber = 0;
        int pageSize = 2;

        List<RankingEntry> content = List.of(mock(RankingEntry.class), mock(RankingEntry.class));

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<RankingEntry> pageResult = new PageImpl<>(content, pageRequest, 100);

        when(userStatsRepository.getGlobalRanking(any(Pageable.class))).thenReturn(pageResult);

        // Wykonanie
        LoadGlobalRankingData result = rankingService.loadGlobalRankingEntries(pageNumber, pageSize);

        // Weryfikacja
        assertTrue(result.isMoreToLoad()); // Teraz powinno być true
        assertEquals(2, result.loadedRankingEntries().size());
    }

    @Test
    void test_load_global_ranking_entries_empty() {

        Page<RankingEntry> emptyPage = Page.empty();
        when(userStatsRepository.getGlobalRanking(any(Pageable.class))).thenReturn(emptyPage);

        LoadGlobalRankingData result = rankingService.loadGlobalRankingEntries(0, 10);

        assertTrue(result.loadedRankingEntries().isEmpty());
        assertFalse(result.isMoreToLoad());
    }

    @Test
    void test_get_user_position_in_ranking() {
        long userId = 123L;
        int expectedRank = 5;

        when(userStatsRepository.calculateRankPositionById(userId)).thenReturn(expectedRank);

        int actualRank = rankingService.getUserPositionInRanking(userId);

        assertEquals(expectedRank, actualRank);
        verify(userStatsRepository, times(1)).calculateRankPositionById(userId);
    }
}
