package exception;

import org.json.JSONObject;

public class MyException
{
	public String badRequest() {
		JSONObject obj = new JSONObject();
		obj.put("message", "You lost you connexion, or you didn't informed the keys. Please go to home page and make sur to write good informations");

		return String.valueOf(obj);
	}
}
