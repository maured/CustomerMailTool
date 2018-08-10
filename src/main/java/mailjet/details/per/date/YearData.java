package mailjet.details.per.date;

import java.util.ArrayList;

public class  YearData{

	int year;
	public int getDate() {
		return year;
	}
	public void setDate(int year) {
		this.year = year;
	}
	String nameClient;
	String clientID;

	public void setNameClient(String nameClient) {
		this.nameClient = nameClient;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	ArrayList<MonthData> monthData = new ArrayList<>();
	public ArrayList<MonthData> getMonthData() {
		return monthData;
	}
	public void setMonthData(ArrayList<MonthData> monthData) {
		this.monthData = monthData;
	}
	
}
