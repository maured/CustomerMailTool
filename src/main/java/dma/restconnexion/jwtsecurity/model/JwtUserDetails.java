package dma.restconnexion.jwtsecurity.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtUserDetails implements UserDetails{
	
	private String login;
	private String password;
	private String token;
	private Collection<? extends GrantedAuthority> authorities;

	public JwtUserDetails(String login, String password, String token) //, List<GrantedAuthority> grantedAuthorities 
	{
		this.login = login;
		this.password = password;
		this.token = token;
		//this.authorities = grantedAuthorities;
	}

	@Override public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override public String getPassword() {
		return password;
	}

	@Override public String getUsername() {
		return login;
	}

	@Override public boolean isAccountNonExpired() {
		return true;
	}

	@Override public boolean isAccountNonLocked() {
		return true;
	}

	@Override public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override public boolean isEnabled() {
		return true;
	}

	public String getLogin() {
		return login;
	}

	public String getToken() {
		return token;
	}
	
}
