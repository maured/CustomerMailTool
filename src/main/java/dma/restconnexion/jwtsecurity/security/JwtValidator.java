package dma.restconnexion.jwtsecurity.security;

import dma.restconnexion.jwtsecurity.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

	@Value(value = "${jwt.secret}")
	public String secret;
	
	public JwtUser validate(String token) 
	{
		JwtUser jwtUser = null;
		try {
			Claims body = Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();
			jwtUser = new JwtUser();
			jwtUser.setLogin(body.getSubject());
			jwtUser.setPassword((String) body.get("password"));
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return jwtUser;
	}
}
