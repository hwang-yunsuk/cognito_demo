package cognito.example.cognito_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;
    private final CognitoOidcLogoutSuccessHandler cognitoOidcLogoutSuccessHandler;

    public SecurityConfig(CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler, CognitoOidcLogoutSuccessHandler cognitoOidcLogoutSuccessHandler) {
        this.customOAuth2LoginSuccessHandler = customOAuth2LoginSuccessHandler;
        this.cognitoOidcLogoutSuccessHandler = cognitoOidcLogoutSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        OAuth2AuthorizationCodeGrantRequestEntityConverter requestEntityConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        tokenResponseClient.setRequestEntityConverter(requestEntityConverter);

        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/**").permitAll() // 全てのリクエストを許可
                .anyRequest().authenticated() // 認証が必要
            )
            .oauth2Login(oauth2Login -> oauth2Login
                .successHandler(customOAuth2LoginSuccessHandler) // 認証成功時のハンドラーを設定
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                    .accessTokenResponseClient(tokenResponseClient)) // トークンエンドポイントを設定
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // ログアウトURLを設定
                .logoutSuccessHandler(cognitoOidcLogoutSuccessHandler) // ログアウト成功時のハンドラーを設定
                .invalidateHttpSession(true) // セッションを無効化
                .clearAuthentication(true) // 認証情報をクリア
                .deleteCookies("JSESSIONID", "remember-me") // クッキーを削除
            );
        return http.build();
    }
}