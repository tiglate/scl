package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.CustomUserDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@TestConfiguration
@EnableMethodSecurity
public class TestSecurityConfig {

    public static CustomUserDetails createCustomUserDetails(String username, List<String> authorities) {
        User user = new User();
        user.setUsername(username);
        user.setName(username); // Set name so getInitials() works
        Set<Role> roles = authorities.stream().map(auth -> {
            Role role = new Role();
            role.setCode(auth);
            return role;
        }).collect(Collectors.toSet());
        user.setRoles(roles);
        return new CustomUserDetails(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
