package ludo.mentis.aciem.scl.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableMethodSecurity
public class FormsSecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        // creates hashes with {bcrypt} prefix
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain formsSecurityConfigFilterChain(final HttpSecurity http,
            @Value("${formsSecurityConfig.rememberMeKey}") final String rememberMeKey) throws
            Exception {
        return http.cors(withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/**", "/api/**"))
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().permitAll())
                .formLogin(form -> form
                    .loginPage("/login")
                    .successHandler(new HtmxAwareAuthenticationSuccessHandler())
                    .failureHandler((request, response, exception) -> {
                        if ("true".equals(request.getHeader("HX-Request"))) {
                            response.setHeader("HX-Redirect", "/login?loginError=true");
                        } else {
                            response.sendRedirect("/login?loginError=true");
                        }
                    }))
                .rememberMe(rememberMe -> rememberMe
                    .tokenValiditySeconds(((int)Duration.ofDays(180).getSeconds()))
                    .rememberMeParameter("rememberMe")
                    .key(rememberMeKey))
                .logout(logout -> logout
                    .logoutSuccessUrl("/?logoutSuccess=true")
                    .deleteCookies("JSESSIONID"))
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new HtmxAuthenticationEntryPoint("/login?loginRequired=true")))
                .build();
    }

}
