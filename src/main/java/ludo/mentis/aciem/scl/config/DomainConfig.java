package ludo.mentis.aciem.scl.config;

import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.CustomUserDetails;
import org.hibernate.cfg.MappingSettings;
import org.hibernate.type.format.jackson.Jackson3JsonFormatMapper;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
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
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;


@Configuration
@EntityScan("ludo.mentis.aciem.scl.domain")
@EnableJpaRepositories("ludo.mentis.aciem.scl.repos")
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
@EnableTransactionManagement
public class DomainConfig {

    @Bean
    HibernatePropertiesCustomizer jsonFormatMapper(final JsonMapper jsonMapper) {
        return properties -> properties.put(MappingSettings.JSON_FORMAT_MAPPER, new Jackson3JsonFormatMapper(jsonMapper));
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