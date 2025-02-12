package com.isl.modal;

/*Modified by :  Dhakan lal
Modified On :  21-May-2016.
Purpose     :  CR# 1.9.1 (iMaintain)
Version     :  0.1

 Modified By : Avishek Singh
 Modified On : 02-mar-2021
 Version     : 1.2
 Purpose     : iMaintain cr# 821

 * Modified By : Dhakan Lal
 * Modified On : 02-Feb-2024
 * Version     : 1.3
 * Purpose     : DM-1178
*/

public class BeansTicketDetails extends BeansErrorResponse {
	String tktId;
	String sid;
	String sName;
	String status;
	String osid;
	String sType;
	String circle;
	String zone;
	String eqp;
	String eqpId;
	String severity;
	String severityId;
	String almShDesc;
	String almDesc;
	String aDetail;
	String logDt;
	String logBy;
	String tType;
	String tTypeId;
	String pDesc;
	String asgnTo;
	String tStatus;
	String eta;
	String etaTime;
	String etr;
	String etrTime;
	String rca;
	String rCat;
	String pStDt;
	String pStTime;
	String pEDt;
	String pETime;
	String rTktId;
	String tRCA;
	String cnts;
	String rCatId;
	String grpName;
	String oLst;
	String sites;
	String oName;
	String aId;
	// start 0.1
	String rSCat;
	String bbDisSDt;
	String bbDSTime;
	String bbBkp;
	String rSCatName;
	String oExemLst;
	String oExemName;
	String enableCloseTKT;
	String asgnToUid;
	String etsid;
	String offLineFlag;
	String trvlDistnc;
	String noOfTech;
	String wrkgNights;

	String dgReading;
	String gridReading;
	String fuelLevel;
	String actionTaken;
    String tt_flag;
	String tktTretmnt;
	String tt_rev_flag;
	String hubSiteId;

	String firstLevel;
	String secondLevel;
	String rejCat;
	String rejRmk;
	String OPERATOR_LIST_NAME;
	String grpId;
	String userCat;
	String userSubcat;


	String STATUS_REASON;
	String STATUS_REASON_NAME;
	String APPROVAL_STATUS;
	String APPROVAL_STATUS_NAME;
	String PR_NO;
	String RTTS_PARENT_TICKET;
	String ABSTRACT;
	String SERVICE_IMPACTED;
	String SERVICE_IMPACT_START;



	String SERVICE_IMPACT_START_TIME;
	String SERVICE_AFFECTED;
	String INITIAL_ANALYSIS;
	String X_REFRENCES;
	String TWOG_NODE;
	String THREEG_NODE;
	String LTE;
	String	IP_SITE;
	String ORGINATOR_ID;
	String ORGINATOR_GROUP;
	String LAST_REFRED_GRP;
	String REFER_TO_GRP;
	String RTTS_OPER_SITE_ID;
	String PREV_ASSIGNEE;
	String PREV_GROUP;
	String SOLUTION_DETAILS;
	String RTTS_PARENT_CHILD_FLAG;
	String FILE_SIZE;
	String FAULT_AREA_NAME;
	String FAULT_AREA_DETAIL_NAME;
	String RESULATION_METHOD_NAME;
	String FAULT_AREA;
	String FAULT_AREA_DETAIL;
	String RESULATION_METHOD;
	String RTTS_CHIELD_TICKET;
	String RTTS_FLAG;
	//1.3
	String isUserSatisfied;
	String loggedBy;
	String assetId;
	String PRIORITY_ID;
	String PRIORITY_NAME;
	String LATITUDE;
	String LONGITUDE;
	String digital_dis;
	String delay_rsnid;
	String delay_rsn;
	String toc_fo_user;
	String ALARM_IN_TIM;

	String consumedpwr;
	String gridpwr;
	String ebvltg;
	String genpwr;
	String dgruntm;
	String fuellvl;
	String batsoc;
	String batvltg;
	String batterychamp;
	String batterydischamp;

	String actstdt;
	String actsttime;
	String actenddt;
	String actendtime;
	String accessid;
	String accreqsts;
	String energySource;


	public String getEnergySource() {
		return energySource;
	}

	public void setEnergySource(String energySource) {
		this.energySource = energySource;
	}

	public String getActstdt() {
		return actstdt;
	}

