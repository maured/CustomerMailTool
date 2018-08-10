package dma.restconnexion.jwtsecurity.controller;

import com.google.gson.Gson;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import data.treatment.CampaignSortedByMonth;
import data.treatment.CampaignSortedByYear;
import data.treatment.GetDate;
import dma.restconnexion.UserInfosConnexion;
import dma.restconnexion.hub.HubCall;
import exception.MyException;
import mailjet.Campaign;
import mailjet.Client;
import mailjet.api.ApiCampaign;
import mailjet.api.ApiCampaignStatistic;
import mailjet.api.ApiClient;
import mailjet.api.MailJetDAO;
import mailjet.details.per.date.YearData;
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

	/*tant que toutes les methodes de l'api Mailjet vont être implémentées dans mailJetDAO, pour chaque nouvelles
		routes je n'aurais plus qu'à appeler un mailJetDAO.maMethode().
	*/
	// Instantiated at null for jwtsecurity 
	private static MailJetDAO mailJetDAO = null;

	//Implementation of @Autowired to avoid many other configuration in another files 
	@Autowired public CampaignController() {

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

	private String isTokenValid(HttpHeaders header) {
		
		String checkToken = header.getFirst(HttpHeaders.AUTHORIZATION);
		
		if (header != null) {
			//substring(7)

			String checkTokenTrunked = checkToken.substring(7);
			
			System.out.println(checkTokenTrunked); //remove later

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
							//ici je regarde si l'expiration est bonne ou pas
							// Si c'est expiré...

							Date check = new Date();
							if (check.compareTo(listUserConnected.get(i).getExpirationDate()) < 0)
							{
								HubCall hub = new HubCall();
								listUserConnected.get(i).setLastPasswordReset(new Date());
								listUserConnected.get(i).setExpirationDate(hub.calculateExpirationDate(
										listUserConnected.get(i).getLastPasswordReset()));
								//If it's a match but the token isn't expired we give mailjet keys access
								CampaignController.mailJetDAO = new MailJetDAO(listUserConnected.get(i));
								//ApiClient apiClient = mailJetDAO.getClient(); // c'etait pour return le nom et l'ID

								System.out.println(listUserConnected.get(i).getPublicK());//remove later
								System.out.println(listUserConnected.get(i).getPrivateK()); //remove later
							}
							else
							{
								listUserConnected.remove(i);
								i--;
								return toJson(new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED));
							}
						}
					}
				}
				//It can't be empty, exception would be handle before.
			} catch (Exception e) {
				System.err.println("cannot get APIKeys:" + e.getMessage());
				e.printStackTrace(System.err);
				return toJson(new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED));
			}
		}
		return "Token Authorized";
	}
	
/* ------------------------------------------------------------------------------------------------------/
	This route is the core of the Sorted Data Page. It send back to the front all data sorted by month and 
	years.
*/
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/campaign-statistics", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String listCampaignByMonth(@RequestHeader HttpHeaders header)
			throws MailjetSocketTimeoutException, MailjetException, ParseException {
		
		isTokenValid(header);
		
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

		ApiClient apiClient = mailJetDAO.getClient();
		Client client = new Client(apiClient);
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
		
		return toJson(new CampaignSortedByYear().getMyListYears(yearMap, yearData));
	}

	/* ------------------------------------------------------------------------------------------------------/
		This one will be use to avoid the 1000 filter limitation. We will use some post to send back data
		with the exact date that the user want.
	*/
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/campaign-statistics", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	
	public @ResponseBody String listCampaignByMonth(@RequestHeader HttpHeaders header, @RequestBody GetDate pDate)
			throws MailjetSocketTimeoutException, MailjetException, ParseException {
		
		isTokenValid(header);
		
		DateFormat sdt = new SimpleDateFormat("yyyy" + "-01-01'T'00:00:00");
		String dateAsString = pDate.getDate();
		
		Date dateYear = sdt.parse(dateAsString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateYear);
				
		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignsForAYear(cal.get(Calendar.YEAR));
		// A break si c'est null au lieu de renvoyer un jsoin avec un message.
		//sinon j'execute le reste des traitements.
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