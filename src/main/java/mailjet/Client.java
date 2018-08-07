package mailjet;

import mailjet.api.ApiClient;

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