	public void setActstdt(String actstdt) {
		this.actstdt = actstdt;
	}

	public String getActsttime() {
		return actsttime;
	}

	public void setActsttime(String actsttime) {
		this.actsttime = actsttime;
	}

	public String getActenddt() {
		return actenddt;
	}

	public void setActenddt(String actenddt) {
		this.actenddt = actenddt;
	}

	public String getActendtime() {
		return actendtime;
	}

	public void setActendtime(String actendtime) {
		this.actendtime = actendtime;
	}

	public String getAccessid() {
		return accessid;
	}

	public void setAccessid(String accessid) {
		this.accessid = accessid;
	}

	public String getAccreqsts() {
		return accreqsts;
	}

	public void setAccreqsts(String accreqsts) {
		this.accreqsts = accreqsts;
	}

	public String getConsumedpwr() {
		return consumedpwr;
	}

	public void setConsumedpwr(String consumedpwr) {
		this.consumedpwr = consumedpwr;
	}

	public String getGridpwr() {
		return gridpwr;
	}

	public void setGridpwr(String gridpwr) {
		this.gridpwr = gridpwr;
	}

	public String getEbvltg() {
		return ebvltg;
	}

	public void setEbvltg(String ebvltg) {
		this.ebvltg = ebvltg;
	}

	public String getGenpwr() {
		return genpwr;
	}

	public void setGenpwr(String genpwr) {
		this.genpwr = genpwr;
	}

	public String getDgruntm() {
		return dgruntm;
	}

	public void setDgruntm(String dgruntm) {
		this.dgruntm = dgruntm;
	}

	public String getFuellvl() {
		return fuellvl;
	}

	public void setFuellvl(String fuellvl) {
		this.fuellvl = fuellvl;
	}

	public String getBatsoc() {
		return batsoc;
	}

	public void setBatsoc(String batsoc) {
		this.batsoc = batsoc;
	}

	public String getBatvltg() {
		return batvltg;
	}

	public void setBatvltg(String batvltg) {
		this.batvltg = batvltg;
	}

	public String getBatterychamp() {
		return batterychamp;
	}

	public void setBatterychamp(String batterychamp) {
		this.batterychamp = batterychamp;
	}

	public String getBatterydischamp() {
		return batterydischamp;
	}

	public void setBatterydischamp(String batterydischamp) {
		this.batterydischamp = batterydischamp;
	}

	public String getALARM_IN_TIM() {
		return ALARM_IN_TIM;
	}

	public void setALARM_IN_TIM(String ALARM_IN_TIM) {
		this.ALARM_IN_TIM = ALARM_IN_TIM;
	}

	public String getToc_fo_user() {
		return toc_fo_user;
	}

	public void setToc_fo_user(String toc_fo_user) {
		this.toc_fo_user = toc_fo_user;
	}



	public String getDelay_rsn() {
		return delay_rsn;
	}

	public void setDelay_rsn(String delay_rsn) {
		this.delay_rsn = delay_rsn;
	}



	public String getDelay_rsnid() {
		return delay_rsnid;
	}

	public void setDelay_rsnid(String delay_rsnid) {
		this.delay_rsnid = delay_rsnid;
	}

	public String getDigital_dis() {
		return digital_dis;
	}

	public void setDigital_dis(String digital_dis) {
		this.digital_dis = digital_dis;
	}



	public String getLATITUDE() {
		return LATITUDE;
	}

	public void setLATITUDE(String LATITUDE) {
		this.LATITUDE = LATITUDE;
	}

	public String getLONGITUDE() {
		return LONGITUDE;
	}

	public void setLONGITUDE(String LONGITUDE) {
		this.LONGITUDE = LONGITUDE;
	}


	public String getPRIORITY_ID() {
		return PRIORITY_ID;
	}

	public void setPRIORITY_ID(String PRIORITY_ID) {
		this.PRIORITY_ID = PRIORITY_ID;
	}

	public String getPRIORITY_NAME() {
		return PRIORITY_NAME;
	}

	public void setPRIORITY_NAME(String PRIORITY_NAME) {
		this.PRIORITY_NAME = PRIORITY_NAME;
	}


	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getLoggedBy() {
		return loggedBy;
	}

	public void setLoggedBy(String loggedBy) {
		this.loggedBy = loggedBy;
	}

