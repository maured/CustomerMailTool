package dma.restconnexion.jwtsecurity.controller;

import dma.restconnexion.UserInfosConnexion;
import dma.restconnexion.hub.HubGetMailjetAPIToken;
import dma.restconnexion.jwtsecurity.model.JwtUser;
import dma.restconnexion.jwtsecurity.model.JwtUserDetails;
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

		UserInfosConnexion currentUser = new UserInfosConnexion();
		currentUser.setTokenJWT(jwtGenerator.generate(jwtUser));
		//ici les infos de l'utilisateurs sont stockées.
		// la clé est créée.

		HubGetMailjetAPIToken getMJtoken = new HubGetMailjetAPIToken();
		HubGetMailjetAPIToken.APIKeys keys;
		try {
			
			keys = getMJtoken.getMailjetApiKeys(jwtUser.getLogin(), jwtUser.getPassword());
			System.out.println("Mailjet API Keys:"+keys.publicKey +" / "+keys.privateKey);
			
			currentUser.setTokenJWT(currentUser.getTokenJWT());
			currentUser.setPublicK(keys.publicKey);
			currentUser.setPrivateK(keys.privateKey);
			//UserInfosConnexion.listUserConnected.add(currentUser);
			//ici je stock le nouvel utilisateur dans ma futur List Global.
			
		} catch (Exception e) {
			System.err.println("cannot get APIKeys:"+e.getMessage());
			e.printStackTrace(System.err);
		}
		return currentUser.getTokenJWT();
	}
}
