package pap.project.match3;

import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import pap.project.match3.model.MoveRequest;

@Controller
public class Match3Controller
{
    private final Match3Service service;

    public  Match3Controller(Match3Service service)
    {
        this.service = service;
        service.startNewGame();
    }

    @MessageMapping("/board/{gameId}/state")
    @SendTo("/topic/board/{gameId}/state")
    public Match3Block[][] updateBoard(@DestinationVariable int gameId)
    {
        return service.getBoard(gameId).getBlocks();
    }

    @MessageMapping("/board/{gameId}/fillBoard")
    public void fillBoard(@DestinationVariable int gameId)
    {
        service.fillBoard(gameId);
    }

    @MessageMapping("/board/{gameId}/dropFloatingBlocks")
    public void dropFloatingBlocks(@DestinationVariable int gameId)
    {
        service.dropFloatingBlocks(gameId);
    }

    @MessageMapping("/board/{gameId}/swap")
    public boolean swapBlocks(@DestinationVariable int gameId, @NonNull MoveRequest moveRequest)
    {
        // TODO: Add bound-checks, if the request fits the board
        return service.swapBlocks(gameId, moveRequest);
    }

    @MessageMapping("/board/{gameId}/destroyMatchedBlocks")
    public void destroyMatchedBlocks(@DestinationVariable int gameId)
    {
        service.destroyMatchedBlocks(gameId);
    }
}