	public String getIsUserSatisfied() {
		return isUserSatisfied;
	}

	public void setIsUserSatisfied(String isUserSatisfied) {
		this.isUserSatisfied = isUserSatisfied;
	}


	public String getSERVICE_IMPACT_START_DATE() {
		return SERVICE_IMPACT_START_TIME;
	}

	public void setSERVICE_IMPACT_START_DATE(String SERVICE_IMPACT_START_TIME) {
		this.SERVICE_IMPACT_START_TIME = SERVICE_IMPACT_START_TIME;
	}

	public String getSTATUS_REASON() {
		return STATUS_REASON;
	}

	public void setSTATUS_REASON(String STATUS_REASON) {
		this.STATUS_REASON = STATUS_REASON;
	}

	public String getSTATUS_REASON_NAME() {
		return STATUS_REASON_NAME;
	}

	public void setSTATUS_REASON_NAME(String STATUS_REASON_NAME) {
		this.STATUS_REASON_NAME = STATUS_REASON_NAME;
	}

	public String getAPPROVAL_STATUS() {
		return APPROVAL_STATUS;
	}

	public void setAPPROVAL_STATUS(String APPROVAL_STATUS) {
		this.APPROVAL_STATUS = APPROVAL_STATUS;
	}

	public String getAPPROVAL_STATUS_NAME() {
		return APPROVAL_STATUS_NAME;
	}

	public void setAPPROVAL_STATUS_NAME(String APPROVAL_STATUS_NAME) {
		this.APPROVAL_STATUS_NAME = APPROVAL_STATUS_NAME;
	}

	public String getPR_NO() {
		return PR_NO;
	}

	public void setPR_NO(String PR_NO) {
		this.PR_NO = PR_NO;
	}

	public String getRTTS_PARENT_TICKET() {
		return RTTS_PARENT_TICKET;
	}

	public void setRTTS_PARENT_TICKET(String RTTS_PARENT_TICKET) {
		this.RTTS_PARENT_TICKET = RTTS_PARENT_TICKET;
	}

	public String getABSTRACT() {
		return ABSTRACT;
	}

	public void setABSTRACT(String ABSTRACT) {
		this.ABSTRACT = ABSTRACT;
	}

	public String getSERVICE_IMPACTED() {
		return SERVICE_IMPACTED;
	}

	public void setSERVICE_IMPACTED(String SERVICE_IMPACTED) {
		this.SERVICE_IMPACTED = SERVICE_IMPACTED;
	}

	public String getSERVICE_IMPACT_START() {
		return SERVICE_IMPACT_START;
	}

	public void setSERVICE_IMPACT_START(String SERVICE_IMPACT_START) {
		this.SERVICE_IMPACT_START = SERVICE_IMPACT_START;
	}

	public String getSERVICE_AFFECTED() {
		return SERVICE_AFFECTED;
	}

	public void setSERVICE_AFFECTED(String SERVICE_AFFECTED) {
		this.SERVICE_AFFECTED = SERVICE_AFFECTED;
	}

	public String getINITIAL_ANALYSIS() {
		return INITIAL_ANALYSIS;
	}

	public void setINITIAL_ANALYSIS(String INITIAL_ANALYSIS) {
		this.INITIAL_ANALYSIS = INITIAL_ANALYSIS;
	}

	public String getX_REFRENCES() {
		return X_REFRENCES;
	}

	public void setX_REFRENCES(String x_REFRENCES) {
		X_REFRENCES = x_REFRENCES;
	}

	public String getTWOG_NODE() {
		return TWOG_NODE;
	}

	public void setTWOG_NODE(String TWOG_NODE) {
		this.TWOG_NODE = TWOG_NODE;
	}

	public String getTHREEG_NODE() {
		return THREEG_NODE;
	}

	public void setTHREEG_NODE(String THREEG_NODE) {
		this.THREEG_NODE = THREEG_NODE;
	}

	public String getLTE() {
		return LTE;
	}

	public void setLTE(String LTE) {
		this.LTE = LTE;
	}

	public String getIP_SITE() {
		return IP_SITE;
	}

	public void setIP_SITE(String IP_SITE) {
		this.IP_SITE = IP_SITE;
	}

	public String getORGINATOR_ID() {
		return ORGINATOR_ID;
	}

