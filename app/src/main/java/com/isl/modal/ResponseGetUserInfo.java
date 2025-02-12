package com.isl.modal;

public class ResponseGetUserInfo {

	String ID;
	String LoginID;
	String Lat;
	String Longt;
	int BatteryStatus;
	String NetworkCheck;
	int Signal;
	String AutoTime;
	String TimeStamp;
	String imei;
	String gps;
	String mock;
	String CheckInStatus;

	public String getCheckInStatus() {
		return CheckInStatus;
	}

	public void setCheckInStatus(String checkInStatus) {
		CheckInStatus = checkInStatus;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getLoginID() {
		return LoginID;
	}

	public void setLoginID(String loginID) {
		LoginID = loginID;
	}

	public String getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		TimeStamp = timeStamp;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getLat() {
		return Lat;
	}

	public void setLat(String lat) {
		Lat = lat;
	}

	public String getLongt() {
		return Longt;
	}

	public void setLongt(String longt) {
		Longt = longt;
	}

	public int getBatteryStatus() {
		return BatteryStatus;
	}

	public void setBatteryStatus(int batteryStatus) {
		BatteryStatus = batteryStatus;
	}

	public String getNetworkCheck() {
		return NetworkCheck;
	}

	public void setNetworkCheck(String networkCheck) {
		NetworkCheck = networkCheck;
	}

	public int getSignal() {
		return Signal;
	}

	public void setSignal(int signal) {
		Signal = signal;
	}

	public String getAutoTime() {
		return AutoTime;
	}

	public void setAutoTime(String autoTime) {
		AutoTime = autoTime;
	}

	public String getGps() {
		return gps;
	}

	public void setGps(String gps) {
		this.gps = gps;
	}

	public String getMock() {
		return mock;
	}

	public void setMock(String mock) {
		this.mock = mock;
	}

}
