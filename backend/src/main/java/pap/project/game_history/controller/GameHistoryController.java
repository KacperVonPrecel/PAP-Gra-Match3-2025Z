package pap.project.game_history.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pap.project.game_history.GameHistoryService;

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

//    @PostMapping("/save")
//    public @NonNull ResponseEntity<?>

}
