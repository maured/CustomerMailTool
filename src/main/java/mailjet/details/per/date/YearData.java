package mailjet.details.per.date;

import java.util.ArrayList;

public class  YearData{

	int year;
	public String nameClient;
	public String clientID;
	ArrayList<MonthData> monthData = new ArrayList<>();
	
	public int getDate() {
		return year;
	}
	public void setDate(int year) {
		this.year = year;
	}
	
	public void setNameClient(String nameClient) {
		this.nameClient = nameClient;
	}
	
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	
	public ArrayList<MonthData> getMonthData() {
		return monthData;
	}
	public void setMonthData(ArrayList<MonthData> monthData) {
		this.monthData = monthData;
	}
	
}
