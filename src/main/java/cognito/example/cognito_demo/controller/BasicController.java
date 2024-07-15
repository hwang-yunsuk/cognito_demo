package cognito.example.cognito_demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {
    @GetMapping("/")
    public String login(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/siteA";
        }
        return "login";
    }

    @GetMapping("/siteA")
    public String siteA() {
        return "siteA";
    }
}
