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
	public Integer getTotalMailSend() {
		return totalMailSend;
	}
	public void setTotalMailSend(Integer totalMailSend) {
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
}
