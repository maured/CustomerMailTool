package dma.restconnexion.jwtsecurity.controller;

import dma.restconnexion.jwtsecurity.model.JwtUser;
import dma.restconnexion.jwtsecurity.security.JwtGenerator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TokenController {

	private JwtGenerator jwtGenerator;

	public TokenController(JwtGenerator jwtGenerator) {
		this.jwtGenerator = jwtGenerator;
	}
	
	@RequestMapping(value = "/token", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String generate(@RequestBody final JwtUser jwtUser) {
		
		return jwtGenerator.generate(jwtUser);
	}
}
