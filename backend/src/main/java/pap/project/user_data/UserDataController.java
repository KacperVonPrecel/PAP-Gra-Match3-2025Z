package pap.project.user_data;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pap.project.user_data.model.controller.StartingDataResponse;
import pap.project.users.UserAuthDetails;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserDataController
{
    private final @NonNull UserDataService userDataService;

    public UserDataController(@NonNull UserDataService userDataService)
    {
        this.userDataService = userDataService;
    }


    @GetMapping("starting_data")
    public @NonNull StartingDataResponse getStartingData(@NonNull Authentication authentication)
    {
        final UserAuthDetails user = (UserAuthDetails) authentication.getPrincipal();
        final long userId = user.getUserId();
        final StartingDataResponse response = userDataService.getUserData(userId);
        return response;
    }
}
