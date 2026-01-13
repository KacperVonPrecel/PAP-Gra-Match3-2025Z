package pap.project.match3;

import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import pap.project.match3.model.GameStartData;
import pap.project.match3.model.GameState;
import pap.project.match3.model.MoveRequest;
import pap.project.match3.model.PlayerData;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.UserAuthDetails;

import java.security.Principal;
import java.util.Optional;

@Controller
public class Match3Controller {
    private final Match3Service service;
    private final UserStatsRepository userStatsRepository;
    private final SimpMessagingTemplate messaging;

    private static final Logger log = LoggerFactory.getLogger(Match3Controller.class);

    public Match3Controller(Match3Service service, UserStatsRepository userStatsRepository, SimpMessagingTemplate messaging)
    {
        this.service = service;
        this.userStatsRepository = userStatsRepository;
        this.messaging = messaging;
    }

    @MessageMapping("/queue/join")
    public void join(@NonNull Principal principal)
    {
        PlayerData player = getPlayer(principal);

        if (player == null)
            return;

        GameStartData gameData = service.joinOrCreateGame(player);

        if (gameData != null)
            notifyGameStarted(gameData);
    }

    @MessageMapping("/queue/exit")
    public void exitQueue(@NonNull Principal principal)
    {
        PlayerData player = getPlayer(principal);

        if (player == null)
            return;

        service.exitQueue(player);
    }

    @MessageMapping("/board/{gameId}/playTurn")
    @SendTo("/topic/board/{gameId}/state")
    public @Nullable GameState playTurn(@DestinationVariable String gameId, @NonNull @RequestBody MoveRequest moveRequest, @NonNull Principal principal)
    {
        return service.playTurn(gameId, moveRequest, getUser(principal).getUserId());
    }

    @MessageMapping("/board/{gameId}/getState")
    @SendTo("/topic/board/{gameId}/state")
    public @Nullable GameState getState(@DestinationVariable String gameId)
    {
        return service.getState(gameId);
    }

    private void notifyGameStarted(GameStartData gameStartData)
    {
        for (PlayerData data : gameStartData.playerData().values())
            messaging.convertAndSendToUser(data.playerName(), "/queue/gameStart", gameStartData);
    }

    private @NonNull UserAuthDetails getUser(@NonNull Principal principal)
    {
        return (UserAuthDetails)((Authentication)principal).getPrincipal();
    }

    private @Nullable PlayerData getPlayer(@NonNull Principal principal)
    {
        UserAuthDetails user = getUser(principal);
        Optional<UserStats> userStats = userStatsRepository.findUserStatsById(user.getUserId());

        return userStats.map(stats -> new PlayerData(user.getUserId(), user.getUsername(), stats.getEloPoints())).orElse(null);
    }
}