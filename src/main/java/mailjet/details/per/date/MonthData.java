package mailjet.details.per.date;

import java.util.ArrayList;

public class MonthData{
	
	public String Name;
	public Integer MonthProcessedCount;
	public Integer Facturation;
	
	ArrayList<WeekData> months = new ArrayList<WeekData>();
}
