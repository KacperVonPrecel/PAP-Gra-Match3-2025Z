package pap.project.game;

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
import pap.project.game.match3.model.MoveRequest;
import pap.project.game.model.communication.*;
import pap.project.user_data.UserDataService;
import pap.project.user_data.model.UserData;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.UserAuthDetails;
import pap.project.characters.UserCharacter;
import pap.project.characters.UserCharactersService;
import pap.project.characters.model.controller.CharacterData;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class GameController
{
    private final @NonNull GameService service;
    private final @NonNull UserDataService userDataService;
    private final @NonNull UserStatsRepository userStatsRepository;
    private final @NonNull UserCharactersService userCharactersService;

    private final @NonNull SimpMessagingTemplate messaging;

    public GameController(@NonNull GameService service, @NonNull UserDataService userDataService, @NonNull UserStatsRepository userStatsRepository,
                          @NonNull UserCharactersService userCharactersService, @NonNull SimpMessagingTemplate messaging)
    {
        this.service = service;
        this.userDataService = userDataService;
        this.userStatsRepository = userStatsRepository;
        this.userCharactersService = userCharactersService;

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
    public void playTurn(@DestinationVariable String gameId, @NonNull @RequestBody MoveRequest moveRequest, @NonNull Principal principal)
    {
        final GameState gameState = service.playTurn(gameId, moveRequest, getUser(principal).getUserId());

        if  (gameState != null)
        {
            GameEndDataResponse gameEndDataResponse = null;
            if (gameState.gameEndData() != null)
            {
                final var playerStatChange = userDataService.processGameEnd(gameState.gameEndData().winnerId(), gameState.gameEndData().loserId(), System.currentTimeMillis(),
                        gameState.gameEndData().winnerCharacters(), gameState.gameEndData().loserCharacters());
                gameEndDataResponse = new GameEndDataResponse(gameState.gameEndData().winnerId(), false, playerStatChange); //XXX

            }
//            System.out.println("xxx" + xxx);
            messaging.convertAndSend("/topic/board/" + gameId + "/state", new GameStateResponse(gameState, gameEndDataResponse));
        }
    }

    @MessageMapping("/board/{gameId}/getState")
    @SendTo("/topic/board/{gameId}/state")
    public @Nullable GameState getState(@DestinationVariable String gameId)
    {
        return service.getState(gameId);
    }

    private void notifyGameStarted(@NonNull GameStartData gameStartData)
    {
        for (XXX3 data : gameStartData.playerData())
        {
            messaging.convertAndSendToUser(data.playerData().playerName(), "/queue/gameStart", gameStartData);
        }

//        messaging.convertAndSend(gameStartData.gameState(), );
    }

    private @Nullable PlayerData getPlayer(@NonNull Principal principal)
    {
        final UserAuthDetails user = getUser(principal);
        final Optional<UserStats> userStats = userStatsRepository.findUserStatsById(user.getUserId());
        final UserData userData = userDataService.getUserData(user.getUserId());

        if (userStats.isEmpty() || userData.userCharacters().isEmpty())
            return null;

        final List<GameCharacter> gameCharacters = new ArrayList<>();
        for (UserCharacter character : userData.userCharacters())
        {
            long characterId = character.getId().getAsLong();
            final CharacterData characterData = userCharactersService.createCharacterData(character);

            gameCharacters.add(new GameCharacter(characterId, character.getCharacterType(), characterData.health(), characterData.level()));
        }

        return new PlayerData(
                user.getUserId(),
                user.getUsername(),
                userStats.get().getEloPoints(),
                gameCharacters
        );
    }

    private @NonNull UserAuthDetails getUser(@NonNull Principal principal)
    {
        return (UserAuthDetails) ((Authentication) principal).getPrincipal();
    }
}