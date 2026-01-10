package pap.project.match3;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import pap.project.match3.model.BoardState;
import pap.project.match3.model.GameState;
import pap.project.match3.model.MoveRequest;

@Controller
public class Match3Controller {
    private final Match3Service service;

    public Match3Controller(Match3Service service) {
        this.service = service;
        service.startNewGame();
    }

    @MessageMapping("/board/{gameId}/playTurn")
    @SendTo("/topic/board/{gameId}/state")
    public @Nullable GameState playTurn(@DestinationVariable int gameId, @NonNull @RequestBody MoveRequest moveRequest)
    {
        return service.playTurn(gameId, moveRequest);
    }

    @MessageMapping("/board/{gameId}/getState")
    @SendTo("/topic/board/{gameId}/state")
    public @Nullable GameState getState(@DestinationVariable int gameId)
    {
        return service.getState(gameId);
    }
}