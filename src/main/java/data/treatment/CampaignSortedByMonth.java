package data.treatment;

import mailjet.Campaign;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class CampaignSortedByMonth{
	
	public TreeMap<Integer, ArrayList<Campaign>> getMapCampaign(ArrayList<Campaign> campaigns) {
		
		int lastMonth = -1;
		int month;
		int i = 0;

		HashMap<Integer,ArrayList<Campaign>> mapCampaign = new HashMap<>();
		ArrayList<Campaign> maList  = new ArrayList<>();
		int campaignSize = campaigns.size();

		for(Campaign camp : campaigns) {
			// forced value to -1 to be sure that there is no residue value 
			month = -1;

			Calendar cal = Calendar.getInstance();
			cal.setTime(camp.SendingDate);
			
			month = cal.get(Calendar.MONTH) + 1;
			
			/* Parse of createdAt in order to check if there is a difference (in a same Object/Campaign) between the campaign creation date & the campaign sending date.*/
			Calendar calY = Calendar.getInstance();
			calY.setTime(camp.CreationDate);

			int daY = calY.get(Calendar.DATE);
			int monthY = calY.get(Calendar.MONTH) + 1;
			int yearY = calY.get(Calendar.YEAR);

			/*GESTION d'ERREUR éviter que ça boucle 2 fois sur le même mois pour éviter d'écraser les données d'une année à l'autre*/
			//else if (annéeEnCour != lastYear && annéeEnCour > lastYear)if we are at the end of a year and we want go to january again

			if(month != lastMonth) //if my campagn.SendingDate month is different from my last read month(if it's true), then :
			{
				if(lastMonth != -1) // If my last read month is different from the error value(= if it's a true month), then : 
				{
					if(monthY != month) //MonthY means CreatedAt date. Month is always = to Sending date. 
					{
						//mettre une condition pour gérer le cas où plusieurs campagnes sont dans ce cas et gérer le fait que ce soit la première ou la seconde campagne
						//si c'est la première on put la campgne dans la HashTable, Si c'est la seconde ou plus il faut clone / addAll / put / vider et ajouter la campagne en cour d'itération
						// DANS LE CAS OU PLUSIEURS CAMPAGNES ON UNE DATE D'ENVOIE DIFFERENTE DE CREATION.
						mapCampaign.put(lastMonth, maList);
						maList = new ArrayList<>();
						maList.add(camp);
					}
					else
					{
						if (mapCampaign.containsKey(lastMonth)) //In case where we have an already existing list previously mounted, we want save values and add the new others as a result.
						{
							ArrayList<Campaign> copyList = (ArrayList<Campaign>) mapCampaign.get(lastMonth);
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

			lastMonth = month;
			i++;

			if(i == campaignSize && lastMonth == month)
				mapCampaign.put(lastMonth, maList);
		}
		return new TreeMap<>(mapCampaign);
	}
	
	
}