	public void setORGINATOR_ID(String ORGINATOR_ID) {
		this.ORGINATOR_ID = ORGINATOR_ID;
	}

	public String getORGINATOR_GROUP() {
		return ORGINATOR_GROUP;
	}

	public void setORGINATOR_GROUP(String ORGINATOR_GROUP) {
		this.ORGINATOR_GROUP = ORGINATOR_GROUP;
	}

	public String getLAST_REFRED_GRP() {
		return LAST_REFRED_GRP;
	}

	public void setLAST_REFRED_GRP(String LAST_REFRED_GRP) {
		this.LAST_REFRED_GRP = LAST_REFRED_GRP;
	}

	public String getREFER_TO_GRP() {
		return REFER_TO_GRP;
	}

	public void setREFER_TO_GRP(String REFER_TO_GRP) {
		this.REFER_TO_GRP = REFER_TO_GRP;
	}

	public String getRTTS_OPER_SITE_ID() {
		return RTTS_OPER_SITE_ID;
	}

	public void setRTTS_OPER_SITE_ID(String RTTS_OPER_SITE_ID) {
		this.RTTS_OPER_SITE_ID = RTTS_OPER_SITE_ID;
	}

	public String getPREV_ASSIGNEE() {
		return PREV_ASSIGNEE;
	}

	public void setPREV_ASSIGNEE(String PREV_ASSIGNEE) {
		this.PREV_ASSIGNEE = PREV_ASSIGNEE;
	}

	public String getPREV_GROUP() {
		return PREV_GROUP;
	}

	public void setPREV_GROUP(String PREV_GROUP) {
		this.PREV_GROUP = PREV_GROUP;
	}

	public String getSOLUTION_DETAILS() {
		return SOLUTION_DETAILS;
	}

	public void setSOLUTION_DETAILS(String SOLUTION_DETAILS) {
		this.SOLUTION_DETAILS = SOLUTION_DETAILS;
	}

	public String getRTTS_PARENT_CHILD_FLAG() {
		return RTTS_PARENT_CHILD_FLAG;
	}

	public void setRTTS_PARENT_CHILD_FLAG(String RTTS_PARENT_CHILD_FLAG) {
		this.RTTS_PARENT_CHILD_FLAG = RTTS_PARENT_CHILD_FLAG;
	}

	public String getFILE_SIZE() {
		return FILE_SIZE;
	}

	public void setFILE_SIZE(String FILE_SIZE) {
		this.FILE_SIZE = FILE_SIZE;
	}

	public String getFAULT_AREA_NAME() {
		return FAULT_AREA_NAME;
	}

	public void setFAULT_AREA_NAME(String FAULT_AREA_NAME) {
		this.FAULT_AREA_NAME = FAULT_AREA_NAME;
	}

	public String getFAULT_AREA_DETAIL_NAME() {
		return FAULT_AREA_DETAIL_NAME;
	}

	public void setFAULT_AREA_DETAIL_NAME(String FAULT_AREA_DETAIL_NAME) {
		this.FAULT_AREA_DETAIL_NAME = FAULT_AREA_DETAIL_NAME;
	}

	public String getRESULATION_METHOD_NAME() {
		return RESULATION_METHOD_NAME;
	}

	public void setRESULATION_METHOD_NAME(String RESULATION_METHOD_NAME) {
		this.RESULATION_METHOD_NAME = RESULATION_METHOD_NAME;
	}

	public String getFAULT_AREA() {
		return FAULT_AREA;
	}

	public void setFAULT_AREA(String FAULT_AREA) {
		this.FAULT_AREA = FAULT_AREA;
	}

	public String getFAULT_AREA_DETAIL() {
		return FAULT_AREA_DETAIL;
	}

	public void setFAULT_AREA_DETAIL(String FAULT_AREA_DETAIL) {
		this.FAULT_AREA_DETAIL = FAULT_AREA_DETAIL;
	}

	public String getRESULATION_METHOD() {
		return RESULATION_METHOD;
	}

	public void setRESULATION_METHOD(String RESULATION_METHOD) {
		this.RESULATION_METHOD = RESULATION_METHOD;
	}

	public String getRTTS_CHIELD_TICKET() {
		return RTTS_CHIELD_TICKET;
	}

