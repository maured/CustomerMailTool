package mailjet;

import mailjet.api.ApiCampaign;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class Campaign {
    public Date CreationDate;
    
    public String CustomId;

    public String Subject;

    public String Email;

    public Date SendingDate;
    
    public Integer SpamScoreAssassin;
    
    /* ProcessedCount & DeliveredCount comes from another call in mailjet API.*/ 
    public Integer ProcessedCount;
    
    public Integer DeliveredCount;

    /* That's why we do not initialize them directly but we use setters and getters*/
    public Integer getProcessedCount() {
        return ProcessedCount;
    }
    public void setProcessedCount(Integer processedCount) {
        ProcessedCount = processedCount;
    }
    
    public Integer getDeliveredCount() {
        return DeliveredCount;
    }
    public void setDeliveredCount(Integer deliveredCount) {
        DeliveredCount = deliveredCount;
    }

    /*Here we assign only values handle in the Campaign response from mailJet.*/
    public Campaign(ApiCampaign apiCampaign) {
        if (apiCampaign != null) {
            this.CreationDate = apiCampaign.CreatedAt;
            this.CustomId = apiCampaign.CustomValue;
            this.Subject = apiCampaign.Subject;
            this.Email = apiCampaign.FromEmail;
            this.SendingDate = apiCampaign.SendStartAt;
            this.SpamScoreAssassin = apiCampaign.SpamassScore;
        }
    }
}
