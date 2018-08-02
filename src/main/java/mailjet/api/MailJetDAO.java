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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MailJetDAO{
	
	/*Created to set a dynamical current date in the filters of GET's method call.*/
	public String dateForFilter()
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
	
	
	/* This methods are for GET / POST call FOR A YEAR*/
	
	/* This method of treatment could be improve treating by month whereas per year. */
	/*---------------------------- Code for Campaigns Resource ----------------------------*/
	public ApiCampaign[] getCampaignsFromADate(Date date) throws MailjetSocketTimeoutException, MailjetException {
		
		DateFormat sdt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		String myDateFormat = sdt.format(date);
		
		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaign.resource)
				.filter(Campaign.FROMTS, myDateFormat)
				.filter(Campaign.LIMIT, "0");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();
		MyException myException = new MyException();
		myException.mailjetAttributEmpty(clientData, "SendStartAt");

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaign[].class);
	}
	
	public ApiCampaign[]  getCampaignsForAYear(int year) throws MailjetSocketTimeoutException, MailjetException {
		
		ArrayList<ApiCampaign> arrApiCampaignForAYear = new ArrayList<>();
		//We must call MJ : from a date and we stop when we change year.
		
		Calendar calendar = GregorianCalendar.getInstance(Locale.FRANCE);
		
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.DAY_OF_YEAR,1);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		Date fromDate =  calendar.getTime();
		
		calendar=GregorianCalendar.getInstance(Locale.FRANCE);
		
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.MONTH,11);
		calendar.set(Calendar.DAY_OF_MONTH,31);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		Date lastDayOfYear=calendar.getTime();

		Date lastDateUsed = new Date();
		
		while (fromDate.before(lastDayOfYear))
		{
			ApiCampaign[] tmpResultFromMJ = getCampaignsFromADate(fromDate); 
			if (tmpResultFromMJ.length == 0) // Here i check if a MJ call is possible.
				break;
			else
			{
				fromDate = tmpResultFromMJ[tmpResultFromMJ.length - 1].SendStartAt;
				
				Calendar yearToCompare = Calendar.getInstance();
				yearToCompare.setTime(tmpResultFromMJ[0].SendStartAt); // checker sur la premiere campagne
				int yearOfFirstSendingDate = yearToCompare.get(Calendar.YEAR);
				
				if (year < yearOfFirstSendingDate)
					break;
				// now remove the result that are superior to the firstDayOfYear
				// if fromDate is before Years end, I want it all
				if (fromDate.before(lastDayOfYear) && !fromDate.equals(lastDateUsed))
					arrApiCampaignForAYear.addAll(Arrays.asList(tmpResultFromMJ));
				else
				{
					arrApiCampaignForAYear.addAll(Arrays.asList(tmpResultFromMJ));
					break;
				}
			}
			lastDateUsed = fromDate;
		}
		ApiCampaign[] finalResult = new ApiCampaign[arrApiCampaignForAYear.size()];
		if (arrApiCampaignForAYear.isEmpty())
			return finalResult;
		else
		{
			finalResult = arrApiCampaignForAYear.toArray(finalResult);
			System.out.println("fist:" + arrApiCampaignForAYear.get(0).SendStartAt + "    last:"+arrApiCampaignForAYear.get(arrApiCampaignForAYear.size()-1).SendStartAt);
			return finalResult;	
		}
	}
	
	/*---------------------------- Code for CampaignsStatistics resources ----------------------------*/
	public ApiCampaignStatistic[] getCampaignStatisticFromADate(Date date) throws MailjetSocketTimeoutException, MailjetException {

		DateFormat sdt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		String myDateFormat = sdt.format(date);

		MailjetClient client = getAccessToSpecificClient();
		MailjetRequest request = new MailjetRequest(Campaignstatistics.resource)
				.filter(Campaign.FROMTS, myDateFormat)
				.filter(Campaign.LIMIT, "0");
		MailjetResponse response = client.get(request);

		JSONArray clientData = response.getData();
		MyException myException = new MyException();
		myException.mailjetAttributEmpty(clientData, "CampaignSendStartAt");

		return new Gson().fromJson(String.valueOf((clientData)), ApiCampaignStatistic[].class);
	}

	public ApiCampaignStatistic[] getCampaignsStatisticsForAYear(int year) throws MailjetSocketTimeoutException, MailjetException {

		ArrayList<ApiCampaignStatistic> arrApiCampaignForAYear = new ArrayList<>();
		//We must call MJ : from a date and we stop when we change year.

		Calendar calendar = GregorianCalendar.getInstance(Locale.FRANCE);

		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.DAY_OF_YEAR,1);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		Date fromDate =  calendar.getTime();

		calendar=GregorianCalendar.getInstance(Locale.FRANCE);

		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.MONTH,11);
		calendar.set(Calendar.DAY_OF_MONTH,31);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		Date lastDayOfYear=calendar.getTime();

		Date lastDateUsed = new Date();

		while (fromDate.before(lastDayOfYear))
		{
			ApiCampaignStatistic[] tmpResultFromMJ = getCampaignStatisticFromADate(fromDate);	
			if (tmpResultFromMJ.length == 0)
				break;
			else
			{
				fromDate = tmpResultFromMJ[tmpResultFromMJ.length - 1].CampaignSendStartAt;

				Calendar yearToCompare = Calendar.getInstance();
				yearToCompare.setTime(tmpResultFromMJ[0].CampaignSendStartAt); // checker sur la premiere campagne
				int yearOfFirstSendingDate = yearToCompare.get(Calendar.YEAR);

				if (year < yearOfFirstSendingDate)
					break;
				// now remove the result that are superior to the firstDayOfYear
				// if fromDate is before Years end, I want it all
				if (fromDate.before(lastDayOfYear) && !fromDate.equals(lastDateUsed))
					arrApiCampaignForAYear.addAll(Arrays.asList(tmpResultFromMJ));
				else
				{
					arrApiCampaignForAYear.addAll(Arrays.asList(tmpResultFromMJ));
					break;
				}
			}
			lastDateUsed = fromDate;
		}
		ApiCampaignStatistic[] finalResult = new ApiCampaignStatistic[arrApiCampaignForAYear.size()];
		finalResult = arrApiCampaignForAYear.toArray(finalResult);
		System.out.println("fist:" + arrApiCampaignForAYear.get(0).CampaignSendStartAt+ "    last:"+arrApiCampaignForAYear.get(arrApiCampaignForAYear.size()-1).CampaignSendStartAt);
		return finalResult;
	}
}
