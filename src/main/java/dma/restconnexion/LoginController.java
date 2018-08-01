package dma.restconnexion;

import com.google.gson.Gson;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import data.treatment.CampaignSortedByMonth;
import data.treatment.CampaignSortedByYear;
import data.treatment.GetDate;
import exception.MyException;
import mailjet.Campaign;
import mailjet.Client;
import mailjet.api.ApiCampaign;
import mailjet.api.ApiCampaignStatistic;
import mailjet.api.ApiClient;
import mailjet.api.MailJetDAO;
import mailjet.details.per.date.YearData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeMap;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginController{

/*tant que toutes les methodes de l'api Mailjet vont être implémentées dans mailJetDAO, pour chaque nouvelles
	routes je n'aurais plus qu'à appeler un mailJetDAO.maMethode().
*/
	
	// Instantiated at null for security 
	private static MailJetDAO mailJetDAO = null;

	//Implementation of @Autowired to avoid many other configuration in another files 
	@Autowired public LoginController() {

	}

	private String toJson(Object obj) {
		return new Gson().toJson(obj);
	}

	private ApiCampaignStatistic findStatisticFromDate(ApiCampaignStatistic[] statistics, String subject) {
		for (ApiCampaignStatistic statistic : statistics) {
			if (statistic.CampaignSubject.compareTo(subject) == 0) { /* 0 means it's equal*/
				return statistic;
			}
		}
		return null;
	}

/* ------------------------------------------------------------------------------------------------------/
	While we don't go through this route, we don't have access to another routes/requests.
	Moreover, this one send back a json with the Name & ID values
*/
	@CrossOrigin(origins = "*", allowedHeaders = "*")//http://192.068.1.110:3003
	@RequestMapping(value = "/auth/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(value = HttpStatus.OK)
	
//Here, Spring instantiate my InfoConnexionClient class to get access to the private & public keys attributes
	public @ResponseBody String login(@RequestBody InfoConnexionClient infoConnexionClient)
			throws MailjetSocketTimeoutException, MailjetException {
		
/* We instantiate a MailjetDAO in order to make a global access point to the connexion information */
		mailJetDAO = new MailJetDAO(infoConnexionClient);
		ApiClient apiClient = mailJetDAO.getClient();

		System.out.println(infoConnexionClient.getPubKey());
		System.out.println(infoConnexionClient.getPrivKey());

		return toJson(new Client(apiClient));
	}
	
/* ------------------------------------------------------------------------------------------------------/
	This route is the core of the Sorted Data Page. It send back to the front all data sorted by month and 
	years.
*/
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/campaign-statistics", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String listCampaignByMonth()
			throws MailjetSocketTimeoutException, MailjetException, ParseException {
		if (mailJetDAO == null) {
			MyException myException = new MyException();
			return myException.badRequest();
		}

		DateFormat sdt = new SimpleDateFormat("yyyy" + "-01-01'T'00:00:00");
		String dateAsString = mailJetDAO.dateForFilter();
		
		Date dateYear = sdt.parse(dateAsString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateYear);
		
		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignsForAYear(cal.get(Calendar.YEAR));
		if (apiCampaigns.length == 0) //i check to prevent IndexOutOfboundException when we will be the 01/01/new year and any campaign were sent yet.
		{
			MyException myException = new MyException();
			return myException.anyDataException();
		}
		ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignsStatisticsForAYear(cal.get(Calendar.YEAR));

		ArrayList<Campaign> campaigns = new ArrayList<>();
		YearData yearData = new YearData();
		for (ApiCampaign apiCampaign : apiCampaigns) {
			Campaign campaign = new Campaign(apiCampaign);
			ApiCampaignStatistic statistic = findStatisticFromDate(apiStatistics, campaign.Subject);

			if (statistic != null) {
				campaign.setProcessedCount(statistic.ProcessedCount);
				campaign.setDeliveredCount(statistic.DeliveredCount);
			}
			campaigns.add(campaign);
		}
		CampaignSortedByMonth sortedByMonth = new CampaignSortedByMonth();
		TreeMap<Integer, ArrayList<Campaign>> yearMap;
		yearMap = sortedByMonth.getMapCampaign(campaigns);

		return toJson(new CampaignSortedByYear().getMyListYears(yearMap, yearData));
	}
	
/* ------------------------------------------------------------------------------------------------------/
	This one will be use to avoid the 1000 filter limitation. We will use some post to send back data
	with the exact date that the user want.
*/
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/campaign-statistics", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String listCampaignByMonth(@RequestBody GetDate pDate)
			throws MailjetSocketTimeoutException, MailjetException, ParseException {
		if(mailJetDAO == null) {
			MyException myException = new MyException();
			return myException.badRequest();
		}
		
		DateFormat sdt = new SimpleDateFormat("yyyy" + "-01-01'T'00:00:00");
		String dateAsString = pDate.getDate();
		
		Date dateYear = sdt.parse(dateAsString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateYear);
				
		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignsForAYear(cal.get(Calendar.YEAR));
		if (apiCampaigns.length == 0)
		{
			MyException myException = new MyException();
			return myException.anyDataException();
		}
		ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignsStatisticsForAYear(cal.get(Calendar.YEAR));
		
		ArrayList<Campaign> campaigns = new ArrayList<>();
		YearData yearData = new YearData();
		
		for (ApiCampaign apiCampaign : apiCampaigns)
		{
			Campaign campaign = new Campaign(apiCampaign);
			ApiCampaignStatistic statistic = findStatisticFromDate(apiStatistics, campaign.Subject);

			if (statistic != null) {
				campaign.setProcessedCount(statistic.ProcessedCount);
				campaign.setDeliveredCount(statistic.DeliveredCount);
			}
			campaigns.add(campaign);
		}

		CampaignSortedByMonth sortedByMonth = new CampaignSortedByMonth();
		TreeMap<Integer, ArrayList<Campaign>> yearMap;
		yearMap = sortedByMonth.getMapCampaign(campaigns);
		
		return toJson(new CampaignSortedByYear().getMyListYears(yearMap, yearData));
	}
 }