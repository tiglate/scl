package ludo.mentis.aciem.scl.repos;

import ludo.mentis.aciem.scl.domain.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditTestConfig {
    private User auditor;

    @Bean
    public AuditorAware<User> auditorProvider() {
        return () -> Optional.ofNullable(auditor);
    }

    public void setAuditor(User auditor) {
        this.auditor = auditor;
    }
}
