//package data.treatment;
//
//import org.json.JSONArray;
//
//public class TreatmentJsonData{
//	
//	public static String[] jsonToStringArray(JSONArray array) {
//		if(array == null)
//			return null;
//		String[] arr = new String[array.length()];
//		for(int i=0; i<arr.length; i++) {
//			arr[i] = array.optString(i);
//		}
//		return arr;
//	}
//	
//	public static String stringArrayToString(String[] strArr, String delimiter)
//	{
//		StringBuilder sb = new StringBuilder();
//		for (String str : strArr)
//			sb.append(str).append(delimiter);
//		return sb.substring(0, sb.length() - 1);
//	}
//	
//	public static String allTreatmentFromMailjetResponse(JSONArray array)
//	{
//		String[] clientDataInArray;
//		String clientDataInString;
//		
//		clientDataInArray = jsonToStringArray(array);
//		clientDataInString = stringArrayToString(clientDataInArray, ",");
//		
//		return clientDataInString;
//		
//		//clientDataParsed = clientDataInString.replaceAll("\"", "\\\\\"");
//		//hookClientInformations(clientDataInString);
//	}
//}
//
//	public static void hookClientInformations(String str)
//	{
//		StringTokenizer tokenizer = new StringTokenizer(str,",");
//		String isActive = tokenizer.nextToken(); //bool
//		String createdAt = tokenizer.nextToken(); //Date
//		String runLevel = tokenizer.nextToken();
//		String acl = tokenizer.nextToken();
//		String quarantineValue = tokenizer.nextToken(); //int
//		String name = tokenizer.nextToken();
//		String APIKey = tokenizer.nextToken();
//		String secretKey = tokenizer.nextToken();
//		String userID = tokenizer.nextToken();
//		String isMaster = tokenizer.nextToken(); //bool
//		String id = tokenizer.nextToken();
//		String skipSpamd = tokenizer.nextToken();//int
//		String trackHost = tokenizer.nextToken();
//
//		System.out.println(name);
//	}
//
//	public static void convertStringToBoolean(String str) throws MyException {
//		int newValueOfMyString;
//
//		if (str != "true" || str != "false") 
//			throw new MyException("Bad properties send. Use this methode to convert String to Boolean");
//		else if (str == "true")
//			newValueOfMyString = 1;
//		else if (str == "false")
//			newValueOfMyString = 0;
//		else
//			System.out.println("Bad properties send. Use this methode to convert String to Boolean");
// }