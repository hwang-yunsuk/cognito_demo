package cognito.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/login", "/siteA", "/siteB", "/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
                    .defaultSuccessUrl("/siteA", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .loginPage("/login")
                    .defaultSuccessUrl("/siteA", true)
            );

        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(cognitoClientRegistration());
    }

    private ClientRegistration cognitoClientRegistration() {
        return ClientRegistration.withRegistrationId("cognito")
                .clientId("your-client-id")
                .clientSecret("your-client-secret")
                .scope("openid", "profile", "email")
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE) // ここを追加
                .authorizationUri("https://your-cognito-domain.auth.region.amazoncognito.com/oauth2/authorize")
                .tokenUri("https://your-cognito-domain.auth.region.amazoncognito.com/oauth2/token")
                .userInfoUri("https://your-cognito-domain.auth.region.amazoncognito.com/oauth2/userInfo")
                .userNameAttributeName("sub")
                .jwkSetUri("https://cognito-idp.region.amazonaws.com/your-pool-id/.well-known/jwks.json")
                .clientName("Cognito")
                .build();
    }
}
