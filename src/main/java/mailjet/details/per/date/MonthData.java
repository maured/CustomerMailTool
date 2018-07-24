package mailjet.details.per.date;

import mailjet.Campaign;
import java.util.List;


public class MonthData{
	public String monthName;
	public String getMonthName() {
		return this.monthName;
	}
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	
	public Integer totalMailSend;
	public Integer getMonthProcessedCount() {
		return totalMailSend;
	}
	public void setMonthProcessedCount(Integer totalMailSend) {
		this.totalMailSend = totalMailSend;
	}


	public List<Campaign> campaignList;
	public List<Campaign> getCampaignList() {
		return this.campaignList;
	}
	public void setCampaignList(List<Campaign> campaignList) {
		this.campaignList = campaignList;
	}
	
	
	public MonthData()
	{
		
	}
	
	public MonthData(Campaign campaign)
	{
		if (campaign != null)
		{
			this.totalMailSend = campaign.ProcessedCount;
		}
	}
	
	public Integer calculMailSendByMonth(Integer campaignProcessedCount)
	{
		Integer result = 0;
		if (campaignProcessedCount != null)
		{
			result = result + campaignProcessedCount;
		}
		return result;
	}
}
