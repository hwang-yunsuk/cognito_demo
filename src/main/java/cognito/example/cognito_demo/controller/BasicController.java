package cognito.example.cognito_demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import cognito.example.cognito_demo.service.CognitoService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import io.github.cdimascio.dotenv.Dotenv;

@Controller
public class BasicController {

    private final CognitoService cognitoService;
    private final Dotenv dotenv = Dotenv.load();

    public BasicController(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @GetMapping("/")
    public String login(Authentication authentication) {
        // 認証済みの場合は /siteA にリダイレクト
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/siteA";
        }
        // 未認証の場合は login ページを表示
        return "login";
    }

    @GetMapping("/*")
    public String home(HttpServletRequest request, Model model, Authentication authentication) {
        // 認証が OAuth2 の場合
        if (authentication instanceof OAuth2AuthenticationToken) {
            // セッションからIDトークンを取得
            String idToken = (String) request.getSession().getAttribute("idToken");
            model.addAttribute("token", idToken);
            // siteA ページを表示
            return "siteA"; // siteA.htmlに対応するテンプレート
        }
        // 未認証の場合は Cognito 認証ページにリダイレクト
        return "redirect:/oauth2/authorization/cognito";
    }

    @RequestMapping("/redirectToSiteB")
    public String redirectToSiteB(HttpServletRequest request, Authentication authentication, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        // 認証が OAuth2 の場合
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
            String idToken = oidcUser.getIdToken().getTokenValue();

            // IDトークンをデコードしてメールアドレスを取得
            DecodedJWT decodedJWT = JWT.decode(idToken);
            String email = decodedJWT.getClaim("email").asString();

            // メールアドレスをエンコード
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());

            // Cognito B にユーザーが存在するか確認
            boolean userExistsInCognitoB = cognitoService.checkIfUserExists(email);

            if (userExistsInCognitoB) {
                // OIDC認証エンドポイントのURLを作成
                String oidcAuthUrl = dotenv.get("OIDC_AUTH_URL");
                String clientId = dotenv.get("SITE_B_CLIENT_ID");
                String responseType = "code";
                String redirectUri = dotenv.get("SITE_B_REDIRECT_URI");
                String scope = "openid email";

                String authorizationUrl = oidcAuthUrl + "?" +
                        "response_type=" + responseType + "&" +
                        "client_id=" + clientId + "&" +
                        "redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString()) + "&" +
                        "scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8.toString()) + "&" +
                        "email=" + encodedEmail;

                // 認証URLにリダイレクト
                return "redirect:" + authorizationUrl;

            } else {
                // ユーザーが存在しない場合はエラーメッセージをフラッシュ属性として追加して /siteA にリダイレクト
                redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
                return "redirect:/siteA";
            }
        }
        // 認証されていない場合はホームページにリダイレクト
        return "redirect:/";
    }
}
