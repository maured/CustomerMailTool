package data.treatment;

import mailjet.Campaign;
import org.springframework.boot.jta.narayana.NarayanaBeanFactoryPostProcessor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class CampaignSortedByMonth{
	
	public TreeMap<Integer, ArrayList<Campaign>> getMapCampaign(ArrayList<Campaign> campaigns) {
		
		int lastMonth = -1;
		int month;
		int i = 0;

		int lastYear = 0;
		int year;

		HashMap<Integer,ArrayList<Campaign>> mapCampaign = new HashMap<>();
		ArrayList<Campaign> maList  = new ArrayList<>();
		int campaignSize = campaigns.size();
		
		for(Campaign camp : campaigns) {
			// forced value to -1 to be sure that there is no residue value 
			month = -1;

			Calendar cal = Calendar.getInstance();
			cal.setTime(camp.SendingDate);
			
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			
			/* Parse of createdAt in order to check if there is a difference (in a same Object/Campaign)
			between the campaign creation date & the campaign sending date.*/
			Calendar calY = Calendar.getInstance();
			calY.setTime(camp.CreationDate);
			
			int monthY = calY.get(Calendar.MONTH) + 1;

			if(month != lastMonth) //If my campaign.SendingDate month is different from my last read month.
			{
				if(lastMonth != -1)//If my last read month is different from the error value.
				{
					if (month == 1 && year != lastYear)
					{
						mapCampaign.put(lastMonth, maList);
						maList = new ArrayList<>();
						break;
						/*From here the function GET in login controller is ended. All the other front-end
						call will go through the POST method*/
					}
					
				/*In case where many campaign have a Sending Date different from their own creation date.*/
					if(monthY != month) //MonthY means CreatedAt date. Month is always = to Sending date. 
					{
						//si c'est la première on put la campgne dans la HashTable, Si c'est la seconde ou
						// plus il faut clone / addAll / put / vider et ajouter la campagne en cour d'itération
						
						/* Not tested yet but it works below */
						/*In case where we have an already existing list previously mounted, we want save
						 values and add the new others as a result.*/
						if (mapCampaign.containsKey(lastMonth))
						{
							ArrayList<Campaign> copyList = mapCampaign.get(lastMonth);
							ArrayList<Campaign> clone = (ArrayList<Campaign>) copyList.clone();
							clone.addAll(maList);
							mapCampaign.put(lastMonth, clone);
							maList = new ArrayList<>();
							maList.add(camp);	
						}
						mapCampaign.put(lastMonth, maList);
						maList = new ArrayList<>();
						maList.add(camp);
					}
					else
					{
						/*In case where we have an already existing list previously mounted, we want save
						 values and add the new others as a result.*/
						if (mapCampaign.containsKey(lastMonth))
						{
							ArrayList<Campaign> copyList = mapCampaign.get(lastMonth);
							ArrayList<Campaign> clone = (ArrayList<Campaign>) copyList.clone();
							clone.addAll(maList);
							mapCampaign.put(lastMonth, clone);
							maList = new ArrayList<>();
							maList.add(camp);
						}
						else
						{
							mapCampaign.put(lastMonth, maList);
							maList = new ArrayList<>();
							maList.add(camp);
						}
					}
				}
				else
					maList.add(camp);
			}
			else
				maList.add(camp);
			
			lastYear = year;
			lastMonth = month;
			i++;

			if(i == campaignSize)
				mapCampaign.put(lastMonth, maList);
		}
		return new TreeMap<>(mapCampaign);
	}
}
