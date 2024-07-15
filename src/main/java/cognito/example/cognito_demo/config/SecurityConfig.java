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
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login -> oauth2Login
                .successHandler(customOAuth2LoginSuccessHandler)
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                    .accessTokenResponseClient(tokenResponseClient))
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(cognitoOidcLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
            );
        return http.build();
    }
}
