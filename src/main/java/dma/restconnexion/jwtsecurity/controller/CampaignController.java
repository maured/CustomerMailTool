package dma.restconnexion.jwtsecurity.controller;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import data.treatment.CampaignSortedByMonth;
import data.treatment.CampaignSortedByYear;
import data.treatment.GetDate;
import dma.restconnexion.UserInfosConnexion;
import dma.restconnexion.hub.HubCall;
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

	private ApiCampaignStatistic findStatisticFromDate(ApiCampaignStatistic[] statistics, String subject) {
		for (ApiCampaignStatistic statistic : statistics) {
			if (statistic.CampaignSubject.compareTo(subject) == 0) { /* 0 means it's equal*/
				return statistic;
			}
		}
		return null;
	}

	private String isTokenValid(HttpHeaders header) {
		
		String checkToken = header.getFirst(HttpHeaders.AUTHORIZATION);
		
		if (header != null) 
		{
			String checkTokenTrunked = checkToken.substring(7);
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
							if (check.compareTo(listUserConnected.get(i).getExpirationDate()) < 0)
							{
								HubCall hub = new HubCall();
								listUserConnected.get(i).setLastPasswordReset(new Date());
								listUserConnected.get(i).setExpirationDate(hub.calculateExpirationDate(listUserConnected.get(i).getLastPasswordReset()));
								//If it's a match but the token isn't expired we give mailjet keys access
								CampaignController.mailJetDAO = new MailJetDAO(listUserConnected.get(i));

								System.out.println(listUserConnected.get(i).getPublicK());//remove later
								System.out.println(listUserConnected.get(i).getPrivateK()); //remove later
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
				else
					return "tokenExpired";
			} catch (Exception e) {
//				System.err.println("cannot get APIKeys:" + e.getMessage());
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
			DateFormat sdt = new SimpleDateFormat("yyyy" + "-01-01'T'00:00:00");
			String dateAsString = mailJetDAO.dateForFilter();

			Date dateYear = sdt.parse(dateAsString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateYear);

			ApiClient apiClient = mailJetDAO.getClient();
			Client client = new Client(apiClient);
			
			ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignsForAYear(cal.get(Calendar.YEAR));
			
			if (apiCampaigns.length == 0) //i check to prevent IndexOutOfboundException when we will be the 01/01/new year and any campaign were sent yet.
				return new ResponseEntity<>(new ApiCampaign[0], HttpStatus.NO_CONTENT);
			
			ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignsStatisticsForAYear(cal.get(Calendar.YEAR));

			ArrayList<Campaign> campaigns = new ArrayList<>();
			YearData yearData = new YearData();
			
			yearData.setNameClient(client.getNameClient());
			yearData.setClientID(client.getIdClient());

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
			// A break si c'est null au lieu de renvoyer un jsoin avec un message.
			//sinon j'execute le reste des traitements.
			if (apiCampaigns.length == 0)
				return new ResponseEntity<>(new ApiCampaign[0], HttpStatus.NO_CONTENT);
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

			return new ResponseEntity<>(new CampaignSortedByYear().getMyListYears(yearMap, yearData), HttpStatus.OK);
		}	
	}
 }