package dma.restconnexion.jwtsecurity.security;

import dma.restconnexion.jwtsecurity.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtGenerator{

	@Value(value = "${jwt.secret}")
	private String secret;
	
	public String generate(JwtUser jwtUser) {
		
		Claims claims = Jwts.claims()
				.setSubject(jwtUser.getLogin());
		claims.put("password", jwtUser.getPassword());
		
		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}
}
