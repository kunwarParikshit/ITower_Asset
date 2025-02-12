package com.isl.dao.cache;

/*Modified By : Dhakan Lal Sharma
 Version     : 0.4
 Purpose     : CR# 2.8.2.11

 Modified By : Dhakan Lal Sharma
 Version     : 0.5
 Date        : 21-June-2017
 Purpose     : method for auto date and time functionality check or not.

Modified By : Avishek Singh
 Modified On : 27-Aug-2020
 Version     : 1.0
 */
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.isl.constant.AppConstants;
import com.isl.util.Utils;

public class AppPreferences {

	private static final String APP_SHARED_PREFS = "com.isl";
	private static SharedPreferences appSharedPrefs;
	private Editor prefsEditor;

	public AppPreferences(Context context) {
		this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}

	public String getFirstTimeRunApp() {
		return appSharedPrefs.getString( AppConstants.FIRST_TIME_RUN_APP, "");
	}

	public void setFirstTimeRunApp(String text) {
		prefsEditor.putString(AppConstants.FIRST_TIME_RUN_APP, text);
		prefsEditor.commit();
	}

	public String getFirstTimeHS() {
		return appSharedPrefs.getString( AppConstants.FIRST_TIME_HS, "");
	}

	public void setFirstTimeHS(String text) {
		prefsEditor.putString(AppConstants.FIRST_TIME_HS, text);
		prefsEditor.commit();
	}

	public String getCounrtyID() {
		return appSharedPrefs.getString(AppConstants.COUNTRY_ID_ALIAS, "");
	}

