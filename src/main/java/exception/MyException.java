package exception;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.json.JSONArray;
import org.json.JSONObject;

public class MyException
{
	/*This method check if the user has correctly informed the right information of connexion */
	public String badRequest() {
		JSONObject obj = new JSONObject();
		obj.put("message", "You lost your connexion, or you didn't informed the keys. Please go to home page and make sur to write good information");

		return String.valueOf(obj);
	}
	
	public String anyDataException()
	{
		Error error = new Error();
		
		JSONObject obj = new JSONObject();
		obj.put("errorStatus", 542);
		
		return String.valueOf(obj);
	}
	
	/*Ajouter des exceptions si Mailjet crash (TimeOut par exemple)*/
	
	/*This is for all campaign with an important Attribute with Null value*/
	public JSONArray mailjetAttributEmpty(JSONArray clientData, String attributeName)
	{
		if (clientData != null)
		{
			for (int i = 0; i <= clientData.length()-1; i++)
			{
				//if (clientData.length() == 0)
				/*checker le fait de remove le seul element et de me retrouver avec une liste vide*/

				if ("".equals(clientData.getJSONObject(i).get(attributeName).toString()))
				{
					/*A implémenter plus tard : Créer un objet comme ApiCampaign qui récupère toutes les
					campagnes qui possède un attribut CreatedAt qui a une valeur & avec l'attribut SendStartAt
					SANS valeur. Le but sera d'avoir une liste de campagnes créées ET non envoyées*/
					
					clientData.remove(i);
					if (i > 0)
						i--;
				}
			}
		}
		return clientData;
	}
}
