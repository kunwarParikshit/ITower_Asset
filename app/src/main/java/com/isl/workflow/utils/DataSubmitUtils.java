package com.isl.workflow.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.hsse.HsseConstant;
import com.isl.hsse.HsseFrame;
import com.isl.itower.GPSTracker;
import com.isl.modal.MenuDetail;
import com.isl.modal.Response;
import com.isl.util.Utils;
import com.isl.workflow.CallService;
import com.isl.workflow.PreChecklistAdapter;
import com.isl.workflow.SubmitData;
import com.isl.workflow.WorkflowImpl;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.form.control.GridAdapter;
import com.isl.workflow.form.control.ImageAdapter;
import com.isl.workflow.modal.ActionType;
import com.isl.workflow.modal.AuditTrail;
import com.isl.workflow.modal.CamundaInVariables;
import com.isl.workflow.modal.CamundaModVariables;
import com.isl.workflow.modal.CamundaVariable;
import com.isl.workflow.modal.ComponentType;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.ImagesList;
import com.isl.workflow.modal.JavaScriptExpression;
import com.isl.workflow.modal.OnChangeDetail;
import com.isl.workflow.modal.Parameter;
import com.isl.workflow.modal.ServiceDetail;
import com.isl.workflow.modal.UploadAssestDetail;
import com.isl.workflow.modal.UploadDocDetail;
import com.isl.workflow.modal.responce.AccessTokenResponce;
import com.isl.workflow.modal.responce.GetSerachKeyResponse;
import com.isl.workflow.modal.responce.KeyDetailsResponce;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infozech.itower.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataSubmitUtils {

    private static String operation = "A";
    private static String source = "AddRequest";
    private static String latitude = "AddRequest";
    private static String longitude = "AddRequest";
    private static ArrayList<ImagesList>imagesList,imagesList1;
    private static PreChecklistAdapter preChecklistAdapter,preChecklistAdapter1;
    public static Context mcontext;
    public static int position;

    public static void callService(Context context, String fieldKey, ServiceDetail serviceDetail) {
        AppPreferences mAppPreferences = new AppPreferences(context);
        HashMap<String, Object> data = new HashMap<String, Object>();
        List<String> auditTrail = new ArrayList<String>();
        operation = (String) FormCacheManager.getPrvFormData().get(Constants.OPERATION);
        source = (String) FormCacheManager.getPrvFormData().get(Constants.TXN_SOURCE);
        latitude = null;
        longitude = null;

        if (!validateGPS(context)) {
            return;
        }

        boolean validation = false;
        boolean isParameterValidated = false;

        if (serviceDetail.isValidateFormdata()) {
            validation = validateFormData(context, data, fieldKey, auditTrail, serviceDetail.isGenrateAuditTrail());
        } else if (serviceDetail.getValidateParameters() != null && serviceDetail.getValidateParameters().size() > 0) {
            //System.out.println("Validate Parameter enabled**************************************8");
            validation = validateParameters(context, data, serviceDetail.getValidateParameters(), auditTrail, serviceDetail.isGenrateAuditTrail());
            isParameterValidated = true;
        } else {
            //auditTrail.add("Key Generation successfully");
            validation = true;
        }

        if (validation) {

            CamundaVariable variable = new CamundaVariable();
            variable.setValue(latitude);
            data.put(Constants.LATITUDE, variable);
            variable = new CamundaVariable();
            variable.setValue(longitude);
            data.put(Constants.LONGITUDE, variable);

            String url = serviceDetail.getUrl();
            String requestFlag = "";
            String newAssignee = "";

            if (!(url.contains("https") || url.contains("http"))) {
                url = AppConstants.moduleList.get(FormCacheManager.getAppPreferences().getModuleIndex()).getBaseurl() + url;
            }

            PackageInfo pInfo = null;

            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }
            variable = new CamundaVariable();
            variable.setValue(pInfo.versionName);
            data.put(AppConstants.APP_VERSIONS, variable);

            variable = new CamundaVariable();
            variable.setValue((String) FormCacheManager.getPrvFormData().get(AppConstants.OPERATION));
            data.put(AppConstants.OPERATION, variable);

            String accessForm = FormCacheManager.getFormConfiguration().getName();

            if (accessForm.contains("AccessRequest") && FormCacheManager.getFormConfiguration().getFormFields().
            containsKey(AppConstants.SITE_ID_ALIAS)) {
                variable = new CamundaVariable();
                variable.setValue((String) FormCacheManager.getPrvFormData().get(AppConstants.SITE_ID_ALIAS));
                String s = (String) FormCacheManager.getPrvFormData().get(AppConstants.SITE_ID_ALIAS);
                data.put(AppConstants.SITE_ID_ALIAS, variable);
            }
            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey(AppConstants.AbloyLockId)) {
                variable = new CamundaVariable();
                variable.setValue((String) FormCacheManager.getPrvFormData().get(AppConstants.AbloyLockId));
                data.put(AppConstants.AbloyLockId, variable);

            }
            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey(AppConstants.keySerNo)) {
                variable = new CamundaVariable();
                Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get(AppConstants.keySerNo);
                String abc = "" + FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText();
                variable.setValue(abc);

                data.put(AppConstants.keySerNo, variable);

            }

            variable = new CamundaVariable();
            variable.setValue(AppConstants.moduleList.get(FormCacheManager.getAppPreferences().getModuleIndex()).getModuleName());
            data.put(AppConstants.MODULE, variable);

            if (serviceDetail != null) {

                String tmpVal = null;

                for (Parameter param : serviceDetail.getParams()) {

                    tmpVal = "";
                    switch (param.getType()) {
                        case CONSTANT:
                            if ("current-date".equalsIgnoreCase(param.getValue())) {
                                tmpVal = DateTimeUtils.currentDateTime(param.getValKey());
                            } else {
                                tmpVal = param.getValue();
                            }
                            break;
                        case TRAN:
                            if (FormCacheManager.getPrvFormData().containsKey(param.getField())) {
                                tmpVal = (String) FormCacheManager.getPrvFormData().get(param.getField());
                            } else {
                                tmpVal = "";
                            }
                            break;
                        case SESSION:
                            // System.out.println("param"+param.getKey());
                            if (param.getValKey() != null) {
                                tmpVal = WorkFlowUtils.getSessionValue(param.getValKey());
                            }

                            break;
                        case FORM:
                            if (isParameterValidated) {
                                continue;
                            }

                            tmpVal = WorkFlowUtils.getValueFromLocalData(param.getField(), param.getValKey(), false);

                            /*if(data.containsKey(param.getField())){
                                tmpVal = ((CamundaVariable)data.get(param.getField())).getValue();
                            } else{
                                tmpVal = WorkFlowUtils.getValueFromLocalData(param.getField(),param.getValKey(),false);
                            }*/

                            break;
                        case EXPRESSION:
                            try {
                                Object temp = WorkFlowUtils.evaluateExpression(param.getExpression(), false);
                                if (temp != null) {
                                    tmpVal = org.mozilla.javascript.Context.toString(temp);
                                } else {
                                    tmpVal = "";
                                }
                            } catch (Exception exp) {
                                exp.printStackTrace();
                                tmpVal = "";
                            }
                            break;
                        default:
                            tmpVal = WorkFlowUtils.getValueFromLocalData(param.getField(), param.getValKey(), false);
                            break;
                    }

                    if (param.getKey().equalsIgnoreCase(Constants.REQUEST_FLAG)
                            || param.getKey().equalsIgnoreCase(Constants.NEW_ASSGINEE)) {

                        if (param.getKey().equalsIgnoreCase(Constants.REQUEST_FLAG)) {
                            requestFlag = tmpVal;
                        } else if (param.getKey().equalsIgnoreCase(Constants.NEW_ASSGINEE)) {
                            newAssignee = tmpVal;
                        }
                    } else {
                        variable = new CamundaVariable();
                        if (tmpVal == null) {
                            variable.setValue("");
                        } else {
                            variable.setValue(tmpVal);
                        }
                    }
                    if (data.containsKey("sid") &&
                            param.getKey().equalsIgnoreCase("sid")
                            && variable.getValue().length() == 0) {
                        variable.setValue(mAppPreferences.getSite());
                    }
                    data.put(param.getKey(), variable);
                }
            }
            if ((requestFlag == null || requestFlag.length() == 0) && data.containsKey(Constants.REQUEST_FLAG) &&
                    data.get(Constants.REQUEST_FLAG) != null) {
                requestFlag = ((CamundaVariable) data.get(Constants.REQUEST_FLAG)).getValue();
            }

            if ((newAssignee == null || newAssignee.length() == 0) && data.containsKey(Constants.NEW_ASSGINEE) &&
                    data.get(Constants.NEW_ASSGINEE) != null) {
                newAssignee = ((CamundaVariable) data.get(Constants.NEW_ASSGINEE)).getValue();
            }

            String jsonData = "";
            String auditJson = "";

            if (serviceDetail.isGenrateAuditTrail() && operation.equalsIgnoreCase("E") && auditTrail != null && auditTrail.size() > 0) {
                List<AuditTrail> auditTrailObjList = new ArrayList<AuditTrail>();
                for (String auditLog : auditTrail) {

                    if (auditLog == null || auditLog.trim().length() == 0) {
                        continue;
                    }
                    //System.out.println("Audit String ********************- "+auditLog);
                    AuditTrail auditTrailObj = new AuditTrail();
                    auditTrailObj.setLoginid(FormCacheManager.getAppPreferences().getLoginId());
                    auditTrailObj.setLatitude(((CamundaVariable) data.get(Constants.LATITUDE)).getValue());
                    auditTrailObj.setLongitude(((CamundaVariable) data.get(Constants.LONGITUDE)).getValue());
                    auditTrailObj.setRemarks(auditLog);
                    auditTrailObj.setTxnid((String) FormCacheManager.getPrvFormData().get(Constants.TXN_ID));
                    auditTrailObjList.add(auditTrailObj);
                }

                auditJson = Constants.gson.toJson(auditTrailObjList);

            }
            // 109
             if(UIUtils.isMatch && UIUtils.key)
             {
                String activity_status =((CamundaVariable) data.get("aststxt")).getValue();
                variable = new CamundaVariable();
                variable.setValue(activity_status);
                data.put("implementer",variable);
                variable = new CamundaVariable();
                variable.setValue("1");
                data.put("isassigncng",variable);
             }
             //109

            if (serviceDetail.getAction() == ActionType.NEW || serviceDetail.getAction() == ActionType.NEWSUBMIT) {
                CamundaInVariables inVariable = new CamundaInVariables();
                inVariable.setVariables(data);
                jsonData = Constants.gson.toJson(inVariable);
            } else if (serviceDetail.getAction() == ActionType.MODIFICATION || serviceDetail.getAction() == ActionType.MODIFICATIONSUBMIT) {
                CamundaModVariables inVariable = new CamundaModVariables();
                inVariable.setModifications(data);
                jsonData = Constants.gson.toJson(inVariable);
            } else {
                jsonData = Constants.gson.toJson(data);
            }


            System.out.println("Form data - " + jsonData);
            System.out.println("Audit Trail - " + auditJson);

            if (Utils.isNetworkAvailable(context)) {

                String msg = serviceDetail.getMsg();

                if (serviceDetail.getMsgexp() != null) {
                    Object tmpmsg = WorkFlowUtils.evaluateExpression(serviceDetail.getMsgexp(), false);
                    if (tmpmsg != null) {
                        msg = org.mozilla.javascript.Context.toString(tmpmsg);
                    }
                }

                if (serviceDetail.getAction() == ActionType.NEWSUBMIT || serviceDetail.getAction() == ActionType.MODIFICATIONSUBMIT) {
                    String key = "";
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                        key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
                        int isVisible = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getVisibility();
                        Log.d("Avi v",""+isVisible);
                        String formName = FormCacheManager.getFormConfiguration().getName();

                        if (fieldKey.equalsIgnoreCase("svbutton")&& isVisible ==0) {
                            if (formName.equalsIgnoreCase("AccessRequestImpl") ||
                                    formName.equalsIgnoreCase("AccessRequesttoc")) {
                                callGetAccessTokenForSbmit(context, key);
                            }
                        }
                    }

                    SubmitData task = new SubmitData(context, jsonData, auditJson, (String) FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_ID), operation,
                            (String) FormCacheManager.getPrvFormData().get(Constants.TASK_ID), (String) FormCacheManager.getPrvFormData().get(HsseConstant.OLD_TKT_STATUS)
                            , (String) FormCacheManager.getPrvFormData().get(HsseConstant.OLD_GRP), requestFlag, newAssignee);
                    task.execute(url);
                } else {
                    CallService task = new CallService(context, jsonData, auditJson, (String) FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_ID), operation,
                            (String) FormCacheManager.getPrvFormData().get(Constants.TASK_ID), requestFlag, newAssignee, msg, serviceDetail.isShowMessage(), serviceDetail.isOnSuccessCloseForm(), source);
                    task.execute(url);
                }
            } else {
                if (serviceDetail.isSaveLocalInFailure()) {
                    WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(context);
                    db.open();
                    db.insertDataLocally(AppConstants.moduleList.get(FormCacheManager.getAppPreferences().getModuleIndex()).getModuleName(), jsonData, null, FormCacheManager.getAppPreferences().getUserId());

                    Utils.toast(context, "66");
                    //No internet connection.Data stored locally in the app
                    // context.finish();
                    db.close();
                }
            }
        }
    }

    public static boolean validateFormData(Context context, HashMap<String, Object> formData,
                                           String fieldKey, List<String> auditTrail, boolean isAuditTrail) {

        StringBuilder auditTrailObj = new StringBuilder();
        // FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().setText(assestData);


        for (Fields formField : FormCacheManager.getFormConfiguration().getFormFields().values()) {

            if (formField.getKey().equalsIgnoreCase(fieldKey)) {
                continue;
            }

            if (!validateField(context, formField, formData, auditTrailObj, auditTrail, isAuditTrail, true)) {
                formData.clear();
                auditTrailObj.setLength(0);
                return false;
            }


        }


        if (auditTrailObj.length() > 2) {
            auditTrailObj.setLength(auditTrailObj.length() - 2);
        }
        auditTrail.add(auditTrailObj.toString());
        //System.out.println("******************* auditTrail - "+auditTrail);
        return true;
    }



    private static boolean validateParameters(Context context, HashMap<String, Object> formData, List<String> paramList, List<String> auditTrail, boolean isAuditTrail) {

        StringBuilder auditTrailObj = new StringBuilder();
        Fields formField = null;

        for (String paramKey : paramList) {
            //System.out.println("Validateion Parameter -***********************-- "+paramKey);
            formField = FormCacheManager.getFormConfiguration().getFormFields().get(paramKey);
            if (!validateField(context, formField, formData, auditTrailObj, auditTrail, isAuditTrail, true)) {
                formData.clear();
                auditTrailObj.setLength(0);
                return false;
            }
            ;

        }
        if (auditTrailObj.length() > 2) {
            auditTrailObj.setLength(auditTrailObj.length() - 2);
        }
        auditTrail.add(auditTrailObj.toString());
        //System.out.println("******************* auditTrail - "+auditTrail);
        return true;
    }

    public static boolean validateFieldList(Context context, List<String> paramList) {

        Fields formField = null;

        for (String paramKey : paramList) {
            //System.out.println("Validateion Parameter -***********************-- "+paramKey);
            formField = FormCacheManager.getFormConfiguration().getFormFields().get(paramKey);
            if (!validateField(context, formField, null, null, null, false, false)) {
                return false;
            }

            ;

        }

        return true;
    }

    public static boolean validateField(Context context, Fields formField,
                                        Map<String, Object> formData, StringBuilder auditTrailObj,
                                        List<String> auditTrail, boolean isAuditTrail,
                                        boolean isFormData) {

        boolean isValidationFailed = false;
        boolean isRequired;
        String value = "";
        DropdownValue ddVal = null;
        String errId = "";
        String errMsg = "";
        boolean tmpValue;

        FormFieldControl formControl = FormCacheManager.getFormControls().get(formField.getId());
        //System.out.println("isValidationFailed222===="+formField.getKey());
        if (formField.getValidations() == null) {
            isRequired = false;
        } else {
            isRequired = formField.getValidations().isRequired();
        }

        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("assestdetails")) {
            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("assestdetails");
            getAssestQrCode(formControl3.getId());
        }


        //Evaluate the expression
        if (formControl.getCaptionCtrl() != null && formControl.getCaptionCtrl().getVisibility() == View.VISIBLE) {

            if (formField.getValidations() != null && formField.getValidations().getRequiredexp() != null
                    && formField.getValidations().getRequiredexp().getExpression() != null
                    && formField.getValidations().getRequiredexp().getExpression().length() > 0) {

                Object result = WorkFlowUtils.evaluateExpression(formField.getValidations().getRequiredexp(), true);
                isRequired = org.mozilla.javascript.Context.toBoolean(result);

            }
        }

        switch (formField.getType()) {
            case SELECT: //For Dropdown
                ddVal = ((DropdownValue) formControl.getSelectCtrl().getSelectedItem());
                //System.out.println("********** id  - "+formControl.getKey());
                if (ddVal != null) {
                    value = ddVal.getId();
                } else {
                    value = "";
                }

                if (isRequired && formControl.getSelectCtrl().getVisibility() == View.VISIBLE) {
                    if (ddVal.getId().equals(AppConstants.DD_SELECT_ID)) {
                        isValidationFailed = true;
                        errId = "256";
                    }
                } else {
                    if (ddVal == null || ddVal.getId().equals(AppConstants.DD_SELECT_ID)) {
                        value = "";
                    }
                }

                break;
            case MULTISELECT: //For multi select Dropdown
                for (DropdownValue tempval : formControl.getSelectedVal()) {
                    value = value + "," + tempval.getId();
                }
                if (value != null && value.length() > 0) {
                    value = value.substring(1);
                }

                if (isRequired && formControl.getMultiSelectCtrl().getVisibility() == View.VISIBLE) {
                    if (formControl.getMultiSelectCtrl().getText().toString().trim()
                            .equalsIgnoreCase(AppConstants.DD_SELECT_VALUE)) {
                        isValidationFailed = true;
                        errId = "256";
                    }
                } else {
                    if (value.length() == 0 || formControl.getMultiSelectCtrl().getText().toString().trim()
                            .equalsIgnoreCase(AppConstants.DD_SELECT_VALUE)) {
                        value = "";
                    }
                }
                break;
            case TIME: //Time
                value = formControl.getTextBoxCtrl().getText().toString().trim();

                if (formControl.getTextBoxCtrl().getVisibility() == View.VISIBLE && isRequired) {
                    if (value.length() == 0) {
                        isValidationFailed = true;
                        errId = "256";
                    } else {
                        if (!Utils.timeValidate(value)) {
                            isValidationFailed = true;
                            errMsg = "Invalid" + " " + formControl.getCaptionCtrl().getText().toString();
                        }
                    }
                }

                break;
            case DATETIME: //DateTime
            case DATE: //Date
            case STRING: //Alphanumeric
            case FLOAT: //Number
            case INTEGER: //Integer
                String s = formControl.getKey();
                value = formControl.getTextBoxCtrl().getText().toString().trim();
                //System.out.println("************ Field - "+formControl.getKey()+", Value - "+value+", isRequired - "+isRequired);
                if (formControl.getTextBoxCtrl().getVisibility() == View.VISIBLE && isRequired) {
                    if (value.length() == 0) {
                        isValidationFailed = true;
                        errId = "255";
                    }
                }
                break;
            case AUTOCOMPLETE: //AutoComplete TextView

                //ddVal = formControl.getSelectedVal().get(0);
                ddVal = null;

                if (formControl.getSelectedVal() != null && formControl.getSelectedVal().size() > 0) {
                    ddVal = formControl.getSelectedVal().get(0);
                }


                if (ddVal == null) {
                    value = formControl.getAutoCompleteCtrl().getText().toString().trim();
                } else {
                    value = ddVal.getValue();
                }

                if (formControl.getAutoCompleteCtrl().getVisibility() == View.VISIBLE && isRequired) {
                    if (value.length() == 0) {
                        isValidationFailed = true;
                        errId = "255";
                    }
                }

                break;
            case CHECKBOX: //AutoComplete TextView

                tmpValue = formControl.getCheckBoxControl().isChecked();
                //System.out.println("Checkbox Value - ********************* - "+tmpValue);
                if (formControl.getCheckBoxControl().getVisibility() == View.VISIBLE && isRequired) {
                    if (!tmpValue) {
                        isValidationFailed = true;
                        errId = "255";
                    }
                }

                value = String.valueOf(tmpValue);

                break;
            case TOGGLE: //AutoComplete TextView

                tmpValue = formControl.getTgButtonCtrl().isChecked();

                if (formControl.getTgButtonCtrl().getVisibility() == View.VISIBLE && isRequired) {
                    if (!tmpValue) {
                        isValidationFailed = true;
                        errId = "255";
                    }
                }

                value = String.valueOf(tmpValue);

                break;
            case IMAGE: //Images
                if (formControl.getButtonCtrl() != null) {

                    if (formControl.getImgCounter() < formField.getValidations().getMin()) {
                        isValidationFailed = true;
                        errMsg = Utils.msg(context, "257") + " "
                                + formField.getValidations().getMin() + " "
                                + Utils.msg(context, "258") + " "
                                + formField.getValidations().getMax() + " "
                                + Utils.msg(context, "301");

                    } else {
                        if (formControl.getImgCounter() > 0) {
                            try {
                                value = Constants.gson.toJson(getUploadedDocument(formField.getId()));
                            } catch (Exception exp) {
                                exp.printStackTrace();
                                value = "";
                            }
                        }
                    }
                }

                break;
            case QRIMAGE:
                if (formControl.getButtonCtrl() != null) {
                    try {
                        value = Constants.gson.toJson(getUploadedDocument1(formField.getId()));

                       /* String jsonArray = gson.toJson(list);
                        System.out.println(jsonArray);
                      */

                    } catch (Exception exp) {
                        exp.printStackTrace();
                        value = "";
                    }

                    if (formControl.getButtonCtrl().getVisibility() == View.VISIBLE
                            && isRequired
                            && getUploadedDocument1(formField.getId()).size() == 0) {
                        isValidationFailed = true;
                        errMsg = "Add " + formControl.getCaptionCtrl().getText().toString();
                    }

                }
                break;
            default:
        }

        if (isValidationFailed) {


            if (formControl.getCaptionCtrl() != null) {
                formControl.getCaptionCtrl().clearFocus();
                formControl.getCaptionCtrl().requestFocus();
            }

            if (errMsg == null || errMsg.isEmpty()) {
                errMsg = Utils.msg(context, errId) + " " + formControl.getCaptionCtrl().getText().toString();
            }
            Utils.toastMsg(context, errMsg);
            return false;
        } else {
            if (formField.getValidations() != null && formField.getValidations().getValidateexp() != null) {
                //System.out.println("Inside Java script Validation field name- "+formField.getKey());
                for (JavaScriptExpression expression : formField.getValidations().getValidateexp()) {
                    // System.out.println("Inside Java script Validation - field name- "+formField.getKey()+", Expression - "+expression.getExpression());
                    try {
                        Object result = WorkFlowUtils.evaluateExpression(expression, true);
                        isValidationFailed = org.mozilla.javascript.Context.toBoolean(result);
                        //109
                        if(UIUtils.isMatch && UIUtils.key) {
                            if (formField.getTitle().equalsIgnoreCase("Request Status")) {
                                isValidationFailed = false;
                            }
                        }
                        //109
                        //System.out.println("isValidationFailed===="+isValidationFailed);
                        if (isValidationFailed) {

                            if (formControl.getCaptionCtrl() != null) {
                                formControl.getCaptionCtrl().clearFocus();
                                formControl.getCaptionCtrl().requestFocus();
                            }

                            errMsg = expression.getMsg();
                            errId = expression.getMsgId();

                            if (errMsg == null || errMsg.isEmpty()) {
                                errMsg = Utils.msg(context, errId) + " " + formControl.getCaptionCtrl().getText().toString();
                            }
                            Utils.toastMsg(context, errMsg);
                            return false;
                        }
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }
        }

        if (!isValidationFailed && isFormData) {
            CamundaVariable variable = null;



            if (formField.isPersistent()) {

                if(formField.getKey().equalsIgnoreCase("sid")){
                    int A = 1;
                }

                if(formField.getKey().equalsIgnoreCase("osid")){
                    int A = 1;
                }

                String s = formField.getKey();
                variable = new CamundaVariable();
                variable.setValue(value);
                formData.put(formField.getKey(), variable);

                if(formField.getKey().equalsIgnoreCase("sid")){
                    int A = 1;
                }

                if(formField.getKey().equalsIgnoreCase("osid")){
                    int A = 1;
                }
                //System.out.println("***************************** 111111Key - "+formField.getKey()+", Value - "+value);
                //System.out.println("*****************************");
                if (formField.getPersistentVal() != null) {
                    //String tmp = "";
                    for (Parameter param : formField.getPersistentVal()) {
                        variable = new CamundaVariable();
                        if (ddVal != null) {

                            if (ddVal.getId().equals(AppConstants.DD_SELECT_ID)) {
                                variable.setValue("");
                            } else {
                                variable.setValue(WorkFlowUtils.getSelectedItemVal(ddVal, param.getValKey()));
                            }
                            //System.out.println("***************************** Key - "+param.getKey()+", ValKey - "+param.getValKey()+", Value - "+tmp+", Value 11 - "+ddVal.getVal11());
                        } else {
                            if (formField.getType() == ComponentType.MULTISELECT) {
                                String strVal = "";
                                for (DropdownValue temval : formControl.getSelectedVal()) {
                                    strVal = strVal + "," + WorkFlowUtils.getSelectedItemVal(temval, param.getValKey());
                                }
                                if (strVal != "") {
                                    variable.setValue(strVal.substring(1));
                                } else {
                                    variable.setValue("");
                                }
                            } else {
                                variable.setValue("");
                            }
                        }
                        formData.put(param.getKey(), variable);
                    }
                } else {

                }
            }

            //Generate Audit Trail
            if (isAuditTrail) {

                if (formField.isSeperateAuditTrail()) {
                    if (formField.getType().getValue().equalsIgnoreCase("Select")) {
                        String oldVal = null;
                        if (FormCacheManager.getPrvFormData().containsKey(formField.getKey() + "txt")) {
                            oldVal = (String) FormCacheManager.getPrvFormData().get(formField.getKey() + "txt");
                        }
                        String newVal = ddVal.getValue();

                        if (oldVal == null) {
                            oldVal = "";
                        }
                        if (!value.equalsIgnoreCase(oldVal)) {
                            auditTrail.add(formField.getTitle() + " modified: Old Value - " + oldVal + ",New Value - " + newVal);
                        }

                    } else {
                        String oldVal = (String) FormCacheManager.getPrvFormData().get(formField.getKey());
                        String newVal = value;
                        //System.out.println("Field - "+formField.getKey() +", Old Value - "+oldVal+", New Value - "+newVal);

                        if (oldVal == null) {
                            oldVal = "";
                        }
                        if (!value.equalsIgnoreCase(oldVal)) {
                            auditTrail.add(formField.getTitle() + " modified: Old Value - " + oldVal + ",New Value - " + newVal);
                        }
                    }


                } else if (formField.isAuditTrail()) {

                    if (operation.equalsIgnoreCase("E")) {
                        if (FormCacheManager.getPrvFormData().containsKey(formField.getKey())) {

                            String oldVal = (String) FormCacheManager.getPrvFormData().get(formField.getKey());

                            if (oldVal == null) {
                                oldVal = "";
                            }

                            String newVal = value;
                            if (!value.equalsIgnoreCase(oldVal)) {
                                auditTrailObj.append(formField.getTitle())
                                        .append(" modified: ");

                                switch (formField.getType()) {
                                    case SELECT:
                                        if (FormCacheManager.getPrvFormData().containsKey(formField.getKey() + "txt")) {
                                            oldVal = (String) FormCacheManager.getPrvFormData().get(formField.getKey() + "txt");
                                        }
                                        newVal = ddVal.getValue();

                                        break;
                                    case MULTISELECT:
                                        if (FormCacheManager.getPrvFormData().containsKey(formField.getKey() + "txt")) {
                                            oldVal = (String) FormCacheManager.getPrvFormData().get(formField.getKey() + "txt");
                                        }

                                        String tempAudit = "";
                                        for (DropdownValue tempval : formControl.getSelectedVal()) {
                                            tempAudit = tempAudit + "," + tempval.getValue();
                                        }
                                        if (tempAudit != null && tempAudit.length() > 0) {
                                            tempAudit = tempAudit.substring(1);
                                        }
                                        newVal = tempAudit;
                                        break;
                                    default:
                                        break;
                                }

                                if (oldVal == null && oldVal.length() == 0) {
                                    auditTrailObj.append(", New Value - ")
                                            .append(newVal);
                                } else {
                                    auditTrailObj.append("Old Value - ")
                                            .append(oldVal)
                                            .append(", New Value - ")
                                            .append(newVal);
                                }
                                auditTrailObj.append(", ");
                            }

                        }
                    }
                }
            }
        }

        return true;
    }

    public static void FackApp(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Uninstall " + FormCacheManager.getAppPreferences().getAppNameMockLocation() + " app/Remove Fack Location  in your mobile handset.");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static List<UploadDocDetail> getUploadedDocument(String fieldId) {

        ImageAdapter imageAdapter = (ImageAdapter) FormCacheManager.getFormControls().get(fieldId).getImgGridView().getAdapter();
        List<UploadDocDetail> docList = new ArrayList<UploadDocDetail>();

        return imageAdapter.imageList;
    }

    private static void getAssestQrCode(String fieldId) {

        String assestData = "";
        GridAdapter imageAdapter1 = (GridAdapter) FormCacheManager.getFormControls().get(fieldId).getImgGridView().getAdapter();

        if (imageAdapter1 != null && imageAdapter1.imageList != null &&
                imageAdapter1.imageList.size() > 0) {
            for (int i = 0; i < imageAdapter1.imageList.size(); i++) {
                assestData = assestData + "" + imageAdapter1.imageList.get(i).getAssestListName() + ",";
            }
            if (assestData.length() > 0) {
                assestData = assestData.substring(0,
                        assestData.length() - 1);
            }

            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("assetqrcode")) {
                Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("assetqrcode");
                FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().setText(assestData);
            }

        }

    }


    private static List<UploadAssestDetail> getUploadedDocument1(String fieldId) {
        GridAdapter imageAdapter1 = (GridAdapter) FormCacheManager.getFormControls().get(fieldId).getImgGridView().getAdapter();
        return imageAdapter1.imageList;
    }

    public static void dataSubmitted(Context context, String result, String langCode) {
        Gson gson = new Gson();
        Response response;
        String impLoginId = (String) FormCacheManager.getPrvFormData().get(Constants.IMP_LOGIN_ID);
        String implementer = (String) FormCacheManager.getPrvFormData().get(Constants.IMPLEMENTER);
        String siteId = (String) FormCacheManager.getPrvFormData().get(Constants.SITE_ID);

        //callGetApiResponceOwner(context,impLoginId,siteId);

        try {
            response = gson.fromJson(result, Response.class);
        } catch (Exception e) {
            response = null;
        }
        if (response != null) {
            UIUtils.toastMsg(context, response.getMessage(), langCode);
            if (response.getSuccess() != null) {
                if (response.getSuccess().equals("true")) {
                    if (AppConstants.moduleList.get(FormCacheManager.getAppPreferences().getModuleIndex())
                            .getModuleId() == HsseConstant.HSSE_MODULE_ID) {
                        DataBaseHelper dbHelper = new DataBaseHelper(context);
                        dbHelper.open();
                        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight(FormCacheManager.getAppPreferences().getModuleName());
                        dbHelper.close();
                        String editRight = "";
                        for (MenuDetail menu : subMenuList) {
                            switch (menu.getName()) {
                                case "Add Request Tab":
                                    editRight = menu.getRights().toString();
                                    break;
                            }
                        }

                        HashMap<String, Object> tranData = new HashMap<String, Object>();
                        tranData.put(Constants.NEXT_TAB_SELECT, response.getNexttab());
                        tranData.put(Constants.TASK_ID, response.getTicketId());
                        tranData.put(Constants.PROCESS_INSTANCE_ID, response.getInstanceId());
                        tranData.put(HsseConstant.OLD_TKT_STATUS, response.getOldTktStatus());
                        tranData.put(HsseConstant.OLD_GRP, response.getOassigntogrp());
                        tranData.put(Constants.OPERATION, "E");
                        tranData.put(Constants.EDIT_RIGHTS, editRight);
                        tranData.put(Constants.FORM_KEY, "EditHSSERequest");
                        Intent i = new Intent(context, HsseFrame.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(AppConstants.TRAN_DATA_MAP_ALIAS, tranData);
                        ((Activity) context).startActivity(i);
                        ((Activity) context).finish();
                    } else {
                        if (source.equalsIgnoreCase("AddRequest")) {
                            Intent i = new Intent(context, WorkflowImpl.class);
                            ((Activity) context).startActivity(i);
                            ((Activity) context).finish();
                        } else {
                            ((Activity) context).finish();
                        }
                    }
                }
            }
        } else {
            //Server Not Available
            Utils.toast(context, "13");
        }
    }

    private static boolean validateGPS(Context context) {

        GPSTracker gps = new GPSTracker(context);
        //First Validation for GPS OFF and Location Permission Denied
        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
            return false;
        } else if (gps.isMockLocation() == true) {
            FackApp(context);
            return false;
        } else if (Utils.isAutoDateTime(context)) {
            Utils.autoDateTimeSettingsAlert(context);
            return false;
        } else if (gps.canGetLocation() == true) {

            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());

            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                    || (longitude == null || longitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                Utils.toast(context, "252");
                return false;
            } else {
                latitude = String.valueOf(gps.getLatitude());
                longitude = String.valueOf(gps.getLongitude());
            }

        } else if (!Utils.hasPermissions(context, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(context, "Permission denied for device's location.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public static void confirmationMessage(Context ctx, OnChangeDetail onChangeDetail,
                                           final String id, final DropdownValue selectedVal) {

        String msg = null;
        //Validate whether to show confirmation message or not.
        if (onChangeDetail.getMessage().getConfirmexp() != null) {
            if (onChangeDetail.getMessage().getConfirmexp().getExpression() != null) {
                Object result =WorkFlowUtils.evaluateExpression(onChangeDetail.getMessage().getConfirmexp(), false);
                //System.out.println("Confirm Expression is - "+org.mozilla.javascript.Context.toBoolean(result));
                if (org.mozilla.javascript.Context.toBoolean(result)) {

                    if (onChangeDetail.getMessage().getConfirmexp().getMsgexp() != null) {
                        //System.out.println("Confirm Expression msg exp - "+onChangeDetail.getMessage().getConfirmexp().getMsgexp());
                        result = WorkFlowUtils.evaluateExpression(onChangeDetail.getMessage().getConfirmexp().getMsgexp(), false);
                        msg = org.mozilla.javascript.Context.toString(result);
                        //System.out.println("Confirm Expression is msg exp msg - "+org.mozilla.javascript.Context.toBoolean(result));
                    } else {
                        msg = onChangeDetail.getMessage().getConfirmexp().getMsg();
                    }
                } else {
                    onChangeTask(ctx, true, id, selectedVal);
                    return;
                }
            }
        } else {
            msg = onChangeDetail.getMessage().getMsg();
        }
        //System.out.println("Confirm Expression is msg exp msg -1212122 "+msg);
        if (msg == null && onChangeDetail.getMessage().getMsg() != null) {
            msg = onChangeDetail.getMessage().getMsg();
        }

        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(ctx, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(
                R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(ctx));
        positive.setTypeface(Utils.typeFace(ctx));
        positive.setText(Utils.msg(ctx, "7"));
        negative.setTypeface(Utils.typeFace(ctx));
        negative.setText(Utils.msg(ctx, "8"));
        title.setTypeface(Utils.typeFace(ctx));
        //title.setGravity(Gravity.LEFT);
        title.setText(msg);
        title.setGravity(Gravity.LEFT);
        positive.setText(onChangeDetail.getMessage().getPositiveText());

        if (onChangeDetail.getMessage().getNegativeText() != null) {
            negative.setText(onChangeDetail.getMessage().getNegativeText());
        } else {
            negative.setVisibility(View.INVISIBLE);
        }

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(id.equalsIgnoreCase("204") && checkUser(ctx) )
                {
                    actvity_dialog.cancel();
                    boolean value = FormCacheManager.getFormControls().get(id).getTgButtonCtrl().isChecked();
                    dialog_prepost_checklist(ctx,value,id,selectedVal);
                }
                else {
                    actvity_dialog.cancel();
                    onChangeTask(ctx, true, id, selectedVal);
                }

            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                onChangeTask(ctx, false, id, selectedVal);
            }
        });
    }

    public static boolean checkUser(Context context)
    {
        String id = Utils.msg(context, "910");
        String[] group_id = new AppPreferences(context).getUserGroup().split(",");
        for(int i = 0 ; i<group_id.length;i++)
        {
            if(id.equals(group_id[i]))
            {
                return true;
            }
        }
        return false;
    }

    public static void dialog_prepost_checklist(Context context, boolean tvalue, String id, DropdownValue selectedVal)
    {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait...");
        WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(context);
        db.open();
        mcontext = context;
        imagesList = new ArrayList<>();
        imagesList1 = new ArrayList<>();
        imagesList.add(new ImagesList("","","","","","","","Site General View*",null,null));
        imagesList.add(new ImagesList("","","","","","","","General View for the TowerSide1*",null,null));
        imagesList.add(new ImagesList("","","","","","","","General View for the TowerSide2*",null,null));
        imagesList.add(new ImagesList("","","","","","","","General View for the TowerSide3*",null,null));
        imagesList.add(new ImagesList("","","","","","","","General View for the TowerSide4*",null,null));
        imagesList.add(new ImagesList("","","","","","","","Fence and Door Status*",null,null));
        imagesList.add(new ImagesList("","","","","","","","Shelter General View*",null,null));
        imagesList.add(new ImagesList("","","","","","","","Inside shelter*",null,null));
        imagesList.add(new ImagesList("","","","","","","","Generator",null,null));
        imagesList.add(new ImagesList("","","","","","","","SCECO",null,null));

        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(context, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(
                R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.dialog_pre_checklist);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.setCancelable(false);
        actvity_dialog.show();
        TextView txtbiew_status = actvity_dialog.findViewById(R.id.txtbiew_status);
        TextView txtview_wordonestatus = actvity_dialog.findViewById(R.id.txtview_wordonestatus);
        RecyclerView recyclerview_checklist = actvity_dialog.findViewById(R.id.recyclerview_checklist);
        Spinner sp_workdone = actvity_dialog.findViewById(R.id.sp_workdone);
        RecyclerView recyclerview_workdone = actvity_dialog.findViewById(R.id.recyclerview_workdone);
        TextView txtview_btnupload = actvity_dialog.findViewById(R.id.btn_upload);
        TextView btn_close = actvity_dialog.findViewById(R.id.btn_close);
        EditText et_reject_remarks = actvity_dialog.findViewById(R.id.et_reject_remarks);
        if(tvalue)
        {
            txtbiew_status.setText("Before");
            txtview_wordonestatus.setText("Before");
        }
        else
        {
            txtbiew_status.setText("After");
            txtview_wordonestatus.setText("After");
        }
        ArrayList<String> list = db.getPrePostAM();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mcontext,
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        sp_workdone.setAdapter(dataAdapter);
        sp_workdone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                imagesList1.clear();
             if(sp_workdone.getSelectedItemPosition()!=0)
             {
                 recyclerview_workdone.setVisibility(View.VISIBLE);
                 int value = Integer.parseInt(sp_workdone.getSelectedItem().toString());
              for(int j= 0;j<value;j++)
              {
                  imagesList1.add(new ImagesList("","","","","","","","Image"+String.valueOf(j+1),null,null));
              }
                 preChecklistAdapter1 = new PreChecklistAdapter(context,imagesList1,"109","107");
                 LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                 recyclerview_workdone.setAdapter(preChecklistAdapter1);
                 recyclerview_workdone.setLayoutManager(layoutManager);
             }
             else {
                 recyclerview_workdone.setVisibility(View.GONE);
             }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        preChecklistAdapter = new PreChecklistAdapter(context,imagesList,"108","110");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerview_checklist.setAdapter(preChecklistAdapter);
        recyclerview_checklist.setLayoutManager(layoutManager);

        txtview_btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String opr = "";
                if(tvalue)
                {
                 opr = "Pre" ;
                }
                else
                {
                    opr = "Post";
                }
               // String opr = (String) FormCacheManager.getPrvFormData().get(Constants.OPERATION);
                String txnId = (String) FormCacheManager.getPrvFormData().get(Constants.TXN_ID);
                String userId = new AppPreferences(context).getUserId();
                String workdoneid = db.getPrePostAMId(sp_workdone.getSelectedItem().toString());
               if(checkImageValidaton(imagesList,imagesList1))
               {
                   if(sp_workdone.getSelectedItemPosition()!=0) {
                       if (et_reject_remarks != null && !et_reject_remarks.getText().toString().isEmpty()) {
                           dialog.show();
                           saveAccessChecklist(txnId, et_reject_remarks.getText().toString(), opr, userId,context,actvity_dialog,workdoneid,selectedVal,id,dialog);
                       } else {
                           Toast.makeText(context, "Please add remarks", Toast.LENGTH_SHORT).show();
                       }
                   }
                   else
                   {
                       Toast.makeText(context, "Please select work done value", Toast.LENGTH_SHORT).show();
                   }
               }
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvity_dialog.dismiss();
                onChangeTask(context, false, id, selectedVal);
            }
        });

    }

    public static boolean checkImageValidaton(ArrayList<ImagesList>list,ArrayList<ImagesList>list1)
    {
      for(int i = 0;i<list.size()-2;i++)
      {
          if(list.get(i).getImageUri() == null)
          {
              Toast.makeText(mcontext,"Please add all mandatory images of whole site",Toast.LENGTH_SHORT).show();
              return false;
          }
      }
      if(list1.size()>0) {
          for (int j = 0; j < list1.size(); j++) {
              if (list1.get(j).getImageUri() == null) {
                  Toast.makeText(mcontext, "Please add all  images of work done", Toast.LENGTH_SHORT).show();
                  return false;
              }
          }
      }
      return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            if (requestCode == 108) {
                Bitmap photo = null;
                GPSTracker tracker = new GPSTracker(mcontext);
                Uri selectedFileURI = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), selectedFileURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagesList.get(position).setImageUri(selectedFileURI);
                imagesList.get(position).setName(getFileName(selectedFileURI));
                imagesList.get(position).setType(getfileExtension(selectedFileURI));
                imagesList.get(position).setTime(DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss"));
                imagesList.get(position).setLatitude(String.valueOf(tracker.getLatitude()));
                imagesList.get(position).setLongitude(String.valueOf(tracker.getLongitude()));
                imagesList.get(position).setBitmap(photo);
                preChecklistAdapter.notifyDataSetChanged();
            } else if (requestCode == 109) {
                Bitmap photo = null;
                GPSTracker tracker = new GPSTracker(mcontext);
                Uri selectedFileURI = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), selectedFileURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagesList1.get(position).setImageUri(selectedFileURI);
                imagesList1.get(position).setName(getFileName(selectedFileURI));
                imagesList1.get(position).setType(getfileExtension(selectedFileURI));
                imagesList1.get(position).setTime(DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss"));
                imagesList1.get(position).setLatitude(String.valueOf(tracker.getLatitude()));
                imagesList1.get(position).setLongitude(String.valueOf(tracker.getLongitude()));
                imagesList1.get(position).setBitmap(photo);
                preChecklistAdapter1.notifyDataSetChanged();
            } else if (requestCode == 110) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                GPSTracker tracker = new GPSTracker(mcontext);
                Uri selectedFileURI = getImageUri1(mcontext, photo);
                imagesList.get(position).setImageUri(selectedFileURI);
                imagesList.get(position).setName(getFileName(selectedFileURI));
                imagesList.get(position).setType(getfileExtension(selectedFileURI));
                imagesList.get(position).setTime(DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss"));
                imagesList.get(position).setLatitude(String.valueOf(tracker.getLatitude()));
                imagesList.get(position).setLongitude(String.valueOf(tracker.getLongitude()));
                imagesList.get(position).setBitmap(photo);
                preChecklistAdapter.notifyDataSetChanged();
            } else if (requestCode == 107) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                GPSTracker tracker = new GPSTracker(mcontext);
                Uri selectedFileURI = getImageUri1(mcontext, photo);
                imagesList1.get(position).setImageUri(selectedFileURI);
                imagesList1.get(position).setName(getFileName(selectedFileURI));
                imagesList1.get(position).setType(getfileExtension(selectedFileURI));
                imagesList1.get(position).setTime(DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss"));
                imagesList1.get(position).setLatitude(String.valueOf(tracker.getLatitude()));
                imagesList1.get(position).setLongitude(String.valueOf(tracker.getLongitude()));
                imagesList1.get(position).setBitmap(photo);
                preChecklistAdapter1.notifyDataSetChanged();
            }
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Uri getImageUri1(Context context, Bitmap bitmap) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Title_" + System.currentTimeMillis() + ".jpg"); // Unique filename
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); // Save to Pictures directory

        Uri uri = null;
        OutputStream outputStream = null;

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                outputStream = resolver.openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } else {
                    // Clean up if OutputStream is null
                    resolver.delete(uri, null, null);
                    uri = null;
                }
            } else {
                Log.e("ImageUri", "Failed to insert image into MediaStore.");
            }
        } catch (IOException e) {
            Log.e("ImageUri", "IOException: " + e.getMessage());
            if (uri != null) {
                resolver.delete(uri, null, null); // Clean up if an exception occurred
            }
            uri = null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e("ImageUri", "IOException during closing OutputStream: " + e.getMessage());
                }
            }
        }

        return uri;
    }



    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mcontext.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = mcontext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    public static void onChangeTask(Context context, boolean response, String id,
                                    DropdownValue selectedValue) {

        Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(id).getKey());

        //In Case -Ve reset value of particular field
        if (!response) {

            if (ComponentType.SELECT == formControl.getType()) {
                DropdownValue previousVal = FormCacheManager.getFormControls().get(id).getSelectedVal().get(0);

                FormFieldControl control = FormCacheManager.getFormControls().get(id);

                int count = control.getSelectCtrl().getAdapter().getCount();
                DropdownValue ddValue;

                for (int i = 0; i < count; i++) {
                    ddValue = (DropdownValue) control.getSelectCtrl().getAdapter().getItem(i);
                    if (ddValue.getId().equalsIgnoreCase(previousVal.getId())) {
                        control.getSelectCtrl().setSelection(i, false);
                        control.getSelectCtrl().setTag("" + i);
                        break;
                    }
                }
            } else if (ComponentType.AUTOCOMPLETE == formControl.getType()) {
                DropdownValue previousVal = FormCacheManager.getFormControls().get(id).getSelectedVal().get(0);
                FormCacheManager.getFormControls().get(id).getAutoCompleteCtrl().setText(previousVal.getValue());
                if (formControl.getOnChange() != null && formControl.getOnChange().getClear() != null) {

                    for (String key : formControl.getOnChange().getClear()) {
                        //System.out.println("********************** key - "+key);
                        FormFieldControl control = FormCacheManager.getFormControls().get(FormCacheManager.getFormConfiguration().getFormFields().get(key).getId());
                        //System.out.println("********************** key - "+control.getValue());
                        control.getTextBoxCtrl().setText(control.getValue());
                    }
                }
            } else if (ComponentType.TOGGLE == formControl.getType()) {
                boolean value = FormCacheManager.getFormControls().get(id).getTgButtonCtrl().isChecked();
                if (value) {
                    FormCacheManager.getFormControls().get(id).getTgButtonCtrl().setChecked(false);
                } else {
                    FormCacheManager.getFormControls().get(id).getTgButtonCtrl().setChecked(true);
                }
            }
            return;
        }


        //Set selected values
        switch (formControl.getType()) {
            case AUTOCOMPLETE:
            case SELECT:
                if (selectedValue != null) {
                    FormCacheManager.getFormControls().get(id).getSelectedVal().add(0, selectedValue);
                }
                break;
            case TOGGLE:
                boolean value = FormCacheManager.getFormControls().get(id).getTgButtonCtrl().isChecked();
                if (value) {
                    if (formControl.getOntitle() != null) {
                        FormCacheManager.getFormControls().get(id).getTgButtonCtrl().setText(formControl.getOntitle());
                    } else {
                        FormCacheManager.getFormControls().get(id).getTgButtonCtrl().setText(formControl.getTitle());
                    }

                } else {
                    if (formControl.getOfftitle() != null) {
                        FormCacheManager.getFormControls().get(id).getTgButtonCtrl().setText(formControl.getOfftitle());
                    } else {
                        FormCacheManager.getFormControls().get(id).getTgButtonCtrl().setText(formControl.getTitle());
                    }
                }
            default:
                break;
        }

        OnChangeDetail onChangeDetail = null;
        boolean isClick = false;

        if (formControl.getOnChange() != null) {
            onChangeDetail = formControl.getOnChange();
        }

        if (formControl.getOnClick() != null) {
            onChangeDetail = formControl.getOnClick();
            isClick = true;
        }

        if (onChangeDetail != null) {

            if (onChangeDetail.getReset() != null) {
                WorkFlowUtils.resetDependentFields(context, formControl, selectedValue);
            }

            //Check which all form field need to refresh
            if (onChangeDetail.getRefresh() != null && onChangeDetail.getRefresh().size() > 0) {
                WorkFlowUtils.refreshDependentFields(context, formControl, isClick);
            }

            if (onChangeDetail.getShow() != null) {
                WorkFlowUtils.showDependentFields(context, formControl, isClick);
            }

            //Check which all form field need to hide
            if (onChangeDetail.getHide() != null) {
                WorkFlowUtils.hideDependentFields(context, formControl, isClick);
            }

            //Check which all form field need to hide
            if (onChangeDetail.getEnable() != null) {
                WorkFlowUtils.enableDependentFields(context, formControl, isClick);
            }

            //Check which all form field need to hide
            if (onChangeDetail.getDisable() != null) {
                WorkFlowUtils.disableDependentFields(context, formControl, isClick);
            }

               /* //Check which all form field need to refresh
                if (formControl.getOnChange().getRefresh() != null && formControl.getOnChange().getRefresh().size() > 0) {
                    refreshDependentFeilds(formControl);
                }
                */
            if (onChangeDetail.getServicecall() != null && onChangeDetail.getServicecall().size() > 0) {

                for (ServiceDetail serviceDetail : onChangeDetail.getServicecall()) {

                    if (serviceDetail.getExpression() != null) {
                        Object result = WorkFlowUtils.evaluateExpression(serviceDetail.getExpression(), false);
                        if (!org.mozilla.javascript.Context.toBoolean(result)) {
                            continue;
                        }
                    }
                    DataSubmitUtils.callService(context, formControl.getKey(), serviceDetail);

                    // callGetApiResponceOwner(context,impLoginId,SiteID);

                }
            }
        }
    }


    private static void callGetAccessTokenForSbmit(Context context, String keySerails) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<AccessTokenResponce> call = iApiRequest.genrateAccessToken(Utils.msg(context, "844"));
        call.enqueue(new Callback<AccessTokenResponce>() {
            @Override
            public void onResponse(Call<AccessTokenResponce> call, retrofit2.Response<AccessTokenResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    // Handle successful response
                    // Process the response body
                    String token = response.body().getTokenType() + " " + response.body().getAccessToken();
                    callGetSearchKeysubmit(context, keySerails, token);
                } else {
                    Utils.toastMsg(context, Utils.msg(context, "841"));
                }

            }

            @Override
            public void onFailure(Call<AccessTokenResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private static void callGetSearchKeysubmit(Context context, String making, String Accesstoken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.msg(context, "841"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyString = "{\n\t\"searchKeys\": {\n\t\t\"keySearchArguments\": " +
                "{\n\t\t\t\"marking\": \"" + making + "\"\n\t\t},\n\t\t\"pagination\":" +
                " {\n\t\t\t\"firstResult\": \"0\",\n\t\t\t\"maxResults\": \"10\"\n\t\t}\n\t}\n}";
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyString);

        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<GetSerachKeyResponse> call = iApiRequest.getSearchkeysMaking(requestBody, Accesstoken);
        call.enqueue(new Callback<GetSerachKeyResponse>() {
            @Override
            public void onResponse(Call<GetSerachKeyResponse> call, retrofit2.Response<GetSerachKeyResponse> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //  Utils.toastMsg(context, "" + response.code());
                if (response.isSuccessful()) {
                    // Handle successful response
                    if (response.body() != null) {
                        if (response.body().getSearchKeysResponse().getSKey() != null) {
                            response.body().getSearchKeysResponse().getSKey().getIdentity();
                            callHandilekey(context, response.body().getSearchKeysResponse().getSKey().getIdentity(), Accesstoken);
                        } else {
                            Utils.toastMsg(context, Utils.msg(context, "845"));
                        }
                    }
                } else {
                    // Handle error response
                    // You can check response.code() and response.message() for details
                }


            }

            @Override
            public void onFailure(Call<GetSerachKeyResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private static void callHandilekey(Context context, String KeyIdentty, String AccessToken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.msg(context, "841"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getHandileKey(KeyIdentty, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    // Process the successful response
                    if (response.code() == 200) {
                        String key = "";
                        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                            key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
                        }

                        String msg = Utils.msg(context,"856")+" " + key +
                                " "+Utils.msg(context,"854")+" "+KeyIdentty+" "+Utils.msg(context,"857");
                        showPopUPNotification(msg,context);
                        callGetUpdateKeyCylinderSbmit(context, KeyIdentty, null, AccessToken);
                    }
                    // Do something with the response body
                } else {
                    Utils.toastMsg(context,Utils.msg(context, "850"));
                }


            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private static void callGetUpdateKeyCylinderSbmit(Context context, String KeyIdentty, String plugIdenty, String AccessToken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        //Toast.makeText(context, "abloyID "+abloyID, Toast.LENGTH_SHORT).show();
        MediaType mediaType = MediaType.parse("application/json");


        // Create the request body using a Map for dynamic properties
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> updateKeyCylinderAuthorisations = new HashMap<>();
        updateKeyCylinderAuthorisations.put("keyIdentity", KeyIdentty);

        List<String> cylinderPlugIdentities = new ArrayList<>();

       /* String [] arrayStr=plugIdenty.split(",");
        JSONArray mJSONArray = new JSONArray();
        for (String s: arrayStr){
            mJSONArray.put(s);
            cylinderPlugIdentities.add(s);
        }*/

        Map<String, Object> cylinderPlugIdentitiesMap = new HashMap<>();
        cylinderPlugIdentitiesMap.put("cylinderPlugIdentity", cylinderPlugIdentities);

        updateKeyCylinderAuthorisations.put("cylinderPlugIdentities", cylinderPlugIdentitiesMap);
        requestBody.put("updateKeyCylinderAuthorisations", updateKeyCylinderAuthorisations);

        RequestBody requestBodyString = RequestBody.create(mediaType, new Gson().toJson(requestBody));


        //Log.d("AviR",  mJSONArray.toString());
        Log.d("AviR", requestBodyString.toString());
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getUpdateKeyCylinder(requestBodyString, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {

                } else {
                    Utils.toastMsg(context, Utils.msg(context, "849"));
                }

            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private static void showPopUPNotification(String msg, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle("Confirmation Message !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void saveAccessChecklist(String txnId, String remarks, String oper, String loginId, Context context, Dialog actvity_dialog, String workdoneid, DropdownValue selectedVal, String id, ProgressDialog dialog)
    {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("txnId",txnId);
        jsonObject.addProperty("remarks",remarks);
        jsonObject.addProperty("oper",oper);
        jsonObject.addProperty("loginID",loginId);
        jsonObject.addProperty("workDone",workdoneid);
        JsonArray jsonArray = new JsonArray();
        JsonArray jsonArray1 = new JsonArray();
        for(int i = 0;i<imagesList.size();i++)
        {
            String path = "";
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("latitude",imagesList.get(i).getLatitude());
            jsonObject1.addProperty("longitude",imagesList.get(i).getLongitude());
            jsonObject1.addProperty("name",imagesList.get(i).getName());
            if(imagesList.get(i).getBitmap() != null)
            {
               path = getBase64Image(mcontext,imagesList.get(i).getBitmap());
            }
            else
            {
                path = "";
            }
            jsonObject1.addProperty("path",path);
            jsonObject1.addProperty("type",imagesList.get(i).getType());
            jsonObject1.addProperty("tag",imagesList.get(i).getType());
            jsonObject1.addProperty("time",imagesList.get(i).getTime());
            jsonObject1.addProperty("filedkey",imagesList.get(i).getFiledkey());
            jsonArray.add(jsonObject1);
        }

        for(int i = 0;i<imagesList1.size();i++)
        {
            String path = "";
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("latitude",imagesList1.get(i).getLatitude());
            jsonObject1.addProperty("longitude",imagesList1.get(i).getLongitude());
            jsonObject1.addProperty("name",imagesList1.get(i).getName());
            if(imagesList1.get(i).getBitmap() != null)
            {
                path = getBase64Image(mcontext,imagesList1.get(i).getBitmap());
            }
            else
            {
                path = "";
            }
            jsonObject1.addProperty("path",path);
            jsonObject1.addProperty("type",imagesList1.get(i).getType());
            jsonObject1.addProperty("tag",imagesList1.get(i).getType());
            jsonObject1.addProperty("time",imagesList1.get(i).getTime());
            jsonObject1.addProperty("filedkey",imagesList1.get(i).getFiledkey());
            jsonArray1.add(jsonObject1);
        }
        jsonObject.add("imgData",jsonArray);
        jsonObject.add("imgDataWorkDone",jsonArray1);
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<ResponseBody> call = request.saveAccessChecklist(jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        String response1 = response.body().string();
                        JSONObject object = new JSONObject(response1);
                        String flag = object.getString("Flag");
                        String message = object.getString("Message");
                        if(message.equalsIgnoreCase("Success"));
                        {
                            actvity_dialog.dismiss();
                            onChangeTask(context, true, id, selectedVal);
                            Toast.makeText(context, "Images upload successfully!!", Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (IOException e) {
                    Log.d("TAG",e.getMessage().toString());
                    e.printStackTrace();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Log.d("TAG",t.getMessage().toString());
            }
        });

    }

    public static String getBase64Image(Context inContext, Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();

        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }



}
