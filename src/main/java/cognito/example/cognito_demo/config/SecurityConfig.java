package cognito.example.cognito_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
}
