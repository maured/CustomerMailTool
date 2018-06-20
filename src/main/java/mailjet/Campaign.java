package mailjet;

import mailjet.api.ApiCampaign;

import java.util.Date;

public class Campaign {
    public String CustomId;

    public String Subject;

    public String Email;

    public Date Date;

    public Integer ProcessedCount;

    public Campaign(ApiCampaign apiCampaign) {
        if (apiCampaign != null) {
            this.CustomId = apiCampaign.CustomValue;
            this.Subject = apiCampaign.Subject;
            this.Email = apiCampaign.FromEmail;
            this.Date = apiCampaign.SendStartAt;
        }
    }
}
