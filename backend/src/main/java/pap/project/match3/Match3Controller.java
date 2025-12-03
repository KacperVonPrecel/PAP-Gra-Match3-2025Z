package pap.project.match3;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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
    public Match3Block[][] updateBoard(@DestinationVariable Integer gameId)
    {
        return service.getBoard(gameId).getBlocks();
    }

    @MessageMapping("/board/{gameId}/fillBoard")
    public void fillBoard(@DestinationVariable Integer gameId)
    {
        service.fillBoard(gameId);
    }

    @MessageMapping("/board/{gameId}/dropFloatingBlocks")
    public void dropFloatingBlocks(@DestinationVariable Integer gameId)
    {
        service.dropFloatingBlocks(gameId);
    }

    @MessageMapping("/board/{gameId}/swap")
    public boolean swapBlocks(@DestinationVariable Integer gameId, Match3Board.MoveRequest moveRequest)
    {
        // TODO: Add bound-checks, if the request fits the board
        return service.swapBlocks(gameId, moveRequest);
    }

    @MessageMapping("/board/{gameId}/destroyMatchedBlocks")
    public void destroyMatchedBlocks(@DestinationVariable Integer gameId)
    {
        service.destroyMatchedBlocks(gameId);
    }
}
