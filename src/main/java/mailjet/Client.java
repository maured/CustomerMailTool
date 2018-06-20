package mailjet;

import mailjet.api.ApiClient;

import java.util.Date;

public class Client {

    private String Name;

    private String Id;

    public Client(ApiClient apiClient) {
        if (apiClient != null) {
            this.Name = apiClient.getName();
            this.Id = apiClient.getId();
        }
    }
}
