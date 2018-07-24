package data.treatment;

import mailjet.Campaign;
import mailjet.details.per.date.MonthData;
import java.util.ArrayList;

public class GetTotalMailSend{
	public void calculMailSendByMonth(ArrayList<MonthData> arrayOfMonths)
	{
		for (MonthData month : arrayOfMonths) {
			int i = 0;
			int totalMailSend = 0;
			for (Campaign campaign : month.campaignList) {
				int campaignSize = month.campaignList.size();
				int mailSendInCampaign = month.campaignList.get(i).ProcessedCount;
				totalMailSend = totalMailSend + mailSendInCampaign;
				i++;
				if (i == campaignSize)
					month.setTotalMailSend(totalMailSend);
			}
		}
	}
}