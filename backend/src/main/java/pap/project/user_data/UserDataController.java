package pap.project.user_data;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void getStartingData(@NonNull Principal principal)
    {
        //XXX check if principal work
    }
}
