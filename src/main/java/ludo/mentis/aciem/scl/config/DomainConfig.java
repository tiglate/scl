package ludo.mentis.aciem.scl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.CustomUserDetails;
import org.hibernate.cfg.MappingSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;


@Configuration
@EntityScan("ludo.mentis.aciem.scl.domain")
@EnableJpaRepositories("ludo.mentis.aciem.scl.repos")
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
@EnableTransactionManagement
public class DomainConfig {

    @Bean
    HibernatePropertiesCustomizer jsonFormatMapper(final ObjectMapper objectMapper) {
        return properties -> properties.put(MappingSettings.JSON_FORMAT_MAPPER, new JacksonJsonFormatMapper(objectMapper));
    }

    @Bean
    AuditorAware<User> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> !(auth instanceof AnonymousAuthenticationToken))
                .map(Authentication::getPrincipal)
                .flatMap(principal -> {
                    if (principal instanceof CustomUserDetails cud) {
                        return Optional.ofNullable(cud.getUser());
                    }
                    if (principal instanceof User user) {
                        return Optional.of(user);
                    }
                    return Optional.empty();
                });
    }
}