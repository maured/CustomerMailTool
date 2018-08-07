package dma.restconnexion.hub;

import java.io.IOException;
import java.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Response;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class HubGetMailjetAPIToken {

	public static final String mailjetApiKeysAPIUrl="https://e-deal.biz/service/getsmtpserverinfo";
	public static final String TOKEN_SEPARATOR = "(separator)";
	private static final String SCRAMBLINGKEY = "lFoQp3rBq0tPqsN4gps6iLbk9ZptApFkfj3kq1AP0tPqsN4gps6i";

	public class APIKeys {
		public String publicKey=null;
		public String privateKey=null;

		public APIKeys(String publicK,String privateK ) {
			this.publicKey=publicK;
			this.privateKey=privateK;
		}
	}

	public HubGetMailjetAPIToken() {

	}
	public APIKeys getMailjetApiKeys(String hubLogin, String hubPassword) throws Exception {
		String jsonResponse=callHubMailjetApiKeys(hubLogin, hubPassword);
		return unScrambleApiKeys(getAPITokenFromJSONResponse(jsonResponse));
	}

	private String callHubMailjetApiKeys(String login, String password) throws Exception {
		ClientResource resource = null;
		String result = null;
		try {
			resource = RestletClientHttpHandler.getHandler().getClientResource(mailjetApiKeysAPIUrl);
			resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
			resource.get();
			Response response = resource.getResponse();		
		    if(!response.getStatus().isSuccess()){
				throw new Exception("GetSmtpServerInfo service error when getting information : " + response.getEntityAsText());
			}
		    result = response.getEntity().getText();
 		} catch (ResourceException e) {

			throw new Exception("Error GETing SMTP information from hub:"+resource.getResponse());
		} catch (IOException e) {

 			throw new Exception("Error GETing SMTP information from hub:"+resource.getResponse());
 		} finally {
 			RestletClientHttpHandler.getHandler().closeClientResource(resource);
 		}

	    return result;
 	}

	private String getAPITokenFromJSONResponse(String jsonResponse) throws JSONException {
		JSONObject jsonObj=new JSONObject(jsonResponse);
		return jsonObj.getString("token");

	}

	private APIKeys unScrambleApiKeys(String scrambledKeys) throws Exception {

		String unScrambledApiKeys = unScramble(scrambledKeys);
		int separatorPos = unScrambledApiKeys.indexOf(TOKEN_SEPARATOR);
		String apiPublicKey=unScrambledApiKeys.substring(0, separatorPos);
		String apiPrivateKey= unScrambledApiKeys.substring(separatorPos + TOKEN_SEPARATOR.length(), unScrambledApiKeys.length());
		if (apiPublicKey==null || apiPrivateKey==null) {
			throw new Exception("API Keys are null");
		}
		APIKeys apiKeys=new APIKeys(apiPublicKey,apiPrivateKey);
		return apiKeys;

	}

	private String unScramble(String toUnscramble) {
		String scrambleKey = SCRAMBLINGKEY;

		String b64 = new String(Base64.getDecoder().decode(toUnscramble));
		return xorObfuscate(b64, scrambleKey);
	}

	private String xorObfuscate(String toScramble, String key) {
		String sEncrypted = "";
		int keyLen = key.length();
		for (int i = 0; i < toScramble.length(); i++) {
			int c = toScramble.charAt(i) ^ key.charAt(i % keyLen);
			sEncrypted = sEncrypted + (char) c;
		}
		return sEncrypted;
	}

	public static void main(String[] args) {

//		HubGetMailjetAPIToken getMJtoken=new HubGetMailjetAPIToken();
//		APIKeys keys;
//		try {
//			keys = getMJtoken.getMailjetApiKeys("hubafnor_prod", "gf245gre7zko");
//			System.out.println("Mailjet API Keys:"+keys.publicKey +" / "+keys.privateKey);
//		} catch (Exception e) {
//			System.err.println("cannot get APIKeys:"+e.getMessage());
//			e.printStackTrace(System.err);
//		}

	}
}
