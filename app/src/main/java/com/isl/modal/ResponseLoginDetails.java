package com.isl.modal;
/* Modified By : Dhakan Lal Sharma
Modified On : 17-Nov-2015
Version     : 0.1
Purpose     : login time optimization 

Modified By : Dhakan Lal Sharma
Version     : 0.2
CR          : iMaintan 1.8.1

Modified By : Dhakan Lal Sharma
Version     : 0.3
Modified On : 09-Sep-2016
CR          : iETS 2.8.2.11
Modified By : Avishek Singh
 Modified On : 27-Aug-2020
 Version     : 1.0*/

public class ResponseLoginDetails {
	String uId;
	String name;
	String lastLogin;
	//String rspFlag;
	//String msg;
	String success;
	String message;
	String roleId;
	String cnId;
	String rId;
	String hId;
	String crId;
	String zId;
	String clId;
	String omId;
	String dataTS;
	String userCategory;
	String userSubCategory;

	String msgKey;
	int srvcTime;
	int pmMinImage;
	int ffMinImage;
	int pmMaxImage;
	int ffMaxImage;
	String passResetDate;
	String pmImageMessage;
	String ffImageMessage;
	int ttMinimumImage;
	int ttMaximumImage;
	String ttImageMsg;
	String distanceRange;
	String pmconfiguration;
	String ttconfiguration;
	String ffconfiguration;
	String userTracking; 
	
	public String getUserTracking() {
		return userTracking;
	}
	public void setUserTracking(String userTracking) {
		this.userTracking = userTracking;
	}
	public String getFfconfiguration() {
		return ffconfiguration;
	}
	public void setFfconfiguration(String ffconfiguration) {
		this.ffconfiguration = ffconfiguration;
	}
	public String getTtconfiguration() {
		return ttconfiguration;
	}
	public void setTtconfiguration(String ttconfiguration) {
		this.ttconfiguration = ttconfiguration;
	}
	
	public String getPMConfiguration() {
		return pmconfiguration;
	}
	public void setPMConfiguration(String pmconfiguration) {
		this.pmconfiguration=pmconfiguration;
	}
	public String getDistanceRange() {
		return distanceRange;
	}
	public void setDistanceRange(String distanceRange) {
		this.distanceRange = distanceRange;
	}
	public int getTtMinimumImage() {
		return ttMinimumImage;
	}
	public void setTtMinimumImage(int ttMinimumImage) {
		this.ttMinimumImage = ttMinimumImage;
	}
	public int getTtMaximumImage() {
		return ttMaximumImage;
	}
	public void setTtMaximumImage(int ttMaximumImage) {
		this.ttMaximumImage = ttMaximumImage;
	}
	public String getTtImageMsg() {
		return ttImageMsg;
	}
	public void setTtImageMsg(String ttImageMsg) {
		this.ttImageMsg = ttImageMsg;
	}
	public String getPmImageMessage() {
		return pmImageMessage;
	}
	public void setPmImageMessage(String pmImageMessage) {
		this.pmImageMessage = pmImageMessage;
	}
	public String getFfImageMessage() {
		return ffImageMessage;
	}
	public void setFfImageMessage(String ffImageMessage) {
		this.ffImageMessage = ffImageMessage;
	}
	public String getPassResetDate() {
		return passResetDate;
	}
	public void setPassResetDate(String passResetDate) {
		this.passResetDate = passResetDate;
	}
	public int getSrvcTime() {
		return srvcTime;
	}
	public void setSrvcTime(int srvcTime) {
		this.srvcTime = srvcTime;
	}
	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	/*public String getRspFlag() {
		return rspFlag;
	}
	public void setRspFlag(String rspFlag) {
		this.rspFlag = rspFlag;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}*/
	
	
	public String getRoleId() {
		return roleId;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	
	public String getDataTS() {
		return dataTS;
	}
	public void setDataTS(String dataTS) {
		this.dataTS = dataTS;
	}

	public String getUserCategory() {    //1.0
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getUserSubCategory() {
		return userSubCategory;
	}

	public void setUserSubCategory(String userSubCategory) {
		this.userSubCategory = userSubCategory;
	}

	public String getCnId() {
		return cnId;
	}
	public void setCnId(String cnId) {
		this.cnId = cnId;
	}
	public String getrId() {
		return rId;
	}
	public void setrId(String rId) {
		this.rId = rId;
	}
	public String gethId() {
		return hId;
	}
	public void sethId(String hId) {
		this.hId = hId;
	}
	public String getCrId() {
		return crId;
	}
	public void setCrId(String crId) {
		this.crId = crId;
	}
	public String getzId() {
		return zId;
	}
	public void setzId(String zId) {
		this.zId = zId;
	}
	public String getClId() {
		return clId;
	}
	public void setClId(String clId) {
		this.clId = clId;
	}
	public String getOmId() {
		return omId;
	}
	public void setOmId(String omId) {
		this.omId = omId;
	}
	public String getMsgKey() {
		return msgKey;
	}
	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}
	public int getPmMinImage() {
		return pmMinImage;
	}
	public void setPmMinImage(int pmMinImage) { 
		this.pmMinImage = pmMinImage;
	}
	public int getFfMinImage() {  //0.3
		return ffMinImage;
	}
	public void setFfMinImage(int ffMinImage) {
		this.ffMinImage = ffMinImage;
	}
	public int getPmMaxImage() {
		return pmMaxImage;
	}
	public void setPmMaxImage(int pmMaxImage) {
		this.pmMaxImage = pmMaxImage;
	}
	public int getFfMaxImage() {  //0.3
		return ffMaxImage;
	}
	public void setFfMaxImage(int ffMaxImage) { 
		this.ffMaxImage = ffMaxImage;
	}
}
