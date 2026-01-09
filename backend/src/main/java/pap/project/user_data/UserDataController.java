package pap.project.user_data;

import jakarta.validation.Valid;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.controller.*;
import pap.project.users.UserAuthDetails;
import pap.project.users.characters.UserCharactersService;
import pap.project.users.characters.model.controller.CharacterData;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserDataController
{
    private final @NonNull UserDataService userDataService;
    private final @NonNull UserCharactersService userCharactersService;

    public UserDataController(@NonNull UserDataService userDataService, @NonNull UserCharactersService userCharactersService)
    {
        this.userDataService = userDataService;
        this.userCharactersService = userCharactersService;
    }

    @GetMapping("data")
    public @NonNull UserDataResponse getStartingData(@NonNull Authentication authentication)
    {
        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
        final long userId = user.getUserId();
        final UserData userData = userDataService.getUserData(userId);
        final List<CharacterData> charactersData = userCharactersService.createCharactersData(userData.userCharacters());
        return new UserDataResponse(userId, charactersData, userData.currency());
    }

    @GetMapping("get_team")
    public @NonNull GetActiveTeamResponse getTeam(@NonNull Authentication authentication)
    {
        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
        final long userId = user.getUserId();
        final UserData userData = userDataService.getUserData(userId);
        return new GetActiveTeamResponse(userData.activeTeamIds());
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

}
