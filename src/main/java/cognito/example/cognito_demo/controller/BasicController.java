package cognito.example.cognito_demo.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BasicController {

    @GetMapping("/")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/siteA";
        }
        return "login";
    }

    @GetMapping("/siteA")
    public String home(HttpServletRequest request, Model model) {
        String idToken = (String) request.getSession().getAttribute("idToken");
        model.addAttribute("token", idToken);
        return "siteA"; // siteA.htmlに対応するテンプレート
    }

    @RequestMapping("/redirectToSiteB")
    public String redirectToSiteB(HttpServletRequest request, Authentication authentication) throws UnsupportedEncodingException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
            String idToken = oidcUser.getIdToken().getTokenValue();

            // Decode the ID token to get email
            DecodedJWT decodedJWT = JWT.decode(idToken);
            String email = decodedJWT.getClaim("email").asString();

            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());
            return "redirect:http://localhost:8081/oauth2/authorization/cognito?email=" + encodedEmail;
        }
        return "redirect:/";
    }
}
