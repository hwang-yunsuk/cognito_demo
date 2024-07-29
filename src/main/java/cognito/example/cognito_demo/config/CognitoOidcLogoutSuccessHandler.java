package cognito.example.cognito_demo.config;

import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import java.io.IOException;

@Component
public class CognitoOidcLogoutSuccessHandler implements LogoutSuccessHandler {

    // .envファイルから環境変数を読み込むためのDotenvインスタンスを作成
    private final Dotenv dotenv = Dotenv.load();
    // クライアントID
    private final String clientId = dotenv.get("CLIENT_ID");
    // ログアウトURL
    private final String logoutUrl = dotenv.get("LOGOUT_URL");
    // リダイレクトURI
    private final String redirectUri = dotenv.get("REDIRECT_URI");

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // ログアウトURLにクエリパラメータを追加
        String url = UriComponentsBuilder.fromHttpUrl(logoutUrl)
                .queryParam("client_id", clientId)
                .queryParam("logout_uri", redirectUri)
                .build().toUriString();
        // ログアウトURLにリダイレクト
        response.sendRedirect(url);
    }
}