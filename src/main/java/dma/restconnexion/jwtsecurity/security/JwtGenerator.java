package dma.restconnexion.jwtsecurity.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtGenerator{

	@Value(value = "${jwt.secret}")
	private String secret;
	
	public String generate() {

		//I generate a random string to avoid to send login and password of user.
		String str = Long.toHexString(Double.doubleToLongBits(Math.random()));
		
		return Jwts.builder()
				.setPayload(str) //That's why i use Payload and not Claims.
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}
}
