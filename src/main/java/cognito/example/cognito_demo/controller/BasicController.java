package cognito.example.cognito_demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {
    @GetMapping("/")
    public String Login(Model model, Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            String email = makeEmail(authentication);
            model.addAttribute("email", email);
            return "siteA";
        }
        return "login";
    }

    public String makeEmail(Authentication authentication){
        String principal =authentication.getPrincipal().toString();
        int principalEmailIndex = principal.indexOf("email=");
        String email = principal.substring(principalEmailIndex+6, principal.length()-2);
        return email;
    }
}
