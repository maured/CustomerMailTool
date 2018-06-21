package mailjet.api;

import java.util.Date;

public class ApiClient {
	
	private Boolean IsActive;
	private Date CreatedAt;
	private String Runlevel;
	private String ACL;
	private Integer QuarantineValue;
	private String Name;
	private String APIKey;//:"9743186126e0d3616148a0a46240aec7"
	private String SecretKey; //:"27c7cf02e143502978d634cf9be59889"
	private String UserID;
	private Boolean IsMaster;
	private String ID;
	private Integer Skipspamd;
	private String TrackHost;
	
	public Boolean getActive() {
		return IsActive;
	}
	public void setActive(Boolean active) {
		IsActive = active;
	}

	public Date getCreatedAt() {
		return CreatedAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.CreatedAt = createdAt;
	}

	public String getRunLevel() {
		return Runlevel;
	}
	public void setRunLevel(String runLevel) {
		this.Runlevel = runLevel;
	}

	public String getAcl() {
		return ACL;
	}
	public void setAcl(String acl) {
		this.ACL = acl;
	}

	public Integer getQuarantineValue() {
		return QuarantineValue;
	}
	public void setQuarantineValue(Integer quarantineValue) {
		this.QuarantineValue = quarantineValue;
	}

	public String getName() {
		return Name;
	}
	public void setName(String name) {
		this.Name = name;
	}

	public String getAPIKey() {
		return APIKey;
	}
	public void setAPIKey(String apiKey) {
		this.APIKey = apiKey;
	}

	public String getSecretKey() {
		return SecretKey;
	}
	public void setSecretKey(String secretKey) {
		this.SecretKey = secretKey;
	}

	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		this.UserID = userID;
	}

	public Boolean getMaster() {
		return IsMaster;
	}
	public void setMaster(Boolean master) {
		IsMaster = master;
	}

	public String getId() {
		return ID;
	}
	public void setId(String id) {
		this.ID = id;
	}

	public Integer getSkipSpamd() {
		return Skipspamd;
	}
	public void setSkipSpamd(Integer skipSpamd) {
		this.Skipspamd = skipSpamd;
	}

	public String getTrackHost() {
		return TrackHost;
	} 
	public void setTrackHost(String trackHost) {
		this.TrackHost = trackHost;
	}
}