package com.isl.workflow.form;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.constant.AppConstants;
import com.isl.dao.cache.AppPreferences;
import com.isl.hsse.HsseConstant;
import com.isl.modal.SiteLockResponce;
import com.isl.util.FontUtils;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.WorkflowImpl;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.form.control.AutoCompleteTextBocControl;
import com.isl.workflow.form.control.ButtonControl;
import com.isl.workflow.form.control.CheckBoxControl;
import com.isl.workflow.form.control.ImageControl;
import com.isl.workflow.form.control.MultiSelectControl;
import com.isl.workflow.form.control.QRImageControl;
import com.isl.workflow.form.control.SelectControl;
import com.isl.workflow.form.control.TextBoxControl;
import com.isl.workflow.form.control.ToggleControl;
import com.isl.workflow.modal.CamundaVariable;
import com.isl.workflow.modal.Component;
import com.isl.workflow.modal.ComponentType;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.FormObject;
import com.isl.workflow.modal.responce.AccessTokenResponce;
import com.isl.workflow.modal.responce.GetSerachKeyResponse;
import com.isl.workflow.modal.responce.KeyDetailsResponce;
import com.isl.workflow.tabs.RequestReport;
import com.isl.workflow.utils.DataSubmitUtils;
import com.isl.workflow.utils.UIUtils;
import com.isl.workflow.utils.WorkFlowUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import infozech.itower.R;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created By - Manoj Yadav on 23rd Jun
 * Purpose - To Render Form using JSON form configuration
 */

public class WorkFlowForm extends Fragment implements View.OnClickListener {

