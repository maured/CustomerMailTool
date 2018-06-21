package dma.test.restconnexion;

import com.google.gson.Gson;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import data.treatment.GetDate;
import exception.MyException;
import mailjet.Campaign;
import mailjet.Client;
import mailjet.api.ApiCampaign;
import mailjet.api.ApiCampaignStatistic;
import mailjet.api.ApiClient;
import mailjet.api.MailJetDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginController {
	// instantiated at null for security 
	private static MailJetDAO mailJetDAO = null;

	//Implementation of @Autowired to avoid many other configuration in another files 
	@Autowired
	public LoginController() {
		
	}

	private ApiCampaignStatistic findStatisticFromDate (ApiCampaignStatistic[] statistics, Date date) {
		for (ApiCampaignStatistic statistic : statistics)
		{
			if (statistic.CampaignSendStartAt.compareTo(date) == 0) { /* 0 means it's equal*/
				return statistic;
			}
		}
		return  null;
	}
	/* -------------------------------------------------------------------------------------------------------
	Tant qu'on ne passe pas par ma connexion ici, on accède pas aux autres requêtes
 */
	@CrossOrigin(origins = "*", allowedHeaders = "*")//http://192.068.1.110:3003
	@RequestMapping(value = "/auth/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
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

	private String toJson (Object obj) {
		return new Gson().toJson(obj);
	}

	/* -------------------------------------------------------------------------------------------------------
	Sachant que toutes les methodes de l'api Mailjet vont être implémentées dans mailJetDAO, pour chaque nouvelles
	routes je n'aurais plus qu'à appeler un mailJetDAO.maMethode(). 
*/
	
//This one send back a json with the name Value
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/campaign", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String listCampaign() throws MailjetSocketTimeoutException, MailjetException {
		if(mailJetDAO == null) {
			MyException myException = new MyException();
			return myException.badRequest();
		}

		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignList();
		ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignStatisticList();

		ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
		for (ApiCampaign apiCampaign : apiCampaigns)
		{
			Campaign campaign = new Campaign(apiCampaign);
			ApiCampaignStatistic statistic = findStatisticFromDate(apiStatistics, campaign.Date);

			if (statistic != null) {
				campaign.setProcessedCount(statistic.ProcessedCount);
				campaign.setDeliveredCount(statistic.DeliveredCount);
			}

			campaigns.add(campaign);
		}
		return toJson(campaigns);
	}

//	@CrossOrigin(origins = "*", allowedHeaders = "*")
//	@RequestMapping(value = "/NouvellePagePasEncoreDeNom", method = RequestMethod.GET)
//	@ResponseStatus(value = HttpStatus.OK)
//	public String listCampaignByMonth() throws MailjetSocketTimeoutException, MailjetException {
//		if(mailJetDAO == null) {
//			MyException myException = new MyException();
//			return myException.badRequest();
//		}
//
//		ApiCampaign[] apiCampaigns = mailJetDAO.getCampaignList();
//		ApiCampaignStatistic[] apiStatistics = mailJetDAO.getCampaignStatisticList();
//
//		ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
//		for (ApiCampaign apiCampaign : apiCampaigns)
//		{
//			Campaign campaign = new Campaign(apiCampaign);
//			ApiCampaignStatistic statistic = findStatisticFromDate(apiStatistics, campaign.Date);
//			ApiCampaignStatistic monthStatistic = addProcessedMailFromDate(apiStatistics, campaign.Date);
//
//			if (statistic != null) {
//				campaign.setProcessedCount(statistic.ProcessedCount);
//				campaign.setDeliveredCount(statistic.DeliveredCount);
//			}
//
//			campaigns.add(campaign);
//			
//			if (monthStatistic != null) {
//				campaign.se
//			}
//		}
//
//		return toJson(campaigns);
//	}
//private ApiCampaignStatistic addProcessedMailFromDate (ApiCampaignStatistic[] statistics, Date date) {
//	for (ApiCampaignStatistic statistic : statistics)
//	{
//		if (statistic.CampaignSendStartAt.compareTo(date) == 0) { /* 0 means it's equal*/
//			return statistic;
//		}
//	}
//	return  null;
//}
}