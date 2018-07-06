package dma.test.restconnexion;

import com.google.gson.Gson;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import data.treatment.CampaignSortedByMonth;
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
import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginController{

	/*chant que toutes les methodes de l'api Mailjet vont être implémentées dans mailJetDAO, pour chaque nouvelles
	routes je n'aurais plus qu'à appeler un mailJetDAO.maMethode(). */
	// instantiated at null for security 
	private static MailJetDAO mailJetDAO = null;

	//Implementation of @Autowired to avoid many other configuration in another files 
	@Autowired public LoginController() {

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
	Moreover, this one send back a json with the Name & ID values */
	@CrossOrigin(origins = "*", allowedHeaders = "*")//http://192.068.1.110:3003
	@RequestMapping(value = "/auth/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(value = HttpStatus.OK)
	//ici Spring m'instancie ma class InfoConnexionClient pour avoir accès aux attributs clé priv et clé pub
	public @ResponseBody String login(@RequestBody InfoConnexionClient infoConnexionClient)
			throws MailjetSocketTimeoutException, MailjetException {
		/* ici on instancie un mailJetDAO dans le but d'avoir un point d'accès global aux info de connexion */
		mailJetDAO = new MailJetDAO(infoConnexionClient);
		ApiClient apiClient = mailJetDAO.getClient();

		System.out.println(infoConnexionClient.getPubKey());
		System.out.println(infoConnexionClient.getPrivKey());

		return toJson(new Client(apiClient));
	}

	private String toJson(Object obj) {
		return new Gson().toJson(obj);
	}
	/*------------------------------------------------------------------------------------------------------*/
	
	/* ------------------------------------------------------------------------------------------------------/
		This one send back a json with the name Value */
	@CrossOrigin(origins = "*", allowedHeaders = "*") @RequestMapping(value = "/campaign", method = RequestMethod.GET) @ResponseStatus(value = HttpStatus.OK) public String listCampaign()
			throws MailjetSocketTimeoutException, MailjetException {
		if (mailJetDAO == null) {
			MyException myException = new MyException();
			return myException.badRequest();
		}

		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignList();
		ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignStatisticList();

		ArrayList<Campaign> campaigns = new ArrayList<>();
		for (ApiCampaign apiCampaign : apiCampaigns) {
			Campaign campaign = new Campaign(apiCampaign);
			ApiCampaignStatistic statistic = findStatisticFromDate(apiStatistics, campaign.Subject);

			if (statistic != null) {
				campaign.setProcessedCount(statistic.ProcessedCount);
				campaign.setDeliveredCount(statistic.DeliveredCount);
			}
			campaigns.add(campaign);
		}
		return toJson(campaigns);
	}

	// mettre en get / utiliser le tots sur la curretn date
	// Cvoir le redirect vers du post avec le formatagge de la date renvoyé par kévin

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/newpage", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String listCampaignByMonth() throws MailjetSocketTimeoutException, MailjetException {
		if (mailJetDAO == null) {
			MyException myException = new MyException();
			return myException.badRequest();
		}
		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignSortedByRecentDate();
		ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignStatisticListByRecentDate();

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
		yearData.setYear(sortedByMonth.getMapCampaign(campaigns));
		return toJson(yearData);
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/newpage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String listCampaignByMonth(@RequestBody YearData pYear) throws MailjetSocketTimeoutException, MailjetException {
		if(mailJetDAO == null) {
			MyException myException = new MyException();
			return myException.badRequest();
		}
		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignSorted(pYear);
		ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignStatisticList();
		
		ArrayList<Campaign> campaigns = new ArrayList<>();
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
		return toJson(campaigns);
	}
 }