    private View view;
    private AppPreferences mAppPreferences;
    private LinearLayout linear;
    private Button btBack;
    private LinearLayout.LayoutParams tvParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private WorkFlowDatabaseHelper dbHelper;
    private boolean isFormInitialized = false, isDataFetched = false;
    private String source, operation;
    private FormObject formObject = null;
    String status = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.workflow_form, container, false);
        RelativeLayout rl_header_ticket_list = (RelativeLayout) view.findViewById(R.id.rl_header_ticket_list);
        rl_header_ticket_list.setVisibility(View.GONE);
        operation = "A";
        initialize();

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonAlert("291", "63", "64");

            }
        });

        String formName = (String) FormCacheManager.getPrvFormData().get("formKey");
        if (operation.equals("E")) {
            if (Utils.isNetworkAvailable(getActivity())) {
                try {
                    new GetTransactionDetail(getActivity(), formName).execute().get();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                backButton();
            }
        } else {
            renderForm(formName);
        }
        return view;
    }

    private void renderForm(String formName) {

        System.out.println("************************************************");
        System.out.println("Form Name - " + formName);
        System.out.println("************************************************");

        dbHelper = new WorkFlowDatabaseHelper(getActivity());
        dbHelper.open();

        boolean isFormModified = dbHelper.isFormConfigurationModified(formName);
        dbHelper.close();

        if (isFormModified) {
            if (Utils.isNetworkAvailable(getActivity())) {
                new GetForm(getActivity(), formName).execute();
            } else {
                Toast.makeText(getActivity(), "No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                InitializeWorkFlowForm(formName);
            }
        } else {
            InitializeWorkFlowForm(formName);
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getActivity(), "edit click", Toast.LENGTH_LONG).show();
    }

    private void initialize() {
        //lhmImages.clear();
        mAppPreferences = new AppPreferences(getActivity());
        HashMap<String, String> tranData = (HashMap<String, String>) getActivity().getIntent().getSerializableExtra(AppConstants.TRAN_DATA_MAP_ALIAS);
        FormCacheManager.getPrvFormData().clear();
        FormCacheManager.getPrvFormData().putAll(tranData);
        source = (String) FormCacheManager.getPrvFormData().get(Constants.TXN_SOURCE);
        operation = (String) FormCacheManager.getPrvFormData().get(Constants.OPERATION);
        linear = (LinearLayout) view.findViewById(R.id.ll_textview);
        btBack = (Button) view.findViewById(R.id.bt_back);
    }

    public void InitializeWorkFlowForm(String formName) {

        WorkFlowDatabaseHelper dbHelper = new WorkFlowDatabaseHelper(getActivity());
        dbHelper.open();
        dbHelper.clearImage();
        String fromConfiguration = dbHelper.getWorkFlowForm(formName);

        if (fromConfiguration != null) {
            Gson gsonObj = new Gson();
            //FormObject formObject = null;
            try {
                formObject = gsonObj.fromJson(fromConfiguration, FormObject.class);
                if (formObject != null && formObject.getComponents() != null && formObject.getComponents().size() > 0) {
                    FormCacheManager.setFormConfiguration(formObject);
                } else {
                    Toast.makeText(getActivity(), "Invalid Form Configuration.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    backButton();
                }
            } catch (Exception exp) {
                exp.printStackTrace();
                return;
            }

            tvParam.setMargins(10, 20, 10, 0);
            System.out.println("***************isDataFetched - " + isDataFetched);
            System.out.println("***************isFormInitialized - " + isFormInitialized);

            isFormInitialized = true;

            drawComponent(formObject.getComponents());
            initializeFormField();
        }
    }

    private void drawComponent(List<Component> componentList) {

        int index = 0;
        for (Component component : componentList) {
            switch (component.getType()) {
                case PANEL:
                    index = drawPanel(component, index);
                    break;
                case FLOAT:
                case INTEGER:
                case STRING:
                case SELECT:
                case MULTISELECT:
                case DATE:
                case TIME:
                case IMAGE:
                case QRIMAGE:
                    break;
                default:
                    break;
            }


            if (component.getFields() == null || component.getFields().size() == 0) {
                return;
            } else {
                index = drawFields(component, index);
            }
        }
    }

    private int drawPanel(Component component, int index) {
        try {
            TextView tvGroupName = new TextView(getActivity());
            tvGroupName.setId(component.getId());
            linear.addView(Utils.groupTV(getActivity(), tvGroupName, component.getTitle()), index);
            index++;
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return index;

    }

    private int drawFields(Component component, int index) {

        TextView tvFieldCaption = null;
        TextBoxControl textBoxControl = null;
        MultiSelectControl multiSelectControl = null;
        SelectControl selectControl = null;
        AutoCompleteTextBocControl autoCompleteTextBocControl = null;
        ImageControl imgControl = null;
        QRImageControl qrImageControl = null;
        ButtonControl buttonControl = null;
        ToggleControl toggleControl = null;
        CheckBoxControl checkBoxControl = null;

        if(component.getTitle().equalsIgnoreCase("Access Request TOC") && status.equalsIgnoreCase("Success"))
        {
            WorkFlowUtils.status = true;
        }
        else
        {
            WorkFlowUtils.status = false;
        }


        for (Fields field : component.getFields()) {


            FormCacheManager.getFormConfiguration().getFormFields().put(field.getKey(), field);

            if (textBoxControl == null) {
                textBoxControl = new TextBoxControl();
            }

            if (selectControl == null) {
                selectControl = new SelectControl();
            }

            if (multiSelectControl == null) {
                multiSelectControl = new MultiSelectControl();
            }


            if (autoCompleteTextBocControl == null) {
                autoCompleteTextBocControl = new AutoCompleteTextBocControl();
            }

            if (imgControl == null) {
                imgControl = new ImageControl(getActivity());
            }
            if (qrImageControl == null) {
                qrImageControl = new QRImageControl(getActivity());
            }
            if (buttonControl == null) {
                buttonControl = new ButtonControl();
            }

            if (toggleControl == null) {
                toggleControl = new ToggleControl();
            }

            if (checkBoxControl == null) {
                checkBoxControl = new CheckBoxControl();
            }

            FormFieldControl frmControl = new FormFieldControl();
            frmControl.setKey(field.getKey());

            if (ComponentType.BUTTON != field.getType() && ComponentType.SUBMIT != field.getType() &&
                    ComponentType.CHECKBOX != field.getType() && ComponentType.TOGGLE != field.getType()
                    && field.getTitle() != null && field.getTitle().length() > 0) {

                tvFieldCaption = new TextView(getActivity());
                tvFieldCaption.setTypeface(FontUtils.typeface(getActivity()));
                //New Hsse CR
//                String s="Hello world";
//                Textview someTextView;
//                someTextView.setText(getSafeSubstring(s, 5));
//                //the text of someTextView will be Hello
//
//                public String getSafeSubstring(String s, int maxLength){
//                    if(!TextUtils.isEmpty(s)){
//                        if(s.length() >= maxLength){
//                            return s.substring(0, maxLength);
//                        }
//                    }
//                    return s;
//                }
                //New Hsse CR
                tvFieldCaption.setTextColor(Color.parseColor("#2B4E81"));
                tvFieldCaption.setTextSize(15);
                tvFieldCaption.setLayoutParams(tvParam);
                tvFieldCaption.setFocusable(true);
                tvFieldCaption.setFocusableInTouchMode(true);

                if (field.getValidations() != null && field.getValidations().isRequired()) {
                    tvFieldCaption.setText(Html.fromHtml(field.getTitle() + "<sup><font color='#FF0000'>*</font></sup>"));
                } else {
                    tvFieldCaption.setText(field.getTitle());
                }

                linear.addView(tvFieldCaption, index);
                index++;

                frmControl.setCaptionCtrl(tvFieldCaption);

                if (field.isHidden()) {
                    tvFieldCaption.setVisibility(View.GONE);
                }
            } else {
                frmControl.setCaptionCtrl(null);
            }

            FormCacheManager.getFormControls().put(field.getId(), frmControl);

            if (field.getValidations() != null && field.getValidations().getLen() != null) {

                String[] arr = field.getValidations().getLen().split(AppConstants.COMMA);

                if (arr.length > 1) {
                    field.getValidations().setBfrLen(Integer.parseInt(arr[0]));
                    field.getValidations().setAfrLen(Integer.parseInt(arr[1]));
                }
            }

            //System.out.println("Field Key ************- "+field.getKey());
            switch (field.getType()) {
                case FLOAT:
                case INTEGER:
                case STRING:
                    linear.addView(textBoxControl.addTextBoxControl(getActivity(), field), index);
                    break;
                case DATE:
                    linear.addView(textBoxControl.addDate(getActivity(), field), index);
                    break;
                case TIME:
                    linear.addView(textBoxControl.addTime(getActivity(), field), index);
                    break;
                case DATETIME:
                    linear.addView(textBoxControl.addDateTime(getActivity(), field), index);
                    break;
                case SELECT:
                    linear.addView(selectControl.addSpinner(getActivity(), field), index);
                    break;
                case MULTISELECT:
                    linear.addView(multiSelectControl.addMultiSelect(getActivity(), field), index);
                    break;
                case AUTOCOMPLETE:
                    linear.addView(autoCompleteTextBocControl.addAutoComplete(getActivity(), field), index);
                    break;
                case IMAGE:
                    linear.addView(imgControl.cameraBt(getActivity(), field), index);
                    index++;
                    linear.addView(imgControl.recyclerView(getActivity(), field), index);
                    break;
                case QRIMAGE:
                    linear.addView(qrImageControl.qrBt(getActivity(), field), index);
                    index++;
                    linear.addView(qrImageControl.recyclerView1(getActivity(), field), index);
                    break;
                case BUTTON:
                case SUBMIT:
                    linear.addView(buttonControl.addButton(getActivity(), field), index);
                    break;
                case TOGGLE:
                    linear.addView(toggleControl.addToggleButton(getActivity(), field), index);
                    break;
                case CHECKBOX:
                    linear.addView(checkBoxControl.addCheckBox(getActivity(), field), index);
                    break;
                default:
                    break;
            }
            index++;
        }

        return index;
    }

    public void backButtonAlert(String confirmID, String primaryBt, String secondaryBT) {

        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
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
        tv_header.setTypeface(Utils.typeFace(getActivity()));
        positive.setTypeface(Utils.typeFace(getActivity()));
        negative.setTypeface(Utils.typeFace(getActivity()));
        title.setTypeface(Utils.typeFace(getActivity()));
        title.setText(Utils.msg(getActivity(), confirmID));
        // title.setText("Do you want to exit?");
        positive.setText(Utils.msg(getActivity(), primaryBt));
        negative.setText(Utils.msg(getActivity(), secondaryBT));

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                String key = "";
                backButton();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        });
    }

    public void backButton() {
        if ("AddRequest".equalsIgnoreCase(source)) {
            Intent i = new Intent(getActivity(), WorkflowImpl.class);
            startActivity(i);
            getActivity().finish();
        } else if ("MyRequest".equalsIgnoreCase(source)) {
            Intent i = new Intent(getActivity(), RequestReport.class);
            startActivity(i);
            getActivity().finish();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppPreferences.setTrackMode(1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri = null;
        if(requestCode == 108 || requestCode == 109 || requestCode == 107 || requestCode == 110)
        {
            if (requestCode == 108 || requestCode == 109) {
                if(data != null)
                {
                imageUri = data.getData();
                if (imageUri != null) {
                    try {
                        File file = new File(getRealPathFromURI(imageUri));
                        if (isFileSizeWithinLimit(file)) {
                            DataSubmitUtils utils = new DataSubmitUtils();
                            utils.onActivityResult(requestCode, resultCode, data);
                        } else {
                            Toast.makeText(getContext(), "File size exceeds 5MB limit.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            } else if (requestCode == 107 || requestCode == 110) {
                DataSubmitUtils utils = new DataSubmitUtils();
                utils.onActivityResult(requestCode,resultCode,data);
            }
        }
        else {
            ImageControl imgControl = new ImageControl(getActivity());
            imgControl.onActivityResult(requestCode, resultCode);
        }


    }

    private boolean isFileSizeWithinLimit(File file) {
        final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB in bytes
        return file.length() <= MAX_FILE_SIZE;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        try (Cursor cursor =getContext().getContentResolver().query(contentUri, projection, null, null, null)) {
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    private Uri getImageUri(Bitmap bitmap) {

        File imagesDir = new File(getContext().getFilesDir(), "images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        File imageFile = new File(imagesDir, "image_" + System.currentTimeMillis() + ".jpg");

        // Save the bitmap to the file
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Get the URI for the file
        return FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", imageFile);
    }

    private void initializeFormField() {

        for (Fields field : FormCacheManager.getFormConfiguration().getFormFields().values()) {

            WorkFlowUtils.resetFieldValues(getActivity(), field, false);
            WorkFlowUtils.resetEnableControl(getActivity(), field);
            WorkFlowUtils.resetShowHideControl(getActivity(), field);
        }


    }

    public class GetForm extends AsyncTask<Void, Void, String> {
        ProgressDialog pd;
        Context con;
        String formName = "";
        AppPreferences mAppPreferences;

        public GetForm(Context con, String formName) {
            this.formName = formName;
            this.con = con;
            mAppPreferences = new AppPreferences(con);
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(AppConstants.USER_ID_ALIAS, mAppPreferences.getUserId()));
            nameValuePairs.add(new BasicNameValuePair(Constants.FORM_NAME, "" + formName));
            nameValuePairs.add(new BasicNameValuePair(AppConstants.LANGUAGE_CODE_ALIAS, mAppPreferences.getLanCode()));
            String response = "";

            try {
                response = HttpUtils.httpGetRequest(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl() + WebAPIs.formConfiguration, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if ((result == null)) {
                Utils.toastMsg(con, AppConstants.msg_form);
            } else {
                WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(con);
                db.open();
                db.updateWorkFlowForm(result, formName, true);
                db.close();
            }

            InitializeWorkFlowForm(formName);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            }, 100);

            super.onPostExecute(result);
        }
    }

    public class GetTransactionDetail extends AsyncTask<Void, Void, String> {
        ProgressDialog pd;
        Context con;
        AppPreferences mAppPreferences;
        String formName;

        public GetTransactionDetail(Context con, String formName) {
            this.con = con;
            mAppPreferences = new AppPreferences(con);
            pd = new ProgressDialog(con);
            this.formName = formName;
        }

        @Override
        protected void onPreExecute() {
            isDataFetched = false;
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";

            String apiName = WebAPIs.requestDetail;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            if (AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleId()
                    == HsseConstant.HSSE_MODULE_ID
                    && formName.equalsIgnoreCase("EditHSSERequest")) {
                apiName = WebAPIs.getHSSErequestDetail;
                nameValuePairs.add(new BasicNameValuePair(HsseConstant.SUB_TAB, ""));
                nameValuePairs.add(new BasicNameValuePair(Constants.INSTANCE_ID, (String) FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_ID)));
                nameValuePairs.add(new BasicNameValuePair(Constants.INSTANCE_NAME, (String) FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_KEY)));
                nameValuePairs.add(new BasicNameValuePair(HsseConstant.OLD_TKT_STATUS, (String) FormCacheManager.getPrvFormData().get(HsseConstant.OLD_TKT_STATUS)));
                //nameValuePairs.add( new BasicNameValuePair( HsseConstant.OLD_GRP, (String)FormCacheManager.getPrvFormData().get(HsseConstant.OLD_GRP)));
            } else if (AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleId()
                    == HsseConstant.HSSE_MODULE_ID
                    && (formName.equalsIgnoreCase("EditHSSEPersonalDetail") ||
                    formName.equalsIgnoreCase("EditHSSELogBook") ||
                    formName.equalsIgnoreCase("EditHSSEPreventiveActions"))) {
                apiName = WebAPIs.getHSSESubTabrequestDetail;

                nameValuePairs.add(new BasicNameValuePair(HsseConstant.SUB_TAB, formName));
                nameValuePairs.add(new BasicNameValuePair(Constants.TASK_ID, (String) FormCacheManager.getPrvFormData().get(Constants.TASK_ID)));
                nameValuePairs.add(new BasicNameValuePair(Constants.TXN_ID, (String) FormCacheManager.getPrvFormData().get(Constants.TXN_ID)));
                nameValuePairs.add(new BasicNameValuePair(HsseConstant.OLD_TKT_STATUS, (String) FormCacheManager.getPrvFormData().get(HsseConstant.OLD_TKT_STATUS)));

                // nameValuePairs.add( new BasicNameValuePair( HsseConstant.OLD_GRP, (String)FormCacheManager.getPrvFormData().get(HsseConstant.OLD_GRP)));
            } else if (AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleId() == 1001) {
                nameValuePairs.add(new BasicNameValuePair(Constants.INSTANCE_ID, (String) FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_ID)));
                nameValuePairs.add(new BasicNameValuePair(Constants.INSTANCE_NAME, (String) FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_ID)));
                nameValuePairs.add(new BasicNameValuePair(Constants.S_ID, ""));
                nameValuePairs.add(new BasicNameValuePair(Constants.PSDT, ""));
                nameValuePairs.add(new BasicNameValuePair(Constants.PEDT, ""));
                nameValuePairs.add(new BasicNameValuePair("flag", "1"));
            }
            try {
                response = HttpUtils.httpGetRequest(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl() + apiName, nameValuePairs);
                int a = 1;

                //response = response.replaceAll("\"\"","");
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if ((result == null)) {
                Utils.toastMsg(con, AppConstants.msg_txn_data);
            } else {
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();


                Type listType = new TypeToken<Map<String, CamundaVariable>>() {
                }.getType();
                Map<String, CamundaVariable> requestDetail = gson.fromJson(result, listType);
                if (requestDetail != null) {
                    for (String key : requestDetail.keySet()) {
                        FormCacheManager.getPrvFormData().put(key, requestDetail.get(key).getValue());
                    }
                }
            }
            try {
                status = (String) FormCacheManager.getPrvFormData().get("implementer");
            }
            catch (Exception e)
            {
                status = "";

            }

            String pmName = (String) FormCacheManager.getPrvFormData().get("pmname");
            //109
            UIUtils.key = false;
            UIUtils.isMatch = false;

            if(mAppPreferences.getUserId().equalsIgnoreCase(pmName))
            {
                UIUtils.isMatch = true;
            }
            else
            {
                UIUtils.isMatch = false;
            }
            //109

            System.out.println("Transaction Data - " + FormCacheManager.getPrvFormData());

            isDataFetched = true;

            String formName = (String) FormCacheManager.getPrvFormData().get(Constants.FORM_KEY);

            String assignedUser = (String) FormCacheManager.getPrvFormData().get(Constants.ASSIGNED_USER);
            String assignedGroup = (String) FormCacheManager.getPrvFormData().get(Constants.ASSIGNED_GROUP);


            boolean isGroupExists = false;
            if (assignedGroup != null && assignedGroup.length() > 0) {
                List<String> aGroups = Arrays.asList(assignedGroup.split(","));

                List<String> userGroups = Arrays.asList(mAppPreferences.getUserGroupName().split(","));
                for (String name : userGroups) {
                    isGroupExists = aGroups.contains(name);
                    if (isGroupExists) {
                        break;
                    }
                }
            }

            boolean isUserExists = false;
            if (assignedUser != null && assignedUser.length() > 0) {
                List<String> aUser = Arrays.asList(assignedUser.toLowerCase().split(","));
                isUserExists = aUser.contains(mAppPreferences.getLoginId().toLowerCase());

                //List<String> aUser = Arrays.asList(assignedUser.split(","));
                //isUserExists = aUser.contains(mAppPreferences.getLoginId());
            }


            System.out.println("********** assignedUser - " + assignedUser);
            System.out.println("********** assignedGroup - " + assignedGroup);
            System.out.println("********** User Group - " + mAppPreferences.getUserGroupName().toString());
            System.out.println("********** User Login id - " + mAppPreferences.getLoginId());
            System.out.println("********** isGroupExists - " + isGroupExists);
            System.out.println("********** isUserExists - " + isUserExists);
            //if request is assigned to user then show the form using formkey else use formkeyedit
            //if(mAppPreferences.getLoginId().equalsIgnoreCase(assignedUser) ||isGroupExists){
            if (isUserExists || isGroupExists) {

                formName = (String) FormCacheManager.getPrvFormData().get(Constants.FORM_KEY);
                String impLoginId = (String) FormCacheManager.getPrvFormData().get(Constants.IMP_LOGIN_ID);
                String implementer = (String) FormCacheManager.getPrvFormData().get(Constants.IMPLEMENTER);

                if (FormCacheManager.getPrvFormData().containsKey(Constants.FORM_KEY_IMPL)
                        && mAppPreferences.getLoginId().equalsIgnoreCase(impLoginId)
                        && implementer != null && implementer.length() == 0) {
                    formName = (String) FormCacheManager.getPrvFormData().get(Constants.FORM_KEY_IMPL);
                }
                //   if(mAppPreferences.getLoginId().equalsIgnoreCase(FormCacheManager.getPrvFormData().get()));

            } else {

                String editForm = "";

                //Check A2 Group
                if (FormCacheManager.getPrvFormData().containsKey(Constants.EDIT_USER)) {
                    String editUsers = (String) FormCacheManager.getPrvFormData().get(Constants.EDIT_USER);
                    System.out.println("********** EDIT_USER - " + editUsers);
                    if (editUsers.length() != 0) {
                        List<String> userList = Arrays.asList(editUsers.split("|"));

                        for (String tmpUser : userList) {
                            String[] tmp = tmpUser.split("~");
                            if (mAppPreferences.getLoginId().equalsIgnoreCase(tmp[0])) {
                                editForm = tmp[1];
                                break;
                            }
                        }
                    }
                }

                if (editForm.length() == 0) {

                    if (FormCacheManager.getPrvFormData().containsKey(Constants.EDIT_GROUP)) {
                        String editGroup = (String) FormCacheManager.getPrvFormData().get(Constants.EDIT_GROUP);
                        System.out.println("********** editGroup - " + editGroup);
                        if (editGroup.length() != 0) {
                            List<String> grpList = Arrays.asList(editGroup.split("\\|"));
                            List<String> userGroups = Arrays.asList(mAppPreferences.getUserGroupName().split(","));

                            for (String tmpGrp : grpList) {
                                System.out.println("********** tmpGrp - " + tmpGrp);
                                String[] tmp = tmpGrp.split("~");

                                if (userGroups.contains(tmp[0])) {
                                    editForm = tmp[1];
                                    break;
                                }
                            }
                        }
                    }
                }

                if (editForm.length() == 0) {
                    if (FormCacheManager.getPrvFormData().containsKey(Constants.EDIT_FORM_KEY) &&
                            FormCacheManager.getPrvFormData().get(Constants.EDIT_FORM_KEY) != null &&
                            ((String) FormCacheManager.getPrvFormData().get(Constants.EDIT_FORM_KEY)).length() > 0) {
                        //109
                        editForm = (String) FormCacheManager.getPrvFormData().get(Constants.FORM_KEY);
                         if(UIUtils.isMatch && editForm.equalsIgnoreCase("AccessRequesttoc"))
                         {
                             UIUtils.key = true;
                             editForm = (String) FormCacheManager.getPrvFormData().get(Constants.FORM_KEY);
                         }
                         else {
                             UIUtils.key = false;
                             editForm = (String) FormCacheManager.getPrvFormData().get(Constants.EDIT_FORM_KEY);
                         }
                         //109
                      //  editForm = (String) FormCacheManager.getPrvFormData().get(Constants.EDIT_FORM_KEY);

                    } else {
                        editForm = (String) FormCacheManager.getPrvFormData().get(Constants.FORM_KEY);
                    }
                }
                formName = editForm;

            }
            System.out.println("********** formName - " + formName);
            renderForm(formName);

            //final Handler handler = new Handler();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            }, 500);

            isFormInitialized = true;

            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("lock")
                    && formName.equalsIgnoreCase("AccessRequestEdit")
                    && FormCacheManager.getPrvFormData().containsKey("sid")
                    && AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleId() == 1001) {
                try {
                    mAppPreferences.setSite((String) FormCacheManager.getPrvFormData().get("sid"));
                    new ButtonControl.GetSiteLocks(getActivity(), null, null,
                            "E", (String) FormCacheManager.getPrvFormData().get("sid"), mAppPreferences.getName()).execute();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            if (formName != null) {
                if ((formName.equalsIgnoreCase("AccessRequestImpl") ||
                        (formName.equalsIgnoreCase("AccessRequesttoc"))
                                && FormCacheManager.getPrvFormData().containsKey("sid")
                                && AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleId() == 1001)) {
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("sid")) {
                        String abc = FormCacheManager.getPrvFormData().get("sid").toString();
                        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("imploginid")) {
                            String implID = FormCacheManager.getPrvFormData().get("imploginid").toString();
                            Log.d("Avi ", abc);
                            Log.d("Avi ", implID);
                            callGetApiRespnceOwner(getContext(), abc, implID);
                        }


                    }


                }
            }
            System.out.println("initializeFormField Done*****************************************");
            super.onPostExecute(result);
        }
    }

    private void callGetApiRespnceOwner(Context context, String siteId, String impId) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<SiteLockResponce> call = request.getOwnerName(impId);
        call.enqueue(new Callback<SiteLockResponce>() {
            @Override
            public void onResponse(Call<SiteLockResponce> call, retrofit2.Response<SiteLockResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.body() != null) {
                    Log.d("Avi", response.body().getOwnerName());
                    String planEndDate = "", planStartDate = "";
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("asdt")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("asdt");
                        FormFieldControl filed = FormCacheManager.getFormControls().get(formControl3.getId());
                        planStartDate = "" + filed.getTextBoxCtrl().getText().toString();
                    }
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("aedt")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("aedt");
                        FormFieldControl filed = FormCacheManager.getFormControls().get(formControl3.getId());
                        planEndDate = "" + filed.getTextBoxCtrl().getText().toString();
                    }
                    Log.d("Avi", planEndDate);
                    Log.d("Avi", planStartDate);
                    if (planEndDate.isEmpty() && planStartDate.isEmpty()) {
                        callGetApiRespnce(siteId, response.body().getOwnerName(), response.body().getUserType(), context);
                        // callAPi(context,siteId, response.body().getOwnerName(), response.body().getUserType());
                    }

                }
            }

            @Override
            public void onFailure(Call<SiteLockResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        });
    }

    private void callGetApiRespnce(String site_id, String activtyTypeId, String userType, Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);
        String authCred = Credentials.basic(Utils.msg(context, "838"), Utils.msg(context, "839"));
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Utils.msg(context, "840"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okclient.build());
        final Retrofit retrofit = builder.build();
        //Toast.makeText(context, Utils.msg(context, "840"), Toast.LENGTH_SHORT).show();

        //Utils.toastMsg(context, "" + userType);
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<SiteLockResponce> call = iApiRequest.getSitelockAbloyID(site_id, activtyTypeId, userType, authCred);
        call.enqueue(new Callback<SiteLockResponce>() {
            @Override
            public void onResponse(Call<SiteLockResponce> call, retrofit2.Response<SiteLockResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //   Utils.toastMsg(context, "" +response.code());
                // Utils.toastMsg(context, "" + response.body().getAbloyIds());
                if (response.code() == 200 && response.body().getAbloyIds() != null) {
                   // Utils.toastMsg(context, "" + response.body().getAbloyIds());
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                        FormFieldControl filed = FormCacheManager.getFormControls().get(formControl3.getId());
                        filed.getCaptionCtrl().setVisibility(View.VISIBLE);
                        filed.getTextBoxCtrl().setEnabled(true);
                        filed.getTextBoxCtrl().setVisibility(View.VISIBLE);
                    }
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("validateKeySer")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("validateKeySer");
                        FormFieldControl filed = FormCacheManager.getFormControls().get(formControl3.getId());
                        filed.getButtonCtrl().setVisibility(View.VISIBLE);
                        filed.getButtonCtrl().setEnabled(true);
                    }
                    WorkFlowUtils.resetFieldValues(context, FormCacheManager.getFormConfiguration().getFormFields().get("validateKeySer"), true);
                    WorkFlowUtils.resetFieldValues(context, FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo"), true);

                }

            }

            @Override
            public void onFailure(Call<SiteLockResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    public void callAPi(Context context, String site_id, String activtyTypeId, String userType) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        String authCred = Credentials.basic("postgres", "11@");


        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://192.168.0.162:5000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Log.d("Avi", authCred);
        Log.d("Avi", site_id);
        Log.d("Avi", activtyTypeId);
        Log.d("Avi", userType);
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<ResponseBody> call = iApiRequest.getSmartLocks(site_id, activtyTypeId, userType, authCred);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + response.code());
                if (response.body() != null) {
                    // Utils.toastMsg(context, "" + response.body().getAbloyIds());
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                        FormFieldControl filed = FormCacheManager.getFormControls().get(formControl3.getId());
                        filed.getCaptionCtrl().setVisibility(View.VISIBLE);
                        filed.getTextBoxCtrl().setEnabled(true);
                        filed.getTextBoxCtrl().setVisibility(View.VISIBLE);
                    }
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("validateKeySer")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("validateKeySer");
                        FormFieldControl filed = FormCacheManager.getFormControls().get(formControl3.getId());
                        filed.getButtonCtrl().setVisibility(View.VISIBLE);
                        filed.getButtonCtrl().setEnabled(true);
                    }
                }
                //    Utils.toastMsg(context, "" + response.body().getAbloyIds());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });

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

                    }
                    // Do something with the response body
                } else {
                    Utils.toastMsg(context, Utils.msg(context, "850"));
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

}