	public void setRTTS_CHIELD_TICKET(String RTTS_CHIELD_TICKET) {
		this.RTTS_CHIELD_TICKET = RTTS_CHIELD_TICKET;
	}

	public String getRTTS_FLAG() {
		return RTTS_FLAG;
	}

	public void setRTTS_FLAG(String RTTS_FLAG) {
		this.RTTS_FLAG = RTTS_FLAG;
	}

	public String getUserCat() {
		return userCat;
	}

	public void setUserCat(String userCat) {
		this.userCat = userCat;
	}

	public String getUserSubcat() {
		return userSubcat;
	}

	public void setUserSubcat(String userSubcat) {
		this.userSubcat = userSubcat;
	}

	public String getGrpId() {
		return grpId;
	}

	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public String getOPERATOR_LIST_NAME() {
		return OPERATOR_LIST_NAME;
	}

	public void setOPERATOR_LIST_NAME(String OPERATOR_LIST_NAME) {
		this.OPERATOR_LIST_NAME = OPERATOR_LIST_NAME;
	}

	public String getRejCat() {
		return rejCat;
	}

	public void setRejCat(String rejCat) {
		this.rejCat = rejCat;
	}

	public String getRejRmk() {
		return rejRmk;
	}

	public void setRejRmk(String rejRmk) {
		this.rejRmk = rejRmk;
	}

	public String getFirstLevel() {
		return firstLevel;
	}

	public void setFirstLevel(String firstLevel) {
		this.firstLevel = firstLevel;
	}

	public String getSecondLevel() {
		return secondLevel;
	}

	public void setSecondLevel(String secondLevel) {
		this.secondLevel = secondLevel;
	}

	public String getDgReading() {
		return dgReading;
	}

	public void setDgReading(String dgReading) {
		this.dgReading = dgReading;
	}

	public String getGridReading() {
		return gridReading;
	}

	public void setGridReading(String gridReading) {
		this.gridReading = gridReading;
	}

	public String getFuelLevel() {
		return fuelLevel;
	}

	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getTktTretmnt() {
		return tktTretmnt;
	}

	public void setTktTretmnt(String tktTretmnt) {
		this.tktTretmnt = tktTretmnt;
	}

	public String getHubSiteId() {
		return hubSiteId;
	}   //1.2

	public void setHubSiteId(String hubSiteId) {
		this.hubSiteId = hubSiteId;
	}  //1.2

	public String getTt_flag() {
		return tt_flag;
	}

	public void setTt_flag(String tt_flag) {
		this.tt_flag = tt_flag;
	}

	public String getTt_rev_flag() {
		return tt_rev_flag;
	}

	public void setTt_rev_flag(String tt_rev_flag) {
		this.tt_rev_flag = tt_rev_flag;
	}

	public String getTrvlDistnc() {
		return trvlDistnc;
	}

	public void setTrvlDistnc(String trvlDistnc) {
		this.trvlDistnc = trvlDistnc;
	}

	public String getNoOfTech() {
		return noOfTech;
	}

	public void setNoOfTech(String noOfTech) {
		this.noOfTech = noOfTech;
	}

	public String getWrkgNights() {
		return wrkgNights;
	}

