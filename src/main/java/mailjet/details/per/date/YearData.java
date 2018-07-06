package mailjet.details.per.date;

import mailjet.Campaign;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class YearData{

	Hashtable<Integer, List<Campaign>> year = new Hashtable<>();
	Date date;
	
	public Hashtable<Integer, List<Campaign>> getYear() {
		return year;
	}

	public void setYear(Hashtable<Integer, List<Campaign>> year) {
		this.year = year;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
	
	/*---------------------
	* 	test	*/
	ArrayList<MonthData> monthData = new ArrayList<>();

	public ArrayList<MonthData> getMonthData() {
		return monthData;
	}

	public void setMonthData(ArrayList<MonthData> monthData) {
		this.monthData = monthData;
	}
}
