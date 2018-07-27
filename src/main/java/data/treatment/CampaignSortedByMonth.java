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
			
			//else if (annéeEnCour != lastYear  && annéeEnCour > lastYear)if we are at the end of a year and we want go to january again

			if(month != lastMonth) //If my campagn.SendingDate month is different from my last read month.
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
						
//						Puis, attendre les post de kévin pour ensuite bouclé sur les appels mailjets tant qu'on a pas
//						toutes les campagnes jusqu'au janvier de l'année d'après OU Jusqu'au jour le plus récent.
					}
					
					//if we already have mounted a january node with data from last year we don't want delete them.
					// Je pourrais vraiment le tester une fois que la methode POST avec la BOUCLE sera implémenté
					//					if (month == 1 && mapCampaign.containsKey(month) && year != lastYear)
					//					{
					//						mapCampaign.put(lastMonth, maList);
					//						maList = new ArrayList<>();
					//						break; //NumberFormatException:
					//						/*Here i don't want add the campaign which is iterating in the list*/
					//						/*I don't want to iterate through campaigns list (stop everything to not put an empty list in the map)*/
					//
					//					}

					// DANS LE CAS OU PLUSIEURS CAMPAGNES ONT UNE DATE D'ENVOIE DIFFERENTE DE CREATION.
					if(monthY != month) //MonthY means CreatedAt date. Month is always = to Sending date. 
					{
						//si c'est la première on put la campgne dans la HashTable, Si c'est la seconde ou plus il faut clone / addAll / put / vider et ajouter la campagne en cour d'itération
						
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
