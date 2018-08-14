package dma.restconnexion.jwtsecurity.controller;

import dma.restconnexion.UserInfosConnexion;
import dma.restconnexion.hub.HubCall;
import dma.restconnexion.jwtsecurity.model.JwtAuthenticationToken;
import dma.restconnexion.jwtsecurity.model.JwtUser;
import dma.restconnexion.jwtsecurity.security.JwtGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginController{
	
	private JwtGenerator jwtGenerator;
	private static List<UserInfosConnexion> maList = new ArrayList<>();
	
	public LoginController(JwtGenerator jwtGenerator) {
		this.jwtGenerator = jwtGenerator;
	}
	
	@RequestMapping(value = "/auth/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE) // auth/login
	@ResponseBody
	public ResponseEntity<?> generate(@RequestBody final JwtUser jwtUser) throws Exception {

		UserInfosConnexion currentUser = new UserInfosConnexion();
		HubCall hubCall = new HubCall();
		
		try {
			if (UserInfosConnexion.listUserConnected != null)
			{
				currentUser.setTokenJWT(jwtGenerator.generate(jwtUser));
				hubCall.hubConfirmationConnexion(jwtUser, currentUser);
				for (int i = 0; i < UserInfosConnexion.listUserConnected.size(); i++)
				{
					if (!currentUser.getTokenJWT().equals(UserInfosConnexion.listUserConnected.get(i).getTokenJWT()))
					{
						if (i == UserInfosConnexion.listUserConnected.size() - 1)
						{
							LoginController.maList.add(currentUser);
							UserInfosConnexion.setListUserConnected(maList);
							break;
						}
						else
							continue;	
					}
					else
					{
						UserInfosConnexion.listUserConnected.remove(i);
						i--;
					}
				}
				if (UserInfosConnexion.listUserConnected.isEmpty())
				{
					LoginController.maList.add(currentUser);
					UserInfosConnexion.setListUserConnected(maList);
				}
			}
			else
			{
				currentUser.setTokenJWT(jwtGenerator.generate(jwtUser));
				hubCall.hubConfirmationConnexion(jwtUser, currentUser);
				LoginController.maList.add(currentUser);
				UserInfosConnexion.setListUserConnected(maList);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		System.out.println(UserInfosConnexion.getListUserConnected().size());
		System.out.println(currentUser.getTokenJWT());
		return new ResponseEntity<>(new JwtAuthenticationToken(currentUser.getTokenJWT()), HttpStatus.OK);
	}
}
