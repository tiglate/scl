package ludo.mentis.aciem.scl.config;

import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class RoleLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleLoader.class);

    private final RoleRepository roleRepository;

    public RoleLoader(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (roleRepository.count() != 0) {
            return;
        }
        log.info("initializing roles");
        final Role adminRole = new Role();
        adminRole.setCode("ADMIN");
        roleRepository.save(adminRole);
        final Role settlementReadRole = new Role();
        settlementReadRole.setCode("SETTLEMENT_READ");
        roleRepository.save(settlementReadRole);
        final Role settlementWriteRole = new Role();
        settlementWriteRole.setCode("SETTLEMENT_WRITE");
        roleRepository.save(settlementWriteRole);
        final Role counterpartyReadRole = new Role();
        counterpartyReadRole.setCode("COUNTERPARTY_READ");
        roleRepository.save(counterpartyReadRole);
        final Role counterpartyWriteRole = new Role();
        counterpartyWriteRole.setCode("COUNTERPARTY_WRITE");
        roleRepository.save(counterpartyWriteRole);
        final Role tradeReadRole = new Role();
        tradeReadRole.setCode("TRADE_READ");
        roleRepository.save(tradeReadRole);
        final Role tradeWriteRole = new Role();
        tradeWriteRole.setCode("TRADE_WRITE");
        roleRepository.save(tradeWriteRole);
    }

}
