package dma.restconnexion.jwtsecurity.controller;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import data.treatment.CampaignSortedByMonth;
import data.treatment.CampaignSortedByYear;
import data.treatment.GetDate;
import dma.restconnexion.jwtsecurity.model.UserInfosConnexion;
import dma.restconnexion.hub.HubCall;
import logger.MyLogger;
import mailjet.Campaign;
import mailjet.Client;
import mailjet.api.ApiCampaign;
import mailjet.api.ApiCampaignStatistic;
import mailjet.api.ApiClient;
import mailjet.api.MailJetDAO;
import mailjet.details.per.date.YearData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CampaignController{
	
	// Instantiated at null for jwtsecurity 
	private static MailJetDAO mailJetDAO = null;

	//Implementation of @Autowired to avoid many other configuration in another files 
	@Autowired public CampaignController() {

	}

	private ApiCampaignStatistic findStatisticFromSubject(ApiCampaignStatistic[] statistics, String subject) {
		for (ApiCampaignStatistic statistic : statistics) {
			if (statistic.CampaignSubject.compareTo(subject) == 0) { /* 0 means it's equal*/
				return statistic;
			}
		}
		return null;
	}

	private String isTokenValid(HttpHeaders header) {
		MyLogger logger = new MyLogger();
		String checkToken = header.getFirst(HttpHeaders.AUTHORIZATION);
		
		if (header != null) 
		{
			// 7 = Bearer[space]
			String checkTokenTrunked = checkToken.substring(7);//i keep just the token.  
			List<UserInfosConnexion> listUserConnected = UserInfosConnexion.getListUserConnected();
			boolean hasMatched = false;

			try {
				if (!listUserConnected.isEmpty())
				{
					for (int i = 0; i < listUserConnected.size() && !hasMatched; i++)
					{
						if (listUserConnected.get(i).getTokenJWT().equals(checkTokenTrunked))
						{
							hasMatched = true;
							Date check = new Date();
		//i check if the date of the asked request is inferior than the expirationDate calculated before.
							if (check.compareTo(listUserConnected.get(i).getExpirationDate()) < 0) 
							{
								HubCall hub = new HubCall();
								listUserConnected.get(i).setLastPasswordReset(new Date());
								listUserConnected.get(i).setExpirationDate(hub.calculateExpirationDate(listUserConnected.get(i).getLastPasswordReset()));
								//If it's a match but the token isn't expired we give mailjet keys access
								CampaignController.mailJetDAO = new MailJetDAO(listUserConnected.get(i));
								
								logger.infoLevel("Public key recovered : " + listUserConnected.get(i).getPublicK());
								logger.infoLevel("Private key recovered : " + listUserConnected.get(i).getPrivateK());
							}
							else
							{
								listUserConnected.remove(i);
								i--;
								return "tokenExpired";
							}
						}
					}
				}
//i set always a user in the list when he is logging in (LoginController). If the list isEmpty it means that
//the user try to get access to /api/campaign-statistics without any logs.			
				else 
					return "tokenExpired";
			} catch (Exception e) {
				logger.errorLevel("cannot get APIKeys:" + e.getMessage());
				return "Unauthorized";
			}
		}
		return "Token Authorized";
	}
	
/* ------------------------------------------------------------------------------------------------------/
	This route is the core of the Sorted Data Page. It send back to the front all data sorted by month and 
	years.
*/
	@RequestMapping(value = "/api/campaign-statistics", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> listCampaignByMonth(@RequestHeader HttpHeaders header)
			throws MailjetSocketTimeoutException, MailjetException, ParseException {
		
		String str = isTokenValid(header);
		
		if ("Unauthorized".equals(str) || "tokenExpired".equals(str))
		{
			JSONObject r= new JSONObject();
			r.put("message", str);
			return new ResponseEntity<>(r.toString(), HttpStatus.UNAUTHORIZED);
		}
		else
		{
			//i assign the date for the get in order to get the Current Year
			DateFormat sdt = new SimpleDateFormat("yyyy" + "-01-01'T'00:00:00");
			String dateAsString = mailJetDAO.dateForFilter();
			Date dateYear = sdt.parse(dateAsString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateYear);

			//i make my first Mailjet call to get client's Name and client's ID value. 
			ApiClient apiClient = mailJetDAO.getClient();
			Client client = new Client(apiClient);
			
			//i make my multiple campaign calls and i stock them in an array of campaigns. 
			ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignsForAYear(cal.get(Calendar.YEAR));
			
			if (apiCampaigns.length == 0) //i check to prevent IndexOutOfboundException when we will be the 01/01/new year and any campaign were sent yet.
				return new ResponseEntity<>(new ApiCampaign[0], HttpStatus.NO_CONTENT);
			
			//i do the same thing here for campaignstatistics instead of campaign. 
			ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignsStatisticsForAYear(cal.get(Calendar.YEAR));

			ArrayList<Campaign> campaigns = new ArrayList<>();
			YearData yearData = new YearData();
			
			//i set the Name and the ID. 
			yearData.setNameClient(client.getNameClient());
			yearData.setClientID(client.getIdClient());

			//i concat the two last calls in an array of campaign(Here i'm talking about my custom campaign,
			// with values i want from the two other)
			for (ApiCampaign apiCampaign : apiCampaigns) {
				Campaign campaign = new Campaign(apiCampaign);
				ApiCampaignStatistic statistic = findStatisticFromSubject(apiStatistics, campaign.Subject);

				if (statistic != null) {
					campaign.setProcessedCount(statistic.ProcessedCount);
					campaign.setDeliveredCount(statistic.DeliveredCount);
				}
				campaigns.add(campaign);
			}
			CampaignSortedByMonth sortedByMonth = new CampaignSortedByMonth();
			TreeMap<Integer, ArrayList<Campaign>> yearMap;
			yearMap = sortedByMonth.getMapCampaign(campaigns);

			return new ResponseEntity<>(new CampaignSortedByYear().getMyListYears(yearMap, yearData), HttpStatus.OK);
		}	
	}

	/* ------------------------------------------------------------------------------------------------------/
		This one will be use to avoid the 1000 filter limitation. We will use some post to send back data
		with the exact date that the user want.
	*/
	@RequestMapping(value = "/api/campaign-statistics", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> listCampaignByMonth(@RequestHeader HttpHeaders header, @RequestBody GetDate pDate)
			throws MailjetSocketTimeoutException, MailjetException, ParseException {

		String str = isTokenValid(header);

		if ("Unauthorized".equals(str) || "tokenExpired".equals(str))
		{
			JSONObject r= new JSONObject();
			r.put("message", str);
			return new ResponseEntity<>(r.toString(), HttpStatus.UNAUTHORIZED);
		}
			
		else
		{
			DateFormat sdt = new SimpleDateFormat("yyyy" + "-01-01'T'00:00:00");
			String dateAsString = pDate.getDate();

			Date dateYear = sdt.parse(dateAsString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateYear);
			
			ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignsForAYear(cal.get(Calendar.YEAR));
			if (apiCampaigns.length == 0)
				return new ResponseEntity<>(new ApiCampaign[0], HttpStatus.NO_CONTENT);
			ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignsStatisticsForAYear(cal.get(Calendar.YEAR));

			ArrayList<Campaign> campaigns = new ArrayList<>();
			YearData yearData = new YearData();

			for (ApiCampaign apiCampaign : apiCampaigns)
			{
				Campaign campaign = new Campaign(apiCampaign);
				ApiCampaignStatistic statistic = findStatisticFromSubject(apiStatistics, campaign.Subject);

				if (statistic != null) {
					campaign.setProcessedCount(statistic.ProcessedCount);
					campaign.setDeliveredCount(statistic.DeliveredCount);
				}
				campaigns.add(campaign);
			}

			CampaignSortedByMonth sortedByMonth = new CampaignSortedByMonth();
			TreeMap<Integer, ArrayList<Campaign>> yearMap;
			yearMap = sortedByMonth.getMapCampaign(campaigns);

			return new ResponseEntity<>(new CampaignSortedByYear().getMyListYears(yearMap, yearData), HttpStatus.OK);
		}	
	}
 }