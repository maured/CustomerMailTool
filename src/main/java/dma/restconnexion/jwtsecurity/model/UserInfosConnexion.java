package dma.restconnexion.jwtsecurity.model;

import java.util.Date;
import java.util.List;

public class UserInfosConnexion {
	
	public String tokenJWT;
	public String publicK;
	public String privateK;
	private Date lastPasswordReset;
	private Date expirationDate;
	public static List<UserInfosConnexion> listUserConnected;

	public String getTokenJWT() {
		return tokenJWT;
	}
	public void setTokenJWT(String tokenJWT) {
		this.tokenJWT = tokenJWT;
	}
	
	public String getPublicK() {
		return publicK;
	}
	public void setPublicK(String publicK) {
		this.publicK = publicK;
	}

	public String getPrivateK() {
		return privateK;
	}
	public void setPrivateK(String privateK) {
		this.privateK = privateK;
	}

	public Date getLastPasswordReset() {
		return lastPasswordReset;
	}
	public void setLastPasswordReset(Date lastPasswordReset) {
		this.lastPasswordReset = lastPasswordReset;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public static List<UserInfosConnexion> getListUserConnected() {
		return listUserConnected;
	}
	public static void setListUserConnected(List<UserInfosConnexion> listUserConnected) {
		UserInfosConnexion.listUserConnected = listUserConnected;
	}
	
}