	public void setCounrtyID(String text) {
		prefsEditor.putString(AppConstants.COUNTRY_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getHubID() {
		return appSharedPrefs.getString(AppConstants.HUB_ID_ALIAS, "");
	}

	public void setHubID(String text) {
		prefsEditor.putString(AppConstants.HUB_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getRegionId() {
		return appSharedPrefs.getString(AppConstants.REGION_ID_ALIAS, "");
	}

	public void setRegionId(String text) {
		prefsEditor.putString(AppConstants.REGION_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getCircleID() {
		return appSharedPrefs.getString(AppConstants.CIRCLE_ID_ALIAS, "");
	}

	public void setCircleID(String text) {
		prefsEditor.putString(AppConstants.CIRCLE_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getZoneID() {
		return appSharedPrefs.getString(AppConstants.ZONE_ID_ALIAS, "");
	}

	public void setZoneID(String text) {
		prefsEditor.putString(AppConstants.ZONE_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getClusterID() {
		return appSharedPrefs.getString(AppConstants.CLUSTER_ID_ALIAS, "");
	}

	public void setClusterID(String text) {
		prefsEditor.putString(AppConstants.CLUSTER_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getPIOMEID() {
		return appSharedPrefs.getString(AppConstants.OME_ID_ALIAS, "");
	}

	public void setPIOMEID(String text) {
		prefsEditor.putString(AppConstants.OME_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getUserId() {
		return appSharedPrefs.getString(AppConstants.USER_ID_ALIAS, "");
	}

	public void saveUserId(String text) {
		prefsEditor.putString(AppConstants.USER_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getName() {
		return appSharedPrefs.getString(AppConstants.USER_NAME, "");
	}

	public void setName(String text) {
		prefsEditor.putString(AppConstants.USER_NAME, text);
		prefsEditor.commit();
	}

	public String getLastLogin() {
		return appSharedPrefs.getString(AppConstants.LAST_LOGIN_TIME, "");
	}

	public void setLastLogin(String text) {
		prefsEditor.putString(AppConstants.LAST_LOGIN_TIME, text);
		prefsEditor.commit();
	}

	public String getRoleId() {
		return appSharedPrefs.getString(AppConstants.USER_ROLE_ID, "");
	}

	public void setRoleId(String text) {
		prefsEditor.putString(AppConstants.USER_ROLE_ID, text);
		prefsEditor.commit();
	}

	public int getLoginState() {
		return appSharedPrefs.getInt(AppConstants.LOGIN_STATE, 0);
	}

	public void setLoginState(int text) {
		prefsEditor.putInt(AppConstants.LOGIN_STATE, text);
		prefsEditor.commit();
	}

	public void saveSyncState(int text) {
		prefsEditor.putInt(AppConstants.SYNC_STATE, text);
		prefsEditor.commit();
	}

	public int getSyncState() {
		return appSharedPrefs.getInt(AppConstants.SYNC_STATE, 0);
	}

	public void saveRefMode(int m) {
		prefsEditor.putInt(AppConstants.REF_TICKET_MODE, m);
		prefsEditor.commit();
	}

	public static int getRefMode() {
		return appSharedPrefs.getInt(AppConstants.REF_TICKET_MODE, 1);
	}

	public String getSiteID() {
		return appSharedPrefs.getString(AppConstants.SITE_ID_ALIAS, "");
	}

	public void setSiteID(String text) {
		prefsEditor.putString(AppConstants.SITE_ID_ALIAS, text);
		prefsEditor.commit();
	}

	public String getSiteName() {
		return appSharedPrefs.getString(AppConstants.SITE_ID_NAME, "");
	}

	public void setSiteName(String text) {
		prefsEditor.putString(AppConstants.SITE_ID_NAME, text);
		prefsEditor.commit();
	}

	public String getEtsSiteID() {
		return appSharedPrefs.getString(AppConstants.ETS_ID, "");
	}

	public void setEtsSiteID(String text) {
		prefsEditor.putString(AppConstants.ETS_ID, text);
		prefsEditor.commit();
	}

	public static String getConfigIP() {
		return appSharedPrefs.getString(AppConstants.APP_CONFIG_IP, "");
	}

	public void setConfigIP(String text) {
		prefsEditor.putString(AppConstants.APP_CONFIG_IP, text);
		prefsEditor.commit();
	}

	public String getRights() {
		return appSharedPrefs.getString(AppConstants.FORM_RIGHTS, "");
	}

	public void setRights(String text) {
		prefsEditor.putString(AppConstants.FORM_RIGHTS, text);
		prefsEditor.commit();
	}

	public String getDataTS() {
		return appSharedPrefs.getString(AppConstants.TIME_STAMP, "");
	}

	public void setDataTS(String text) {
		prefsEditor.putString(AppConstants.TIME_STAMP, text);
		prefsEditor.commit();
	}

	public String getSiteAuditRights() {
		return appSharedPrefs.getString(AppConstants.SITE_AUDIT_RIGHTS, "");
	}

	public void setSiteAuditRights(String text) {
		prefsEditor.putString(AppConstants.SITE_AUDIT_RIGHTS, text);
		prefsEditor.commit();
	}

	public String getAuditMode() {
		return appSharedPrefs.getString(AppConstants.AUDIT_MODE, "no");
	}

	public void setAuditMode(String text) {
		prefsEditor.putString(AppConstants.AUDIT_MODE, text);
		prefsEditor.commit();
	}

	public String getTxnId() {
		return appSharedPrefs.getString(AppConstants.TXN_ID, "");
	}

	public void setTxnId(String text) {
		prefsEditor.putString(AppConstants.TXN_ID, text);
		prefsEditor.commit();
	}

	public String getGCMRegistationId() {
		return appSharedPrefs.getString(AppConstants.FCM_ID, "");
	}

	public void setGCMRegistationId(String text) {
		prefsEditor.putString(AppConstants.FCM_ID, text);
		prefsEditor.commit();
	}

	public String getPopUp() {
		return appSharedPrefs.getString(AppConstants.POPUP_NOTIFICATION, "");
	}

	public void setPopUp(String text) {
		prefsEditor.putString(AppConstants.POPUP_NOTIFICATION, text);
		prefsEditor.commit();
	}

	public String getNotificationTone() {
		return appSharedPrefs.getString(AppConstants.NOTIFICATION_SOUND, "");
	}

	public void setNotificationTone(String text) {
		prefsEditor.putString(AppConstants.NOTIFICATION_SOUND, text);
		prefsEditor.commit();
	}

	public String getNotificationToneName() {
		return appSharedPrefs.getString(AppConstants.NOTIFICATION_SOUND_NAME, "");
	}

	public void setNotificationToneName(String text) {
		prefsEditor.putString(AppConstants.NOTIFICATION_SOUND_NAME, text);
		prefsEditor.commit();
	}

	public String getVibrate() {
		return appSharedPrefs.getString(AppConstants.NOTIFICATION_VIBRATE, "");
	}

	public void setVibrate(String text) {
		prefsEditor.putString(AppConstants.NOTIFICATION_VIBRATE, text);
		prefsEditor.commit();
	}

	public void setNotificationListFlag(int text) {
		prefsEditor.putInt(AppConstants.NOTIFICATION_LIST_CLASS_FLAG, text);
		prefsEditor.commit();
	}

	public int getNotificationListFlag() {
		return appSharedPrefs.getInt(AppConstants.NOTIFICATION_LIST_CLASS_FLAG, 1);
	}

	public void setHomeScreenFlag(int text) {
		prefsEditor.putInt(AppConstants.HOME_SCREEN_FLAG, text);
		prefsEditor.commit();
	}

	public int getHomeScreenFlag() {
		return appSharedPrefs.getInt(AppConstants.HOME_SCREEN_FLAG, 1);
	}

	public void setFFminimage(int text) {
		prefsEditor.putInt(AppConstants.FILLING_MIN_IMAGES, text);
		prefsEditor.commit();
	}

	public int getFFminimage() {
		return appSharedPrefs.getInt(AppConstants.FILLING_MIN_IMAGES, 1);
	}


	public void setFFmaximage(int text) {
		prefsEditor.putInt(AppConstants.FILLING_MAX_IMAGES, text);
		prefsEditor.commit();
	}

	public int getFFmaximage() {
		return appSharedPrefs.getInt(AppConstants.FILLING_MAX_IMAGES, 1);
	}

	public void setFFimageMessage(String text) {
		prefsEditor.putString(AppConstants.FILLING_IMGS_MESSAGE, text);
		prefsEditor.commit();
	}

	public String getFFimageMessage() {
		return appSharedPrefs.getString(AppConstants.FILLING_IMGS_MESSAGE, "");
	}

	public void setSrvcTime(int text) {
		prefsEditor.putInt(AppConstants.SERVICE_RECURSIVE_TIME, text);
		prefsEditor.commit();
	}

	public int getSrvcTime() {
		return appSharedPrefs.getInt(AppConstants.SERVICE_RECURSIVE_TIME, 600000);
	}

	public void setSavePMBackgroundEnable(int text) {
		prefsEditor.putInt(AppConstants.SUBMIT_PM_BACKGROUND, text);
		prefsEditor.commit();
	}

	public int getSavePMBackgroundEnable() {
		return appSharedPrefs.getInt(AppConstants.SUBMIT_PM_BACKGROUND, 0);
	}

	public void setSiteNameEnable(int text) {
		prefsEditor.putInt(AppConstants.SITE_NAME_ENABLE, text);
		prefsEditor.commit();
	}

	public int getSiteNameEnable() {
		return appSharedPrefs.getInt(AppConstants.SITE_NAME_ENABLE, 0);
	}

	public void setSearchTTDateRange(int text) {
		prefsEditor.putInt(AppConstants.SEARCH_TKT_DATE_RANGE, text);
		prefsEditor.commit();
	}

	public int getSearchTTDateRange() {
		return appSharedPrefs.getInt(AppConstants.SEARCH_TKT_DATE_RANGE, 7);
	}

	public void setEnablePrePopulateSitesTT(int text) {
		prefsEditor.putInt(AppConstants.ENABLE_PREPOPULATE_SITES_TT, text);
		prefsEditor.commit();
	}

	public int getEnablePrePopulateSitesTT() {
		return appSharedPrefs.getInt(AppConstants.ENABLE_PREPOPULATE_SITES_TT, 0);
	}


	public void setCheckPopup(String text) {
		prefsEditor.putString(AppConstants.CHECK_POPUP, text);
		prefsEditor.commit();
	}

	public String getCheckPopup() {
		return appSharedPrefs.getString(AppConstants.CHECK_POPUP, "");
	}

	public void setToastFlag(String toast) {
		prefsEditor.putString(AppConstants.TOAST_FLAG, toast);
		prefsEditor.commit();
	}

	public String getToastFlag() {
		return appSharedPrefs.getString(AppConstants.TOAST_FLAG, "");
	}

	public void setListToast(String listToast) {
		prefsEditor.putString(AppConstants.TOAST_SHOW, listToast);
		prefsEditor.commit();
	}

	public String getListToast() {
		return appSharedPrefs.getString(AppConstants.TOAST_SHOW, "");
	}

	public void setTTAssignRb(String TTAssignRb) {
		prefsEditor.putString(AppConstants.TT_ASSIGN_NOTIFICATION_CHK, TTAssignRb);
		prefsEditor.commit();
	}

	public String getTTAssignRb() {
		return appSharedPrefs.getString(AppConstants.TT_ASSIGN_NOTIFICATION_CHK, "");
	}

	public void setTTUpdateRb(String TTUpdateRb) {
		prefsEditor.putString(AppConstants.TT_UPDATE_NOTIFICATION_CHK, TTUpdateRb);
		prefsEditor.commit();
	}

	public String getTTUpdateRb() {
		return appSharedPrefs.getString(AppConstants.TT_UPDATE_NOTIFICATION_CHK, "");
	}

	public void setTTEscalateRb(String TTEscalateRb) {
		prefsEditor.putString(AppConstants.TT_ESC_NOTIFICATION_CHK, TTEscalateRb);
		prefsEditor.commit();
	}

	public String getTTEscalateRb() {
		return appSharedPrefs.getString(AppConstants.TT_ESC_NOTIFICATION_CHK, "");
	}

	public void setPMScheduleRb(String PMScheduleRb) {
		prefsEditor.putString(AppConstants.PM_SCHEDULE_NOTIFICATION_CHK, PMScheduleRb);
		prefsEditor.commit();
	}

	public String getPMScheduleRb() {
		return appSharedPrefs.getString(AppConstants.PM_SCHEDULE_NOTIFICATION_CHK, "");
	}

	public void setPMEscalateRb(String PMEscalateRb) {
		prefsEditor.putString(AppConstants.PM_ESC_NOTIFICATION_CHK, PMEscalateRb);
		prefsEditor.commit();
	}

	public String getPMEscalateRb() {
		return appSharedPrefs.getString(AppConstants.PM_ESC_NOTIFICATION_CHK, "");
	}

	public void setToggleButton(String togglebtn) {
		prefsEditor.putString(AppConstants.TOGGLE_BUTTON, togglebtn);
		prefsEditor.commit();
	}

	public String getToggleButton() {
		return appSharedPrefs.getString(AppConstants.TOGGLE_BUTTON, "");
	}

	public void SetBackModeNotifi123(int m) {
		prefsEditor.putInt(AppConstants.BACK_SCREEN, m);
		prefsEditor.commit();
	}

	public int getBackModeNotifi123() {
		return appSharedPrefs.getInt(AppConstants.BACK_SCREEN, 1);
	}

	public void SetBackModeNotifi45(int m) {
		prefsEditor.putInt(AppConstants.BACK_SCREEN, m);
		prefsEditor.commit();
	}

	public int getBackModeNotifi45() {
		return appSharedPrefs.getInt(AppConstants.BACK_SCREEN, 1);
	}

	public void SetBackModeNotifi6(int m) {
		prefsEditor.putInt(AppConstants.BACK_SCREEN, m);
		prefsEditor.commit();
	}

	public int getBackModeNotifi6() {
		return appSharedPrefs.getInt(AppConstants.BACK_SCREEN, 1);
	}

	public void SetBackModeNotifi7(int m) {
		prefsEditor.putInt(AppConstants.BACK_SCREEN, m);
		prefsEditor.commit();
	}

	public int getBackModeNotifi7() {
		return appSharedPrefs.getInt(AppConstants.BACK_SCREEN, 1);
	}

	public void setTicketFrmNtBr(String frmbr) {
		prefsEditor.putString("frmbr", frmbr);
		prefsEditor.commit();
	}

	public String getTicketFrmNtBr() {
		return appSharedPrefs.getString("frmbr", "");
	}

	public void setPMTabs(String frmbr) {
		prefsEditor.putString(AppConstants.PM_TABS, frmbr);
		prefsEditor.commit();
	}

	public String getsetPMTabs() {
		return appSharedPrefs.getString(AppConstants.PM_TABS, "");
	}

	public void setBackPressHome(String backPress) {
		prefsEditor.putString(AppConstants.BACK_PRESS_HOMESCREEN, backPress);
		prefsEditor.commit();
	}

	public String getBackPressHome() {
		return appSharedPrefs.getString(AppConstants.BACK_PRESS_HOMESCREEN, "");
	}
	public void setMaxAttendUser(String max) {
		prefsEditor.putString(AppConstants.MAX_LOGIN_ATTEMPT, max);
		prefsEditor.commit();
	}

	public String getMaxAttendUser() {
		return appSharedPrefs.getString(AppConstants.MAX_LOGIN_ATTEMPT, "");
	}

	public void setLanCode(String code) {
		prefsEditor.putString(AppConstants.LANGUAGE_CODE, code);
		prefsEditor.commit();
	}

	public String getLanCode() {
		return appSharedPrefs.getString(AppConstants.LANGUAGE_CODE, "");
	}

	public int getTTmaximage() {
		return appSharedPrefs.getInt(AppConstants.TT_MAX_IMAGES, 1);
	}

	public void setTTmaximage(int text) {
		prefsEditor.putInt(AppConstants.TT_MAX_IMAGES, text);
		prefsEditor.commit();
	}

	public int getTTminimage() {
		return appSharedPrefs.getInt(AppConstants.TT_MIN_IMAGES, 1);
	}

	public void setTTminimage(int text) {
		prefsEditor.putInt(AppConstants.TT_MIN_IMAGES, text);
		prefsEditor.commit();
	}

	public void setTTimageMessage(String text) {
		prefsEditor.putString(AppConstants.TT_IMGS_MESSAGE, text);
		prefsEditor.commit();
	}

	public String getTTimageMessage() {
		return appSharedPrefs.getString(AppConstants.TT_IMGS_MESSAGE, "");
	}


	public void setTTMediaFileType(String text) {
		prefsEditor.putString("ttMediaType", text);
		prefsEditor.commit();
	}

	public String getTTMediaFileType() {
		return appSharedPrefs.getString("ttMediaType", "");
	}

	public int getTTmaximageHS() {
		return appSharedPrefs.getInt(AppConstants.TT_MAX_IMAGES_HS, 1);
	}

	public void setTTmaximageHS(int text) {
		prefsEditor.putInt(AppConstants.TT_MAX_IMAGES_HS, text);
		prefsEditor.commit();
	}

	public int getTTminimageHS() {
		return appSharedPrefs.getInt(AppConstants.TT_MIN_IMAGES_HS, 1);
	}

	public void setTTminimageHS(int text) {
		prefsEditor.putInt(AppConstants.TT_MIN_IMAGES_HS, text);
		prefsEditor.commit();
	}

	public void setTTimageMessageHS(String text) {
		prefsEditor.putString(AppConstants.TT_IMGS_MESSAGE_HS, text);
		prefsEditor.commit();
	}

	public String getTTimageMessageHS() {
		return appSharedPrefs.getString(AppConstants.TT_IMGS_MESSAGE_HS, "");
	}

	public void setTTMediaFileTypeHS(String text) {
		prefsEditor.putString("ttMediaTypeHS", text);
		prefsEditor.commit();
	}

	public String getTTMediaFileTypeHS() {
		return appSharedPrefs.getString("ttMediaTypeHS", "");
	}

	public void setEnableFillingField(String text) {
		prefsEditor.putString(AppConstants.ENABLE_FF_FIELDS, text);
		prefsEditor.commit();
	}

	public String getEnableFillingField() {
		return appSharedPrefs.getString(AppConstants.ENABLE_FF_FIELDS, "");
	}

	public void setLoginId(String text) {
		prefsEditor.putString(AppConstants.LOGIN_ID, text);
		prefsEditor.commit();
	}

	public String getLoginId() {
		return appSharedPrefs.getString(AppConstants.LOGIN_ID, "");
	}

	public void setPassword(String text) {
		prefsEditor.putString(AppConstants.PASSWORD, text);
		prefsEditor.commit();
	}

	public String getPassword() {
		return appSharedPrefs.getString(AppConstants.PASSWORD, "");
	}

	public void setPmConfiguration(String text) {
		prefsEditor.putString(AppConstants.PM_CONFIGURATION, text);
		prefsEditor.commit();
	}

	public String getPmConfiguration() {
		return appSharedPrefs.getString(AppConstants.PM_CONFIGURATION, "");
	}

	public void setTTConfiguration(String text) {
		prefsEditor.putString(AppConstants.TT_CONFIGURATION, text);
		prefsEditor.commit();
	}

	public String getTTConfiguration() {
		return appSharedPrefs.getString(AppConstants.TT_CONFIGURATION, "");
	}

	public void setFFConfiguration(String text) {
		prefsEditor.putString(AppConstants.FF_CONFIGURATION, text);
		prefsEditor.commit();
	}

	public String getFFConfiguration() {
		return appSharedPrefs.getString(AppConstants.FF_CONFIGURATION, "");
	}

	public void setAutoDateTime(String text) { // 0.5
		prefsEditor.putString(AppConstants.CHECK_AUTO_DATE_TIME, text);
		prefsEditor.commit();
	}

	public String getAutoDateTime() {
		return appSharedPrefs.getString(AppConstants.CHECK_AUTO_DATE_TIME, "");
	}

	public void setUserTracking(String keys) {
		prefsEditor.putString(AppConstants.USER_TRACKING, keys);
		prefsEditor.commit();
	}

	public String getUserTracking() {
		return appSharedPrefs.getString(AppConstants.USER_TRACKING, "60000");
	}

	public void setUserTrackUploadTime(String tracTime) {
		prefsEditor.putString(AppConstants.USER_TRACKING_UPLOAD_TIME, tracTime);
		prefsEditor.commit();
	}

	public String getUserTrackUploadTime() {
		return appSharedPrefs.getString(AppConstants.USER_TRACKING_UPLOAD_TIME, "600000");
	}

	public void setAppNameMockLocation(String text) { // 0.5
		prefsEditor.putString(AppConstants.MOCK_LOCATION, text);
		prefsEditor.commit();
	}

	public String getAppNameMockLocation() {
		return appSharedPrefs.getString(AppConstants.MOCK_LOCATION, "");
	}

	public void setAppSettingNotification(String text) { // 0.5
		prefsEditor.putString(AppConstants.NOTIFICATION_SETTING, text);
		prefsEditor.commit();
	}

	public String getAppSettingNotification() {
		return appSharedPrefs.getString(AppConstants.NOTIFICATION_SETTING, "");
	}

	public void setTrackMode(int text) { // 0.5
		prefsEditor.putInt(AppConstants.TRACKING_MODE, text);
		prefsEditor.commit();
	}

	public int getTrackMode() {
		return appSharedPrefs.getInt(AppConstants.TRACKING_MODE, 0);
	}

	public void setTrackingOnOff(String text) { // 0.5
		prefsEditor.putString(AppConstants.TRACKING_ENABLE, text);
		prefsEditor.commit();
	}

	public String getTrackingOnOff() {
		return appSharedPrefs.getString(AppConstants.TRACKING_ENABLE, "OFF");
	}

	public void setPMchecklist(String text) { // 0.5
		prefsEditor.putString("pmchecklist", text);
		prefsEditor.commit();
	}

	public String getPMchecklist() {
		return appSharedPrefs.getString("pmchecklist", "");
	}

	public void setPMchecklistBackUp(String text) { // 0.5
		prefsEditor.putString("pmchecklistBackUp", text);
		prefsEditor.commit();
	}

	public String getPMchecklistBackUp() {
		return appSharedPrefs.getString("pmchecklistBackUp", "");
	}

	public void setPMBackTask(int text) { // 0.5
		prefsEditor.putInt("", text);
		prefsEditor.commit();
	}

	public int getPMBackTask() {
		return appSharedPrefs.getInt("", 0);
	}

	public void setPMAssetBackTask(int text) { // 0.5
		prefsEditor.putInt("PMAssetBackTask", text);
		prefsEditor.commit();
	}

	public int getPMAssetBackTask() {
		return appSharedPrefs.getInt("PMAssetBackTask", 0);
	}

	public void setPMImageUploadType(int text) {
		prefsEditor.putInt("imgUploadflag", text);
		prefsEditor.commit();
	}

	public int getPMImageUploadType() {
		return appSharedPrefs.getInt("imgUploadflag", 1);
	}

	public void setSiteMotorable(int text) {
		prefsEditor.putInt("siteMotorable", text);
		prefsEditor.commit();
	}

	public int getSiteMotorable() {
		return appSharedPrefs.getInt("siteMotorable", 1);
	}

	public void setPMRejectMadatoryFields(int text) {
		prefsEditor.putInt("pmReject", text);
		prefsEditor.commit();
	}

	public int getPMRejectMadatoryFields() {
		return appSharedPrefs.getInt("pmReject", 1);
	}

	public void setPMReviewPlanDate(int text) {
		prefsEditor.putInt("PMReviewPlanDate", text);
		prefsEditor.commit();
	}

	public int getPMReviewPlanDate() {
		return appSharedPrefs.getInt("PMReviewPlanDate", 1);
	}

	public void setSpareSerialFlagScreen(int text) {
		prefsEditor.putInt("serialFlag", text);
		prefsEditor.commit();
	}

	public int getSpareSerialFlagScreen() {
		return appSharedPrefs.getInt("serialFlag", 0);
	}


	public void setTTupdateStatus(int TTupdateStatus) { // 0.5
		prefsEditor.putInt("", TTupdateStatus);
		prefsEditor.commit();
	}

	public int getTTupdateStatus() {
		return appSharedPrefs.getInt("", 0); //0 means ticket not update or 1 means ticket updated
	}

	public void setTTtabSelection(String TTtabSelection) { // 0.5
		prefsEditor.putString("TTtabSelection", TTtabSelection);
		prefsEditor.commit();
	}

	public String getTTtabSelection() {
		return appSharedPrefs.getString("TTtabSelection", "Assigned");
	}

	public void setTTModuleSelection(String TTModuleSelection) { // 0.5
		prefsEditor.putString("TTModuleSelection", TTModuleSelection);
		prefsEditor.commit();
	}

	public String getTTModuleSelection() {
		return appSharedPrefs.getString("TTModuleSelection", "Module");
	}

	public void setTTChklistMadatory(int text) {
		prefsEditor.putInt("ttChklistMadatory", text);
		prefsEditor.commit();
	}

	public int getTTChklistMadatory() {
		return appSharedPrefs.getInt("ttChklistMadatory", 0);
	}

	public void setVideoUploadMaxSize(int text) {
		prefsEditor.putInt("maxFileSize", text);
		prefsEditor.commit();
	}

	public int getVideoUploadMaxSize() {
		return appSharedPrefs.getInt("maxFileSize", 0);
	}

	public void setIsVideoCompress(int text) {
		prefsEditor.putInt("isVideoCompress", text);
		prefsEditor.commit();
	}
	public void setDocumentUploadMaxSize(int text) {
		prefsEditor.putInt("maxFileDocSize", text);
		prefsEditor.commit();
	}

	public void setCheckIn(String value)
	{
		prefsEditor.putString("checkInValue",value);
		prefsEditor.commit();
	}

	public String getCheckIn()
	{
		return  appSharedPrefs.getString("checkInValue","");
	}

	public void setCheckInStatus(String value)
	{
		prefsEditor.putString("checkInStatusValue",value);
		prefsEditor.commit();
	}

	public String getCheckInSataus()
	{
		return  appSharedPrefs.getString("checkInStatusValue","");
	}

	public int getDocumentUploadMaxSize() {
		return appSharedPrefs.getInt("maxFileDocSize", 0);
	}


	public int getIsVideoCompress() {
		return appSharedPrefs.getInt("isVideoCompress", 0);
	}

	public void setIsNetworkConnection(int text) {
		prefsEditor.putInt("networkConnection", text);
		prefsEditor.commit();
	}

	public int getIsNetworkConnection() {
		return appSharedPrefs.getInt("networkConnection", 0);
	}

	public void setNetworkConnectionTime(String networkConnectionTime) {
		prefsEditor.putString("NetworkConnectionTime", networkConnectionTime);
		prefsEditor.commit();
	}

	public String getNetworkConnectionTime() {
		return appSharedPrefs.getString("NetworkConnectionTime", Utils.dateNotification());
	}

	public void setModuleName(String ModuleName) { // 0.5
		prefsEditor.putString("ModuleName", ModuleName);
		prefsEditor.commit();
	}

	public String getModuleName() {
		return appSharedPrefs.getString("ModuleName", "ModuleName");
	}

	public void setModuleIndex(int ModuleName) { // 0.5
		prefsEditor.putInt("ModuleIndex", ModuleName);
		prefsEditor.commit();
	}

	public int getModuleIndex() {
		return appSharedPrefs.getInt("ModuleIndex", 0);
	}

	/*
	public void setFormName(String ModuleName) { // 0.5
		prefsEditor.putString("formName", ModuleName);
		prefsEditor.commit();
	}

	public String getFormName() {
		return appSharedPrefs.getString("formName", "AddAccessRequest");
	}
	*/
	public void setUserNumber(String mobileNumber) { // 0.5
		prefsEditor.putString(AppConstants.USER_MOBILE, mobileNumber);
		prefsEditor.commit();
	}

	public String getUserNumber() {
		return appSharedPrefs.getString(AppConstants.USER_MOBILE, "");
	}

	public void setUserMailid(String mailId) { // 0.5
		prefsEditor.putString(AppConstants.USER_MAILID, mailId);
		prefsEditor.commit();
	}

	public String getUserMailid() {
		return appSharedPrefs.getString(AppConstants.USER_MAILID, "");
	}

	public void setUserGroup(String userGroup) { // 0.5
		prefsEditor.putString(AppConstants.USER_GROUP, userGroup);
		prefsEditor.commit();
	}

	public String getUserGroup() {
		return appSharedPrefs.getString(AppConstants.USER_GROUP, "");
	}

	public String getUserGroupName() {
		return appSharedPrefs.getString(AppConstants.USER_GROUP_NAME, "");
	}

	public void setUserGroupName(String userGroup) { // 0.5
		prefsEditor.putString(AppConstants.USER_GROUP_NAME, userGroup);
		prefsEditor.commit();
	}

	// 1.0
	public String getUserCategory() {
		return appSharedPrefs.getString(AppConstants.USER_CATEGORY, "");
	}

	public void setUserCategory(String text) {
		prefsEditor.putString(AppConstants.USER_CATEGORY, text);
		prefsEditor.commit();
	}

	public String getUserSubCategory() {
		return appSharedPrefs.getString(AppConstants.USER_SUB_CATEGORY, "");
	}

	public void setUserSubCategory(String text) {
		prefsEditor.putString(AppConstants.USER_SUB_CATEGORY, text);
		prefsEditor.commit();
	}


	public String getOperatorWiseUserField() {
		return appSharedPrefs.getString(AppConstants.OPERATOR_WISE_USER_FIELD, "");
	}

	public void setOperatorWiseUserField(String text) {
		prefsEditor.putString(AppConstants.OPERATOR_WISE_USER_FIELD, text);
		prefsEditor.commit();
	}

	public String getHyperLinkPM() {
		return appSharedPrefs.getString(AppConstants.PM_HYPER_LINK, "0");
	}

	public void setHyperLinkPM(String text) {
		prefsEditor.putString(AppConstants.PM_HYPER_LINK, text);
		prefsEditor.commit();
	}

	public String getTocForm() {
		return appSharedPrefs.getString("toc", "0");
	}

	public void setTocForm(String text) {
		prefsEditor.putString("toc", text);
		prefsEditor.commit();
	}

	public String getSite() {
		return appSharedPrefs.getString("site", "0");
	}

	public void setSite(String text) {
		prefsEditor.putString("site", text);
		prefsEditor.commit();
	}

	public String getPSDT() {
		return appSharedPrefs.getString("psdt", "");
	}

	public void setPSDT(String text) {
		prefsEditor.putString("psdt", text);
		prefsEditor.commit();
	}

	public String getPEDT() {
		return appSharedPrefs.getString("pedt", "");
	}

	public void setPEDT(String text) {
		prefsEditor.putString("pedt", text);
		prefsEditor.commit();
	}

	public void setSiteLockAPICall(int lock) { // 0.5
		prefsEditor.putInt("lock", lock);
		prefsEditor.commit();
	}

	public int isSiteLockAPICall() {
		return appSharedPrefs.getInt("lock", 0);
	}


	/*Added by Anshul Sharma*/
	public String getUnsyncAudit() {
		return appSharedPrefs.getString("UNSYNC_AUDIT", "");
	}

	public void setUnsyncAudit(String auditId) {
		prefsEditor.putString("UNSYNC_AUDIT", auditId);
		prefsEditor.commit();
	}

	/*Added by Anshul Sharma*/
	public String getTimerCycleCamunda() {
		return appSharedPrefs.getString("timerCycle", "");
	}

	public void setTimerCycleCamunda(String auditId) {
		prefsEditor.putString("timerCycle", auditId);
		prefsEditor.commit();
	}

	public void setCalendarMonth(int text) {
		prefsEditor.putInt("calendarMonth", text);
		prefsEditor.commit();
	}

	public int getCalendarMonth() {
		return appSharedPrefs.getInt("calendarMonth", 0);
	}

	/*Added by Avdhesh kumar*/

	public String getUserPermission() {
		return appSharedPrefs.getString(AppConstants.USER_GIVEN_PERMISSION, "");
	}

	public void setUserPermission(String text) {
		prefsEditor.putString(AppConstants.USER_GIVEN_PERMISSION, text);
		prefsEditor.commit();
	}

	public String getUserPunchInOut() {
		return appSharedPrefs.getString(AppConstants.PUNCH_IN_OUT, "");
	}

	public void setUserPunchInOut(String text) {
		prefsEditor.putString(AppConstants.PUNCH_IN_OUT, text);
		prefsEditor.commit();
	}
	public String getPlugIdentty() {
		return appSharedPrefs.getString(AppConstants.PLUG_IDENTY, "");
	}

	public void setPlugIdentgty(String text) {
		prefsEditor.putString(AppConstants.PLUG_IDENTY, text);
		prefsEditor.commit();
	}
	public String getCheckInTime() {
		return appSharedPrefs.getString(AppConstants.CHECK_OUT_TIME, "");
	}

	public void setCheckInTime(String text) {
		prefsEditor.putString(AppConstants.CHECK_OUT_TIME, text);
		prefsEditor.commit();
	}
	public String getCheckOutTime() {
		return appSharedPrefs.getString(AppConstants.CHECK_OUT_TIME, "");
	}

	public void setCheckOutTime(String text) {
		prefsEditor.putString(AppConstants.CHECK_OUT_TIME, text);
		prefsEditor.commit();
	}

	public String getTimeInterval() {
		return appSharedPrefs.getString(AppConstants.TIME_INTERVAL, "");
	}

	public void setTimeInterval(String text) {
		prefsEditor.putString(AppConstants.TIME_INTERVAL, text);
		prefsEditor.commit();
	}

	public String getPMScheduleCountType() {
		return appSharedPrefs.getString("type", "");
	}

	public void setPMScheduleCountType(String text) {
		prefsEditor.putString("type", text);
		prefsEditor.commit();
	}

	public String getTTEnergyDescription() {
		return appSharedPrefs.getString("ttEnergy", "");
	}

	public void setTTEnergyDescription(String text) {
		prefsEditor.putString("ttEnergy", text);
		prefsEditor.commit();
	}

	public String getTTAccessDescription() {
		return appSharedPrefs.getString("ttAccessDescription", "");
	}

	public void setTTAccessDescription(String text) {
		prefsEditor.putString("ttAccessDescription", text);
		prefsEditor.commit();
	}


	public void setApprovalRemarks(String text) {
		prefsEditor.putString("isRmks", text);
		prefsEditor.commit();
	}

	public String getApprovalRemarks() {
		return appSharedPrefs.getString("isRmks", "0");
	}


	public void setIsRating(String text) {
		prefsEditor.putString("isRating", text);
		prefsEditor.commit();
	}

	public String getIsRating() {
		return appSharedPrefs.getString("isRating", "0");
	}

	public void setPMphotoInfoRating(String text) {
		prefsEditor.putString("PMphotInfoRating", text);
		prefsEditor.commit();
	}

	public String getPMphotoInfoRating() {
		return appSharedPrefs.getString("PMphotInfoRating", "0");
	}

	public void setQualityPMtaskRating(String text) {
		prefsEditor.putString("QualityPMtaskRating", text);
		prefsEditor.commit();
	}

	public String getQualityPMtaskRating() {
		return appSharedPrefs.getString("QualityPMtaskRating", "0");
	}

	public void setVisualCheck1Rating(String text) {
		prefsEditor.putString("VisualCheck1Rating", text);
		prefsEditor.commit();
	}

	public String getVisualCheck1Rating() {
		return appSharedPrefs.getString("VisualCheck1Rating", "0");
	}

	public void setVisualCheck2Rating(String text) {
		prefsEditor.putString("VisualCheck2Rating", text);
		prefsEditor.commit();
	}

	public String getVisualCheck2Rating() {
		return appSharedPrefs.getString("VisualCheck2Rating", "0");
	}

	public void setVisualCheck3Rating(String text) {
		prefsEditor.putString("VisualCheck3Rating", text);
		prefsEditor.commit();
	}

	public String getVisualCheck3Rating() {
		return appSharedPrefs.getString("VisualCheck3Rating", "0");
	}


}