	public void setWrkgNights(String wrkgNights) {
		this.wrkgNights = wrkgNights;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getOffLineFlag() {
		return offLineFlag;
	}

	public void setOffLineFlag(String offLineFlag) {
		this.offLineFlag = offLineFlag;
	}

	public String getEtsid() {
		return etsid;
	}

	public void setEtsid(String etsid) {
		this.etsid = etsid;
	}

	public String getAsgnToUid() {
		return asgnToUid;
	}

	public void setAsgnToUid(String asgnToUid) {
		this.asgnToUid = asgnToUid;
	}

	public String getoExemLst() {
		return oExemLst;
	}

	public void setoExemLst(String oExemLst) {
		this.oExemLst = oExemLst;
	}

	public String getoExemName() {
		return oExemName;
	}

	public void setoExemName(String oExemName) {
		this.oExemName = oExemName;
	}

	public String getRCA_SUB_CAT() {
		return rSCat;
	}

	public void setRCA_SUB_CAT(String rCA_SUB_CAT) {
		rSCat = rCA_SUB_CAT;
	}

	public String getRCA_SUB_CAT_NAME() {
		return rSCatName;
	}

	public void setRCA_SUB_CAT_NAME(String rCA_SUB_CAT_NAME) {
		rSCatName = rCA_SUB_CAT_NAME;
	}

	public String getBATTERY_BACKUP_TIME() {
		return bbBkp;
	}

	public void setBATTERY_BACKUP_TIME(String bATTERY_BACKUP_TIME) {
		bbBkp = bATTERY_BACKUP_TIME;
	}

	public String getBATTERY_DIS_START_DATE() {
		return bbDisSDt;
	}

	public void setBATTERY_DIS_START_DATE(String bATTERY_DIS_START_DATE) {
		bbDisSDt = bATTERY_DIS_START_DATE;
	}

	public String getBATTERY_DIS_START_TIME() {
		return bbDSTime;
	}

	public void setBATTERY_DIS_START_TIME(String bATTERY_DIS_START_TIME) {
		bbDSTime = bATTERY_DIS_START_TIME;
	}
	// endt 0.1

	public String getALARM_TXN_ID() {
		return aId;
	}

	public void setALARM_TXN_ID(String aLARM_TXN_ID) {
		aId = aLARM_TXN_ID;
	}

	public String getEQUIPMENT_ID() {
		return eqpId;
	}

	public void setEQUIPMENT_ID(String eQUIPMENT_ID) {
		eqpId = eQUIPMENT_ID;
	}

	public String getALARM_TYPE_ID() {
		return severityId;
	}

	public void setALARM_TYPE_ID(String aLARM_TYPE_ID) {
		severityId = aLARM_TYPE_ID;
	}

	public String getALARM_DESC_ID() {
		return almDesc;
	}

	public void setALARM_DESC_ID(String aLARM_DESC_ID) {
		almDesc = aLARM_DESC_ID;
	}

	public String getTICKET_TYPE_ID() {
		return tTypeId;
	}

	public void setTICKET_TYPE_ID(String tICKET_TYPE_ID) {
		tTypeId = tICKET_TYPE_ID;
	}

	public String getSITE_STATUS() {
		return status;
	}

	public void setSITE_STATUS(String sITE_STATUS) {
		status = sITE_STATUS;
	}

	public String getOPERATOR_NAME() {
		return oName;
	}

	public void setOPERATOR_NAME(String oPERATOR_NAME) {
		oName = oPERATOR_NAME;
	}

	public String getOPERATOR_LIST() {
		return oLst;
	}

	public void setOPERATOR_LIST(String oPERATOR_LIST) {
		oLst = oPERATOR_LIST;
	}

	public String getEFFECTED_SITES() {
		return sites;
	}

	public void setEFFECTED_SITES(String eFFECTED_SITES) {
		sites = eFFECTED_SITES;
	}

	public String getGROUP_NAME() {
		return grpName;
	}

	public void setGROUP_NAME(String gROUP_NAME) {
		grpName = gROUP_NAME;
	}

	public String getCONTACTS() {
		return cnts;
	}

	public void setCONTACTS(String cONTACTS) {
		cnts = cONTACTS;
	}

	public String getTICKET_RCA() {
		return tRCA;
	}

	public void setTICKET_RCA(String tICKET_RCA) {
		tRCA = tICKET_RCA;
	}

	public String getPROBLEM_START_DATE() {
		return pStDt;
	}

	public void setPROBLEM_START_DATE(String pROBLEM_START_DATE) {
		pStDt = pROBLEM_START_DATE;
	}

	public String getPROBLEM_START_TIME() {
		return pStTime;
	}

	public void setPROBLEM_START_TIME(String pROBLEM_START_TIME) {
		pStTime = pROBLEM_START_TIME;
	}

	public String getPROBLEM_END_DATE() {
		return pEDt;
	}

	public void setPROBLEM_END_DATE(String pROBLEM_END_DATE) {
		pEDt = pROBLEM_END_DATE;
	}

	public String getPROBLEM_END_TIME() {
		return pETime;
	}

	public void setPROBLEM_END_TIME(String pROBLEM_END_TIME) {
		pETime = pROBLEM_END_TIME;
	}

	public String getREF_TICKET_ID() {
		return rTktId;
	}

	public void setREF_TICKET_ID(String rEF_TICKET_ID) {
		rTktId = rEF_TICKET_ID;
	}

	public String getETA() {
		return eta;
	}

	public void setETA(String eTA) {
		eta = eTA;
	}

	public String getETA_TIME() {
		return etaTime;
	}

	public void setETA_TIME(String eTA_TIME) {
		etaTime = eTA_TIME;
	}

	public String getETR() {
		return etr;
	}

	public void setETR(String eTR) {
		etr = eTR;
	}

	public String getETR_TIME() {
		return etrTime;
	}

	public void setETR_TIME(String eTR_TIME) {
		etrTime = eTR_TIME;
	}

	public String getRCA() {
		return rca;
	}

	public void setRCA(String rCA) {
		rca = rCA;
	}

	public String getRCA_CATEGORY_NAME() {
		return rCat;
	}

	public void setRCA_CATEGORY_NAME(String rCA_CATEGORY_NAME) {
		rCat = rCA_CATEGORY_NAME;
	}

	public String getRCA_CATEGORY() {
		return rCatId;
	}

	public void setRCA_CATEGORY(String rCA_CATEGORY) {
		rCatId = rCA_CATEGORY;
	}

	public String getTICKET_ID() {
		return tktId;
	}

	public void setTICKET_ID(String tICKET_ID) {
		tktId = tICKET_ID;
	}

	public String getSITE_ID() {
		return sid;
	}

	public void setSITE_ID(String sITE_ID) {
		sid = sITE_ID;
	}

	public String getOPERATOR_SITE_ID() {
		return osid;
	}

	public void setOPERATOR_SITE_ID(String oPERATOR_SITE_ID) {
		osid = oPERATOR_SITE_ID;
	}

	public String getSITE_TYPE_ID() {
		return sType;
	}

	public void setSITE_TYPE_ID(String sITE_TYPE_ID) {
		sType = sITE_TYPE_ID;
	}

	public String getREGION() {
		return circle;
	}

	public void setREGION(String rEGION) {
		circle = rEGION;
	}

	public String getSUB_REGION() {
		return zone;
	}

	public void setSUB_REGION(String sUB_REGION) {
		zone = sUB_REGION;
	}

	public String getEQUIPMENT() {
		return eqp;
	}

	public void setEQUIPMENT(String eQUIPMENT) {
		eqp = eQUIPMENT;
	}

	public String getALARM_TYPE() {
		return severity;
	}

	public void setALARM_TYPE(String aLARM_TYPE) {
		severity = aLARM_TYPE;
	}

	public String getALARM_SHORT_DISCRIPTION() {
		return almShDesc;
	}

	public void setALARM_SHORT_DISCRIPTION(String aLARM_SHORT_DISCRIPTION) {
		almShDesc = aLARM_SHORT_DISCRIPTION;
	}

	public String getALARM_DETAIL() {
		return aDetail;
	}

	public void setALARM_DETAIL(String aLARM_DETAIL) {
		aDetail = aLARM_DETAIL;
	}

	public String getTICKET_LOG_TIME() {
		return logDt;
	}

	public void setTICKET_LOG_TIME(String tICKET_LOG_TIME) {
		logDt = tICKET_LOG_TIME;
	}

	public String getTICKET_LOGGED_BY() {
		return logBy;
	}

	public void setTICKET_LOGGED_BY(String tICKET_LOGGED_BY) {
		logBy = tICKET_LOGGED_BY;
	}

	public String getTICKET_TYPE() {
		return tType;
	}

	public void setTICKET_TYPE(String tICKET_TYPE) {
		tType = tICKET_TYPE;
	}

	public String getPROBLEM_DESC() {
		return pDesc;
	}

	public void setPROBLEM_DESC(String pROBLEM_DESC) {
		pDesc = pROBLEM_DESC;
	}

	public String getASSIGNED_TO() {
		return asgnTo;
	}

	public void setASSIGNED_TO(String aSSIGNED_TO) {
		asgnTo = aSSIGNED_TO;
	}

	public String getTICKET_STATUS() {
		return tStatus;
	}

	public void setTICKET_STATUS(String tICKET_STATUS) {
		tStatus = tICKET_STATUS;
	}

	public String getEnableCloseTKT() {
		return enableCloseTKT;
	}

	public void setEnableCloseTKT(String enableCloseTKT) {
		this.enableCloseTKT = enableCloseTKT;
	}






}
