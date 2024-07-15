package cognito.example.cognito_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
     @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/siteA")
    public String siteA() {
        return "siteA";
    }
}
