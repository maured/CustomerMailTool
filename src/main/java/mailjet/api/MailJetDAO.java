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
import data.treatment.GetDate;
import dma.restconnexion.InfoConnexionClient;
import exception.MyException;
import mailjet.details.per.date.YearData;
import org.json.JSONArray;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class MailJetDAO{
	
	/*Created to set a dynamical current date in the filters of GET's method call.*/
	private String dateForFilter()
	{
		DateFormat sdt = new SimpleDateFormat("yyyy" + "-01-01'T'00:00:00");
		Date today = Calendar.getInstance().getTime();
		String myDateFormat = sdt.format(today);

		return myDateFormat;
	}
	
/*	 
	While a mailjet DAO instance is running (JVM running) information of connexion will be stocked
	in my connexion object
*/
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

	/* For the first important information send (Name & ID of the client)  */
	public ApiClient getClient() throws MailjetSocketTimeoutException, MailjetException {
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Apikey.resource);
		MailjetResponse response = client.get(request);
		
		JSONArray clientData = response.getData();

		ApiClient[] apiClient = new Gson().fromJson(String.valueOf(clientData), ApiClient[].class);
		
		return apiClient[0];
	}

/* ------------------------------------------------------------------------------------------------------/
		The three above methods are for GET call. We have set the filter with the same timestamp
		for the two Mailjet API calls. If we don't, the informations will not match.
*/
	public ApiCampaign[] getCampaignSortedByRecentDate() throws MailjetSocketTimeoutException, MailjetException {
		String currentYear = dateForFilter();
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaign.resource)
				.filter(Campaign.FROMTS, currentYear)
				.filter(Campaign.LIMIT, "0");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();
		
		/*A voir comment fonctionne le put. Le but est de stocker les différentes ArrayList que renvoie MJ
		* dans mon jsonArray. Casse le code dans MyExeption. (Problème d'index et de récupération de data)*/
//		JSONArray clientData = new JSONArray();
//		clientData.put(response.getData());
		
		MyException myException = new MyException();
		myException.mailjetAttributEmpty(clientData, "SendStartAt");

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaign[].class);
	}
	
	public ApiCampaignStatistic[] getCampaignStatisticListByRecentDate() throws MailjetSocketTimeoutException, MailjetException {
		String currentYear = dateForFilter();
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaignstatistics.resource)
				.filter(Campaignstatistics.FROMTS, currentYear)
				.filter(Campaignstatistics.LIMIT, "0");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();
		
		MyException myException = new MyException();
		myException.mailjetAttributEmpty(clientData, "CampaignSendStartAt");
		
		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaignStatistic[].class);
	}
	/*--------------------------------------------------------------------------------*/

	/* This methods are for POST call*/
	public ApiCampaign[] getCampaignSorted(GetDate year) throws MailjetSocketTimeoutException, MailjetException {

		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaign.resource)
				.filter(Campaign.FROMTS, year.getDate()) 
				.filter(Campaign.LIMIT, "0");
		MailjetResponse response = client.get(request);
		
		JSONArray clientData = response.getData();
		MyException myException = new MyException();
		myException.mailjetAttributEmpty(clientData, "SendStartAt");
		
		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaign[].class);
	}

	public ApiCampaignStatistic[] getCampaignStatisticSorted(GetDate year/*a changer par "date"*/) throws MailjetSocketTimeoutException, MailjetException {
		
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaignstatistics.resource)
				.filter(Campaign.FROMTS, year.getDate())
				.filter(Campaign.LIMIT, "0");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();
		MyException myException = new MyException();
		myException.mailjetAttributEmpty(clientData, "CampaignSendStartAt");

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaignStatistic[].class);
	}

	/*Code de test*/
	public JSONArray oneYearOfCampaign(JSONArray clientData) throws ParseException {
		// ce code de test doit être réalisé dans Mailjet DAO directement pour avoir accès aux données dans clientData
		if (clientData != null)
		{
			for (int i = 0; i <= clientData.length() - 1; i++)
			{
				if (i == clientData.length() - 1)
				{
					Calendar cal = Calendar.getInstance();
					
					/*I use string cause no getDate method is available with getJSONObject()*/
					String str = clientData.getJSONObject(i).getString("SendStartAt");
					SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					Date d = dt.parse(str);
					
					cal.setTime(d);
					int monthCal = cal.get(Calendar.MONTH) + 1;
					
					
					// je doit set dans GetDate la date ou le mois de la derniere campagne reçu dans clientData.
					// Trouver commenyt la récupérer et la setter dans getDate.
				}
			}
		}
		/*fin de test*/
		return clientData;
	}
}
