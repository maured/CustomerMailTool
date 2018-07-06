package mailjet.details.per.date;

import mailjet.Campaign;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class YearData{

	public Hashtable<Integer, List<Campaign>> getYear() {
		return year;
	}

	public void setYear(Hashtable<Integer, List<Campaign>> year) {
		this.year = year;
	}

	Hashtable<Integer, List<Campaign>> year = new Hashtable<>();
	Date date;

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
}
