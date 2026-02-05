package ludo.mentis.aciem.scl.model;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ludo.mentis.aciem.scl.domain.User;


/**
 * Extension of Spring Security User class to store additional data.
 */
public class CustomUserDetails extends User implements UserDetails {

	@Serial
    private static final long serialVersionUID = -3576157936224863000L;

	public CustomUserDetails() {
	
	}
	
	public CustomUserDetails(User user) {
		this.setId(user.getId());
		this.setEmail(user.getEmail());
		this.setUsername(user.getUsername());
		this.setPassword(user.getPassword());
		this.setName(user.getName());
		this.setGender(user.getGender());
		this.setDepartment(user.getDepartment());
		this.setEnabled(user.getEnabled());
		this.setResetUID(user.getResetUID());
		this.setResetStart(user.getResetStart());
		this.setRoles(user.getRoles());
		this.setCreatedAt(user.getCreatedAt());
		this.setUpdatedAt(user.getUpdatedAt());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getRoles() == null
			 ? List.of()
			 : this.getRoles()
			       .stream()
			       .map(roleRef -> new SimpleGrantedAuthority(roleRef.getCode()))
			       .toList();
	}
}
