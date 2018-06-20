//package mailjet;
//
//import com.mailjet.client.MailjetClient;
//import com.mailjet.client.MailjetRequest;
//import com.mailjet.client.MailjetResponse;
//import com.mailjet.client.errors.MailjetException;
//import com.mailjet.client.errors.MailjetSocketTimeoutException;
//
//import com.mailjet.client.resource.Statcounters;
//
//public class OneClientStatcountersTest{
//	
//	public static void SomeInformations() throws MailjetException, MailjetSocketTimeoutException {
//		MailjetClient client;
//		MailjetRequest request;
//		MailjetResponse response;
//
//		client = new MailjetClient(System.getenv("95901f5853673f7593f1c84d9ff824c7"), System.getenv("7dccb2162f16f24c0b20b44fb834ff17"));
//
//		request = new MailjetRequest(Statcounters.resource)
//				.filter(Statcounters.SOURCEID, "$Campaign_ID")
//				.filter(Statcounters.COUNTERSOURCE, "Campaign")
//				.filter(Statcounters.COUNTERTIMING, "Message")
//				.filter(Statcounters.COUNTERRESOLUTION, "Lifetime");
//		response = client.get(request);
//		System.out.println(response.getStatus());
//		System.out.println(response.getData());	
//	}
//}
