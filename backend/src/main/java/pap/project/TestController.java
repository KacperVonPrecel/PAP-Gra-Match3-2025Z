package pap.project;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/xxx")
    public String xxx()
    {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails user = (UserDetails) auth.getPrincipal();
//
//        String username = user.getUsername();
//
//        XXX
//        System.out.println(username);
        return "xxx";
    }
}
