package dma.restconnexion.jwtsecurity.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

	@Value(value = "${jwt.secret}")
	public String secret;
	
	public String validate(String token) 
	{
		try {
			String body = Jwts.parser()
					.setSigningKey(secret)
					.parsePlaintextJws(token)
					.getBody();
			if (body != null)
				return "Body ok";
			else
				return "Body missing";
		}
		catch(Exception e) {
			System.out.println(e);
			return "Body missing";
		}
	}
}
