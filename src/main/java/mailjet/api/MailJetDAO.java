package mailjet.api;

import com.google.gson.Gson;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.Resource;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Apikey;
import com.mailjet.client.resource.Campaign;
import com.mailjet.client.resource.Campaignstatistics;
import dma.test.restconnexion.InfoConnexionClient;
import org.json.JSONArray;

public class MailJetDAO{
	/*tant que nous avons une instance de mailJet DAO (que la JVM tourne) les infos de connxion seront 
	stockées dans mon objet connexion*/
	private InfoConnexionClient connexion;
	
	//private String dateTime = "1970-01-02T13:00:00";
	
	public MailJetDAO(InfoConnexionClient infoConnexionClient) {
		this.connexion = infoConnexionClient;
	}

	private  MailjetClient getAccessToSpecificClient() throws MailjetSocketTimeoutException, MailjetException {
		String pubKey = this.connexion.getPubKey();
		String privKey = this.connexion.getPrivKey();

		return new MailjetClient(pubKey, privKey);
	}

	private String request (Resource resource, String filter) {
		try {
			MailjetClient mailjetClient = getAccessToSpecificClient();

			MailjetRequest request = new MailjetRequest(resource);

			if (filter != "") {
				request.filter(filter, "2018-01-01T00:00:00");
			}

			MailjetResponse response = mailjetClient.get(request);

			JSONArray clientData = response.getData();

			return clientData.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public ApiClient getClient() {
		String json = request(Apikey.resource, "");
		ApiClient[] apiClient = new Gson().fromJson(json, ApiClient[].class);

		return apiClient[0];
	}

	public ApiCampaignStatistic[] getCampaignStatisticList() {
		String json = request(Campaignstatistics.resource, Campaignstatistics.FROMTS);

		return new Gson().fromJson(json, ApiCampaignStatistic[].class);
	}

	public ApiCampaign[] getCampaignList() {
		String json = request(Campaign.resource, Campaign.FROMTS);

		return new Gson().fromJson(json, ApiCampaign[].class);
	}
}
