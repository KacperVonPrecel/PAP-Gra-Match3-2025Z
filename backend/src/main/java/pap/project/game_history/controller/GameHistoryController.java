package pap.project.game_history.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pap.project.game_history.GameHistoryService;
import pap.project.game_history.controller.model.load.*;
import pap.project.game_history.controller.model.save.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/game_history")
public class GameHistoryController
{
    private static final String LOG_PREFIX = "gameHistoryController{%d}";
    private static final Logger LOG = LoggerFactory.getLogger(GameHistoryController.class);
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    private final GameHistoryService gameHistoryService;

    public GameHistoryController(GameHistoryService gameHistoryService)
    {
        this.gameHistoryService = gameHistoryService;
    }

    @PostMapping("/save")
    public @NonNull ResponseEntity<?> save(@NonNull @Valid @RequestBody SaveRequest saveRequest)
    {
        final int requestId = REQUEST_ID.getAndIncrement();
        final String logPrefix = String.format(LOG_PREFIX, requestId);
        LOG.info("%s new save request from users of id %d and %d".formatted(logPrefix, saveRequest.winnerId(), saveRequest.loserId()));
        final SaveResult result = gameHistoryService.saveMatch(logPrefix, saveRequest);
        LOG.info("%s save ended result: %s".formatted(logPrefix, result.name()));

        return switch (result)
        {
            case SUCCESS -> ResponseEntity.ok(new SaveResponse());
            case FAILED ->  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SaveErrorResponse(SaveError.INTERNAL_SERVER_ERROR));
        };
    }

    @PostMapping("/load")
    public @NonNull ResponseEntity<?> load(@NonNull @Valid @RequestBody LoadRequest loadRequest)
    {
        final int requestId = REQUEST_ID.getAndIncrement();
        final String logPrefix = String.format(LOG_PREFIX, requestId);
        LOG.info("%s new load request from user of id: ".formatted(logPrefix, loadRequest.userId()));
        final LoadResult result = gameHistoryService.loadMatch(logPrefix, loadRequest);
        LOG.info("%s load ended result: %s".formatted(logPrefix, result.name()));

        return switch (result)
        {
            case SUCCESS -> ResponseEntity.ok(new LoadResponse());
            case FAILURE ->  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoadErrorResponse(LoadError.INTERNAL_SERVER_ERROR));
        };
    }
}
