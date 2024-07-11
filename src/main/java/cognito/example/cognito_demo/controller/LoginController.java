package cognito.example.cognito_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String customLogin() {
        logger.info("Accessing /login");
        return "login";
    }

    @GetMapping("/siteA")
    public String siteA() {
        logger.info("Accessing /siteA");
        return "siteA";
    }

    @GetMapping("/siteB")
    public String siteB() {
        logger.info("Accessing /siteB");
        return "siteB";
    }
}
