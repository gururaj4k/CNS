package com.cns.domains;




public class WifiAccessPoint {
	private String ssid;
	private String bssid;
	private String dbm;

	public String getDbm() {
		return dbm;
	}

	public void setDbm(String dbm) {
		this.dbm = dbm;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	private String strength;
	public WifiAccessPoint() {
	    // TODO Auto-generated constructor stub
    }

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String mac) {
		this.bssid = mac;
	}
	

}
