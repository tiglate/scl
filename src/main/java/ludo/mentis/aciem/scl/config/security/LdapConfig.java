package ludo.mentis.aciem.scl.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

@Configuration
public class LdapConfig {

    @Bean
    public LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource, 
                                                @Value("${spring.ldap.base}") String ldapBase) {
        var groupSearchBase = "ou=groups";
        var authorities = new DefaultLdapAuthoritiesPopulator(contextSource, groupSearchBase);
        authorities.setGroupSearchFilter("(member=uid={1},ou=users," + ldapBase + ")");
        return authorities;
    }

    @Bean
    public LdapAuthenticationProvider ldapAuthenticationProvider(BaseLdapPathContextSource contextSource,
                                                                 LdapAuthoritiesPopulator authorities) {
        FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch("ou=users", "(uid={0})", contextSource);
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(userSearch);
        
        return new LdapAuthenticationProvider(authenticator, authorities);
    }
}
