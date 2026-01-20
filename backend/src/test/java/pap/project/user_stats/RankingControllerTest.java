package pap.project.user_stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.user_data.UserDataService;
import pap.project.user_stats.model.LoadGlobalRankingData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RankingControllerTest {
    @Mock
    private RankingService rankingService;

    @Mock
    private UserDataService userDataService;

    @InjectMocks
    private RankingController rankingController;

    @Test
    void test_load_global_ranking_data_calls_service() {
        int page = 2;
        int size = 50;

        LoadGlobalRankingData expectedData = mock(LoadGlobalRankingData.class);

        when(rankingService.loadGlobalRankingEntries(page, size)).thenReturn(expectedData);

        LoadGlobalRankingData result = rankingController.loadGlobalRankingData(page, size);

        assertEquals(expectedData, result);
        verify(rankingService, times(1)).loadGlobalRankingEntries(page, size);
    }
}
