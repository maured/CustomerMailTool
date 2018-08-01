package data.treatment;

import mailjet.Campaign;
import mailjet.details.per.date.MonthData;
import mailjet.details.per.date.YearData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class CampaignSortedByYear{

	public ArrayList<YearData> getMyListYears(TreeMap<Integer, ArrayList<Campaign>> yearMap, YearData yearData) {
		MonthData month = new MonthData();
		GetTotalMailSend getTotalMailSend = new GetTotalMailSend();

		ArrayList<MonthData> myInstancesOfMonths = new ArrayList<>();
		ArrayList<YearData> myListYears = new ArrayList<>();

		Calendar cal = Calendar.getInstance();
		int lastYear = -1;
		
		/* ForEach on different objects is for performance*/
		for(Map.Entry<Integer, ArrayList<Campaign>> value : yearMap.entrySet()) {
			ArrayList<Campaign> listCampain = value.getValue();

			cal.setTime(listCampain.get(0).SendingDate);
			
			String monthCal = (cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
			int yearCal = cal.get(Calendar.YEAR);

			if (yearCal != lastYear)
			{
				if (lastYear != -1)
				{
					if (yearCal < lastYear)
					{
						yearData.setDate(lastYear); // I set the previous year in my actual object
						yearData.setMonthData(myInstancesOfMonths); // I set the ArrayList of InstanceOfMonth in My YearData-Object
						getTotalMailSend.calculMailSendByMonth(myInstancesOfMonths); //I use this to compute all MailSend by month in a same year
						
						myListYears.add(yearData); // I make a list of different years found.

						yearData = new YearData(); // I clean my YearData object
						myInstancesOfMonths = new ArrayList<>(); // I clean my ArrayList

						month.setMonthName(monthCal);
						month.setCampaignList(listCampain); // I set my campaignList in my class MonthData
						myInstancesOfMonths.add(month); // I add my object MonthData in an ArrayL <MonthData>
						month = new MonthData(); // I clean my MonthData Object
					}
					/*Impossible to have a else. In CampaignSortedByMonth class, we could not have more than
					 12 month. (must be implemented).
					 It will be impossible to get a next year > than the last one.*/
				}
				else
				{
					month.setMonthName(monthCal); // I set the actual month.
					month.setCampaignList(listCampain); // I set my campaignList in my class MonthData
					myInstancesOfMonths.add(month); // I add my object MonthData in an ArrayL <MonthData>
					month = new MonthData();
				}
			}
			else
			{
				month.setMonthName(monthCal);
				month.setCampaignList(listCampain); // I set my campaignList in my class MonthData
				myInstancesOfMonths.add(month); // I add my object MonthData in an ArrayL <MonthData>
				month = new MonthData(); // I clean my MonthData Object	
			}


			if (value.equals(yearMap.lastEntry()))
			{
				if (lastYear == -1) //If we have only one campaign
				{
					yearData.setDate(yearCal);
					yearData.setMonthData(myInstancesOfMonths); //I set the ArrayList of InstanceOfMonth in My YearData-Object
					getTotalMailSend.calculMailSendByMonth(myInstancesOfMonths); //I use this to compute all MailSend by month
					
					myListYears.add(yearData); //I make a list of different years found.
				}
				else
				{
					yearData.setDate(lastYear); //I set the previous year in my actual object 
					yearData.setMonthData(myInstancesOfMonths); //I set the ArrayList of InstanceOfMonth in My YearData-Object
					getTotalMailSend.calculMailSendByMonth(myInstancesOfMonths); //I use this to compute all MailSend by month in a same year

					myListYears.add(yearData); //I make a list of different years found.	
				}
			}
			lastYear = yearCal;
		}
		return myListYears;
	}
}
