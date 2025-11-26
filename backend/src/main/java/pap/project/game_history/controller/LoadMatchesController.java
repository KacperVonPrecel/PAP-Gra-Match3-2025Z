package pap.project.game_history.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import pap.project.game_history.LoadMatchesService;
import pap.project.game_history.controller.model.load.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/load_matches")
public class LoadMatchesController
{
    public static final int MIN_RECORD_SIZE = 10;
    public static final int MAX_RECORD_SIZE = 100;

    private static final String LOG_PREFIX = "loadMatchesController{%d}";
    private static final Logger LOG = LoggerFactory.getLogger(LoadMatchesController.class);
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    private final LoadMatchesService loadMatchesService;

    public LoadMatchesController(LoadMatchesService loadMatchesService)
    {
        this.loadMatchesService = loadMatchesService;
    }

    @GetMapping ("/load")
    public @NonNull ResponseEntity<?> load(
            @RequestParam @Min(MIN_RECORD_SIZE) @Max(MAX_RECORD_SIZE) int size,
            @RequestParam long userId,
            @RequestParam long latestRecordTime)
    {
        final int requestId = REQUEST_ID.getAndIncrement();
        final String logPrefix = String.format(LOG_PREFIX, requestId);
        LOG.info("%s new load request from user of id: ".formatted(logPrefix, userId));

        try
        {
            final LoadRequest loadRequest = new LoadRequest(size, userId, latestRecordTime);
            final List<MatchProjectionForController> gameMatchesList = loadMatchesService.loadMatches(loadRequest);
            LOG.info("%s load ended result: SUCCESS".formatted(logPrefix));
            return ResponseEntity.ok(gameMatchesList);
        } catch (final Exception ex)
        {
            LOG.info("%s load ended result: FAILURE".formatted(logPrefix));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoadErrorResponse(LoadError.INTERNAL_SERVER_ERROR));
        }
    }
}
