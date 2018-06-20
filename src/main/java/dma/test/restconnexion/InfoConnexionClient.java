package dma.test.restconnexion;

public class InfoConnexionClient{
	
	private String pubKey;
	private String privKey;
	
	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	public String getPrivKey() {
		return privKey;
	}

	public void setPrivKey(String privKey) {
		this.privKey = privKey;
	}
	
	public InfoConnexionClient(){
		
	}
	public InfoConnexionClient(String pubKey, String privKey){
		this.pubKey = pubKey;
		this.privKey = privKey;
	}
}