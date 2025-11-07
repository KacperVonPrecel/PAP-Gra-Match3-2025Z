package pap.project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/xxx")
    public String xxx()
    {
        return "xxx";
    }
}
