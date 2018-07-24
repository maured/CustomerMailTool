package mailjet.api;

import com.google.gson.Gson;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Apikey;
import com.mailjet.client.resource.Campaign;
import com.mailjet.client.resource.Campaignstatistics;
import dma.restconnexion.InfoConnexionClient;
import mailjet.details.per.date.YearData;
import org.json.JSONArray;

import java.sql.Timestamp;
import java.time.Instant;

public class MailJetDAO{
	/*tant que nous avons une instance de mailJet DAO (que la JVM tourne) les infos de connxion seront 
	stockées dans mon objet connexion*/
	private InfoConnexionClient connexion;

	public MailJetDAO(InfoConnexionClient infoConnexionClient) {
		this.connexion = infoConnexionClient;
	}
	
	/*For the connexion */
	private MailjetClient getAccessToSpecificClient() {
		String pubKey = this.connexion.getPubKey();
		String privKey = this.connexion.getPrivKey();

		return new MailjetClient(pubKey, privKey);
	}

	/* For the first important informations send (Name & ID of the client)  */
	public ApiClient getClient() throws MailjetSocketTimeoutException, MailjetException {
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Apikey.resource);
		MailjetResponse response = client.get(request);
		
		JSONArray clientData = response.getData();

		ApiClient[] apiClient = new Gson().fromJson(String.valueOf(clientData), ApiClient[].class);
		
		return apiClient[0];
	}
	
	/* ---------------------------------------------------------------------------------- /
	This 2 methods above are used to give the 5 attributes in the /campaign route in my controller. This route is for the first page developed in the webapp */
	public ApiCampaign[] getCampaignList() throws MailjetSocketTimeoutException, MailjetException {
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaign.resource)
				.filter(Campaign.FROMTS, "2016-01-01T00:00:00")
				.filter(Campaign.LIMIT, "150");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaign[].class);
	}
	
	public ApiCampaignStatistic[] getCampaignStatisticList() throws MailjetSocketTimeoutException, MailjetException {
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaignstatistics.resource)
				.filter(Campaignstatistics.FROMTS, "2016-01-01T00:00:00")
				.filter(Campaignstatistics.LIMIT, "150");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();
		
		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaignStatistic[].class);
	}
	/*--------------------------------------------------------------------------------*/
	
	
	/* --------------------------------------------------------------------------------/
	The three above methods are for GET call. We have set the filter with the same timestamp for the two Mailjet API calls */
	public ApiCampaign[] getCampaignSortedByRecentDate() throws MailjetSocketTimeoutException, MailjetException {
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaign.resource)
				.filter(Campaign.FROMTS, "2016-01-01T00:00:00")
				.filter(Campaign.LIMIT, "0");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaign[].class);
	}
	
	public ApiCampaignStatistic[] getCampaignStatisticListByRecentDate() throws MailjetSocketTimeoutException, MailjetException {
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaignstatistics.resource)
				.filter(Campaignstatistics.FROMTS, "2016-01-01T00:00:00")
				.filter(Campaignstatistics.LIMIT, "0");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaignStatistic[].class);
	}

	private String getTimestampDate() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Instant instant = timestamp.toInstant();
		String myLastDate = instant.toString();
		return myLastDate;
	}
/*--------------------------------------------------------------------------------*/

	/* This methode is for POST call*/
	public ApiCampaign[] getCampaignSorted(YearData year) throws MailjetSocketTimeoutException, MailjetException {
//		Date date = year.getDate();
//		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		dt.format(date);
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaign.resource)
				.filter(Campaign.TOTS, String.valueOf(year)) 
				.filter(Campaign.LIMIT, "150");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaign[].class);
	}
}
