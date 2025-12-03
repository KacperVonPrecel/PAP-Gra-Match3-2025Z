package pap.project.user_data;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.controller.UserDataResponse;
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
        return new UserDataResponse(charactersData, userData.currency());
    }
}
