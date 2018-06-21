package mailjet;

import mailjet.api.ApiCampaign;

import java.util.Date;

public class Campaign {
    public String CustomId;

    public String Subject;

    public String Email;

    public Date Date;
    
    public Integer SpamScoreAssassin;
    
    public Integer MonthProcessedCount;
    
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
            this.CustomId = apiCampaign.CustomValue;
            this.Subject = apiCampaign.Subject;
            this.Email = apiCampaign.FromEmail;
            this.Date = apiCampaign.SendStartAt;
            this.SpamScoreAssassin = apiCampaign.SpamassScore;
        }
    }
}
