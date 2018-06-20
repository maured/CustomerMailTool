package mailjet.api;

import java.util.Date;

public class ApiClient {
	
	private Boolean IsActive; //:true,
	private Date CreatedAt; //:"2016-05-09T11:44:16Z",
	private String Runlevel; //:"Normal",
	private String ACL; //""
	private Integer QuarantineValue; //:0
	private String Name; //:"BP2S"
	private String APIKey;//:"9743186126e0d3616148a0a46240aec7"
	private String SecretKey; //:"27c7cf02e143502978d634cf9be59889"
	private String UserID; //:393783,
	private Boolean IsMaster; //:false,
	private String ID; //:422434,
	private Integer Skipspamd; //:1,
	private String TrackHost; //:"t22p.mjt.lu"

	
	public ApiClient(Boolean isActive, Date createdAt, String runLevel, String acl, Integer quarantineValue, String name, String apiKey, String secretKey, String userID, Boolean isMaster, String id, Integer skipSpamd, String trackHost)
	{
		this.IsActive = isActive;
		this.CreatedAt = createdAt;
		this.Runlevel = runLevel;
		this.ACL = acl;
		this.QuarantineValue = quarantineValue;
		this. Name = name;
		this. APIKey = apiKey;
		this.SecretKey = secretKey;
		this.UserID = userID;
		this.IsMaster = isMaster;
		this.ID = id;
		this.Skipspamd = skipSpamd;
		this.TrackHost = trackHost;
	}
	
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