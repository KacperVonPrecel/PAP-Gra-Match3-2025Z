package pap.project.user_data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.controller.*;
import pap.project.user_stats.RankingService;
import pap.project.users.UserAuthDetails;
import pap.project.characters.UserCharactersService;
import pap.project.characters.model.CharacterType;
import pap.project.characters.model.controller.CharacterData;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserDataController
{
    private final @NonNull UserDataService userDataService;
    private final @NonNull UserCharactersService userCharactersService;
    private final @NonNull RankingService rankingService;

    public UserDataController(@NonNull UserDataService userDataService, @NonNull UserCharactersService userCharactersService, @NonNull RankingService rankingService)
    {
        this.userDataService = userDataService;
        this.userCharactersService = userCharactersService;
        this.rankingService = rankingService;
    }

    @GetMapping("data")
    public @NonNull UserDataResponse getStartingData(@NonNull Authentication authentication)
    {
        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
        final long userId = user.getUserId();
        final UserData userData = userDataService.getUserData(userId);
        final List<CharacterData> charactersData = userCharactersService.createCharactersData(userData.userCharacters());
        final List<CharacterType> unlockedCharacters = charactersData.stream().map(CharacterData::characterType).toList();

        final List<CharacterData> lockedCharacters = Arrays.stream(CharacterType.values()).filter(type -> !unlockedCharacters.contains(type))
                .map(userCharactersService::createEmptyCharacterData).toList();
        final long userRankingPosition = rankingService.getUserPositionInRanking(userId);
        return new UserDataResponse(userId, charactersData, userData.currency(), userRankingPosition, lockedCharacters, userData.activeTeam());
    }

    @PostMapping("draw_characters")
    public @NonNull DrawCharacterResponse drawCharacters(@NonNull Authentication authentication,
                                                         @NonNull @Valid @RequestBody DrawCharacterRequest request)
    {
        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
        final long userId = user.getUserId();

        return userDataService.drawCharacters(request, userId);
    }

    @PostMapping("upgrade_character")
    public @NonNull UpgradeCharacterResponse upgradeCharacter(@NonNull Authentication authentication,
                                                              @NonNull @Valid @RequestBody UpgradeCharacterRequest request)
    {
        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
        final long userId = user.getUserId();
        return userDataService.upgradeCharacter(request, userId);
    }

    @PostMapping("set_team")
    public @NonNull SetActiveTeamResponse setActiveTeam(@NonNull Authentication authentication,
                                                        @NonNull @Valid @RequestBody SetActiveTeamRequest request)
    {
        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
        final long userId = user.getUserId();
        return userDataService.setActiveTeam(request, userId);
    }

    @GetMapping("user_stats")
    public @NonNull UserStatsResponse userStats(
            @NonNull Authentication authentication,
            @RequestParam(required = false) @Positive Long userId
    )
    {
        if (userId == null)
            userId = ((UserAuthDetails) authentication.getPrincipal()).getUserId();
        final UserData userData = userDataService.getUserData(userId);
        return new UserStatsResponse(userData.username(), userData.eloPoints(), userData.matchWon(), userData.matchPlayed() - userData.matchWon());
    }

}
