package pap.project.match3;

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
import pap.project.match3.game.GameService;
import pap.project.match3.model.*;
import pap.project.user_data.UserDataService;
import pap.project.user_data.model.UserData;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.UserAuthDetails;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersService;
import pap.project.users.characters.model.controller.CharacterData;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class Match3Controller {
    private final GameService service;
    private final UserDataService userDataService;
    private final UserStatsRepository userStatsRepository;
    private final UserCharactersService userCharactersService;

    private final SimpMessagingTemplate messaging;

    private static final Logger log = LoggerFactory.getLogger(Match3Controller.class);

    public Match3Controller(GameService service, UserDataService userDataService, UserStatsRepository userStatsRepository, UserCharactersService userCharactersService, SimpMessagingTemplate messaging)
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
            messaging.convertAndSend("/topic/board/{gameId}/state", gameState);

            if (service.hasGameEnded(gameId) && service.getPlayers(gameId) != null)
            {
                // TODO: Implement
//                 notifyGameEnded(PLAYER GAINS HERE, service.getPlayers(gameId));
            }
        }
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

    private void notifyGameEnded(@NonNull GameEndData gameEndData, @NonNull List<PlayerData> playerData)
    {
        for (PlayerData data : playerData)
            messaging.convertAndSendToUser(data.playerName(), "/queue/gameEnd", gameEndData);
    }

    private @NonNull UserAuthDetails getUser(@NonNull Principal principal)
    {
        return (UserAuthDetails)((Authentication)principal).getPrincipal();
    }

    private @Nullable PlayerData getPlayer(@NonNull Principal principal)
    {
        UserAuthDetails user = getUser(principal);
        Optional<UserStats> userStats = userStatsRepository.findUserStatsById(user.getUserId());
        UserData userData = userDataService.getUserData(user.getUserId());

        if (userStats.isEmpty() || userData.userCharacters().isEmpty())
            return null;

        List<GameCharacter> gameCharacters = new ArrayList<>();
        for (UserCharacter character : userData.userCharacters())
        {
            long characterId = character.getId().getAsLong();
            CharacterData characterData = userCharactersService.createCharacterData(character);

            gameCharacters.add(new GameCharacter(characterId, character.getCharacterType(), characterData.damage(), characterData.health()));
        }

        return new PlayerData(
                user.getUserId(),
                user.getUsername(),
                userStats.get().getEloPoints(),
                gameCharacters
        );
    }
}