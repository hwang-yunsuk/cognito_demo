package cognito.example.cognito_demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BasicController {
    @GetMapping("/")
    public String login(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/siteA";
        }
        return "login";
    }

    // @GetMapping("/siteA")
    // public String siteA() {
    //     return "siteA";
    // }

    @GetMapping("/siteA")
    public String home(HttpServletRequest request, Model model) {
        String idToken = (String) request.getSession().getAttribute("idToken");
        model.addAttribute("token", idToken);
        return "siteA";  // siteA.htmlに対応するテンプレート
    }
}
