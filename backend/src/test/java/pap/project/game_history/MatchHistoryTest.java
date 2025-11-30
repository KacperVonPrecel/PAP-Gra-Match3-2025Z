package pap.project.game_history;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pap.project.game_history.model.MatchFromHistoryData;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchHistoryTest
{
    @Mock
    private MatchHistoryService matchHistoryService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    //XXXK don't mock match history. Create custom list and check in tests if controller return valid list. U can use List.of(), to easily create list.
    @Mock
    private List<MatchFromHistoryData>  loadedMatchesList;

    @InjectMocks
    private MatchHistoryController loadMatchesController;

    private final long finishTime = 1000166400;


    @Test
    void test_load_matches_success()
    {
        when(matchHistoryService.loadMatches(1L, finishTime, 10))
                .thenReturn(loadedMatchesList);

        final List<MatchFromHistoryData> matches = loadMatchesController.load(1, finishTime, 10);
        // XXXK check if list is valid
    }
}
