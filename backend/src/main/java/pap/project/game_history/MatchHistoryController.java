package pap.project.game_history;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pap.project.game_history.model.DataNotFoundException;
import pap.project.user_data.UserDataService;
import pap.project.users.UserAuthDetails;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/match_history")
public class MatchHistoryController
{
    public static final int MIN_RECORD_SIZE = 10;
    public static final int MAX_RECORD_SIZE = 100;

    private static final String LOG_PREFIX = "loadMatchesController{%d}";
    private static final Logger LOG = LoggerFactory.getLogger(MatchHistoryController.class);
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    private final @NonNull MatchHistoryService matchHistoryService;

    public MatchHistoryController(@NonNull MatchHistoryService matchHistoryService)
    {
        this.matchHistoryService = matchHistoryService;
    }

    @GetMapping ("/load")
    public @NonNull LoadHistoryMatchesData load(
            @NonNull Authentication authentication,
            @RequestParam(required = false) @Positive Long userId,
            @RequestParam(required = false) @Positive Long latestRecordId,
            @RequestParam @Min(MIN_RECORD_SIZE) @Max(MAX_RECORD_SIZE) int size
            )
    {
        final int requestId = REQUEST_ID.getAndIncrement();
        final String logPrefix = String.format(LOG_PREFIX, requestId);
        LOG.info("%s new load request from user of id: ".formatted(logPrefix, userId));

        if (userId == null)
            userId = ((UserAuthDetails) authentication.getPrincipal()).getUserId();

        if (latestRecordId == null) latestRecordId = Long.MAX_VALUE;
        final LoadHistoryMatchesData loadedData = matchHistoryService.loadMatches(userId, latestRecordId, size);

        if (loadedData == null) throw new DataNotFoundException(String.format(LOG_PREFIX, requestId));
        LOG.info("%s load ended result: SUCCESS".formatted(logPrefix));
        return loadedData;
    }
}
