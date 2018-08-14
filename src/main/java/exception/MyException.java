package exception;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.engine.util.StringUtils;

public class MyException
{
	/*This method check if the user has correctly informed the right information of connexion */
	public String badRequest() 
	{
		JSONObject obj = new JSONObject();
		obj.put("message", "You lost your connexion, or you didn't informed the keys. Please go to home page and make sur to write good information");

		return String.valueOf(obj);
	}
	
	public String anyDataException()
	{
		JSONObject obj = new JSONObject();
		obj.put("errorStatus", 542);
		
		return String.valueOf(obj);
	}
	
	public String badCredentialsException()
	{
		JSONObject obj = new JSONObject();
		obj.put("message", "401. Unauthorized. Bad credentials");
		
		return String.valueOf(obj);
	}
	
	/*Add exceptions if Mailjet crash (TimeOut for exemple)*/
	
	/*This is for all campaign with an important Attribute with Null value*/
	public JSONArray mailjetAttributEmpty(JSONArray clientData, String attributeName)
	{
		/*A implémenter plus tard : Créer un objet comme ApiCampaign, qui récupère toutes les
		campagnes qui possèdent un attribut : CreatedAt qui possède une valeur ET avec l'attribut : SendStartAt
		SANS valeur. Le but sera d'avoir une liste de campagnes créées ET non envoyées*/
		
			JSONArray tt; 
			new JSONArray();
			tt = this.cleanList(clientData, attributeName);
		
			return tt;
	}
		
	private JSONArray cleanList(JSONArray list, String attributeName) {
		JSONArray ret = new JSONArray();
		
		if (!StringUtils.isNullOrEmpty(String.valueOf(list))) {
			for (int i = 0; i < list.length(); i++) {
				if (!"".equals(list.getJSONObject(i).get(attributeName).toString())) {
					ret.put(list.getJSONObject(i));
				}
			}
		}
		return ret;		
	}
}
