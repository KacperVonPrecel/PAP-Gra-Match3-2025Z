package pap.project.game_history.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import pap.project.game_history.EndGameService;
import pap.project.game_history.controller.model.load.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("api/game_history")
public class GameHistoryController
{
    private static final String LOG_PREFIX = "gameHistoryController{%d}";
    private static final Logger LOG = LoggerFactory.getLogger(GameHistoryController.class);
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    private final EndGameService endGameService;

    public GameHistoryController(EndGameService endGameService)
    {
        this.endGameService = endGameService;
    }

    @GetMapping ("/load")
    public @NonNull ResponseEntity<?> load(@NonNull @Valid @RequestBody LoadRequest loadRequest)
    {
        final int requestId = REQUEST_ID.getAndIncrement();
        final String logPrefix = String.format(LOG_PREFIX, requestId);
        LOG.info("%s new load request from user of id: ".formatted(logPrefix, loadRequest.userId()));
        final LoadResult result = endGameService.loadMatches(logPrefix, loadRequest);
        LOG.info("%s load ended result: %s".formatted(logPrefix, result.name()));

        return switch (result)
        {
            case SUCCESS -> ResponseEntity.ok(new LoadResponse());
            case FAILURE ->  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoadErrorResponse(LoadError.INTERNAL_SERVER_ERROR));
        };
    }
}
