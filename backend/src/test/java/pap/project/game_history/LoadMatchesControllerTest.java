package pap.project.game_history;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pap.project.game_history.controller.LoadMatchesController;
import pap.project.game_history.controller.model.load.LoadError;
import pap.project.game_history.controller.model.load.LoadErrorResponse;
import pap.project.game_history.controller.model.load.LoadRequest;
import pap.project.game_history.controller.model.load.MatchProjectionForController;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoadMatchesControllerTest {
    @Mock
    private LoadMatchesService loadMatchesService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private List<MatchProjectionForController>  loadedMatchesList;

    @InjectMocks
    private LoadMatchesController loadMatchesController;

    private static final long MOCK_FINISH_TIME = 1000166400;

    private final LoadRequest loadRequest = new LoadRequest(10, 1L, MOCK_FINISH_TIME);

    @Test
    void test_load_matches_success()
    {
        when(loadMatchesService.loadMatches(eq(loadRequest)))
                .thenReturn(loadedMatchesList);

        final ResponseEntity<?> response = loadMatchesController.load(loadRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
    }

    @Test
    void test_load_matches_failure()
    {
        when(loadMatchesService.loadMatches(eq(loadRequest)))
                .thenThrow(new RuntimeException());

        final ResponseEntity<?> response = loadMatchesController.load(loadRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.hasBody());
        final LoadErrorResponse responseBody = Objects.requireNonNull((LoadErrorResponse) response.getBody());
        assertEquals(LoadError.INTERNAL_SERVER_ERROR, responseBody.loadError());
    }


}
