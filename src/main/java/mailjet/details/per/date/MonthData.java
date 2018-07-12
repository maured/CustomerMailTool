package mailjet.details.per.date;

import mailjet.Campaign;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MonthData{
	
	public Integer MonthProcessedCount;
	public ArrayList<Campaign> months = new ArrayList<>();
	public Hashtable<Integer, List<Campaign>> monthTable = new Hashtable<>();
	
	public Integer getMonthProcessedCount() {
		return MonthProcessedCount;
	}
	public void setMonthProcessedCount(Integer monthProcessedCount) {
		MonthProcessedCount = monthProcessedCount;
	}

	public ArrayList<Campaign> getMonths() {
		return months;
	}
	public void setMonths(ArrayList<Campaign> months) {
		this.months = months;
	}
	
	public Hashtable<Integer, List<Campaign>> getMonthTable() {
	
		return monthTable;
	}
	public void setMonthTable(Hashtable<Integer, List<Campaign>> monthTable) {
		this.monthTable = monthTable;
	}
	
	public MonthData(Campaign campaign)
	{
		if (campaign != null)
		{
			this.MonthProcessedCount = campaign.ProcessedCount;
		}
	}
	
	public Integer calculMailSendByMonth(Integer monthProcessedCount)
	{
		Integer result = 0;
		if (monthProcessedCount != null)
		{
			result = result + monthProcessedCount;
		}
		return result;
	}
}
