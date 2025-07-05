package ludo.mentis.aciem.scl.model;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


/**
 * Extension of Spring Security User class to store additional data.
 */
public class FormsUserDetails extends User {

	private static final long serialVersionUID = -3576157936224863000L;
	private final Long id;

    public FormsUserDetails(final Long id, final String username, final String hash,
                            final Collection<? extends GrantedAuthority> authorities) {
        super(username, hash, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
