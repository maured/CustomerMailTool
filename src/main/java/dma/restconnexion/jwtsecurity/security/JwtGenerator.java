package dma.restconnexion.jwtsecurity.security;

import dma.restconnexion.jwtsecurity.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

@Component
public class JwtGenerator{

	public String generate(JwtUser jwtUser) {
		//date d'expiration = .setExpiration(date)
		
		Claims claims = Jwts.claims()
				.setSubject(jwtUser.getUserName());
		claims.put("password", jwtUser.getPassword());
		claims.put("role", jwtUser.getRole());
		
		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, "test")
				.compact();
	}
}
