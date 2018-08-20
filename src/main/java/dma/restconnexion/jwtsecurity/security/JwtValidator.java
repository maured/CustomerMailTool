package dma.restconnexion.jwtsecurity.security;

import io.jsonwebtoken.Jwts;
import logger.MyLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

	@Value(value = "${jwt.secret}")
	public String secret;
	
	public String validate(String token) 
	{
		MyLogger logger = new MyLogger();
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
			logger.errorLevel("Error in token validation : " + e);
			return "Body missing";
		}
	}
}
