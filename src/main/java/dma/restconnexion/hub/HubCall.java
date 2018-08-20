package dma.restconnexion.hub;

import dma.restconnexion.jwtsecurity.model.UserInfosConnexion;
import dma.restconnexion.jwtsecurity.model.JwtUser;
import logger.MyLogger;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HubCall{
	
	public Date calculateExpirationDate (Date lastPasswordReset)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(lastPasswordReset);
		calendar.add(Calendar.MINUTE, 30);
		lastPasswordReset = calendar.getTime();
		
		return lastPasswordReset;
	}
	
	public void hubConfirmationConnexion(JwtUser jwtUser, UserInfosConnexion currentUser) throws Exception 
	{
		MyLogger logger = new MyLogger();
		Date tmp;
		HubGetMailjetAPIToken getMJtoken = new HubGetMailjetAPIToken();
		HubGetMailjetAPIToken.APIKeys keys;
		
		keys = getMJtoken.getMailjetApiKeys(jwtUser.getLogin(), jwtUser.getPassword());
		logger.infoLevel("Mailjet API Keys:"+keys.publicKey +" / "+keys.privateKey);
		currentUser.setTokenJWT(currentUser.getTokenJWT());
		currentUser.setPublicK(keys.publicKey);
		currentUser.setPrivateK(keys.privateKey);
		currentUser.setLastPasswordReset(new Date());
		tmp = calculateExpirationDate(currentUser.getLastPasswordReset());
		currentUser.setExpirationDate(tmp);
	} 
}