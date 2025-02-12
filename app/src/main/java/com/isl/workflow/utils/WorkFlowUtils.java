package com.isl.workflow.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.faendir.rhino_android.RhinoAndroidHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.itower.MyApp;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.form.control.ImageControl;
import com.isl.workflow.form.control.QRImageControl;
import com.isl.workflow.form.control.SelectControl;
import com.isl.workflow.modal.ComponentType;
import com.isl.workflow.modal.DataSource;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FilterParam;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.JavaScriptExpression;
import com.isl.workflow.modal.LocalValue;
import com.isl.workflow.modal.Parameter;
import com.isl.workflow.modal.ShowHideValues;
import com.isl.workflow.modal.Value;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import infozech.itower.R;

public class WorkFlowUtils {

    private static AppPreferences appPreferences = new AppPreferences( MyApp.getAppContext());
    public static org.mozilla.javascript.Context jsContext;
    public static Scriptable scope;
    public static RhinoAndroidHelper rhinoAndroidHelper;
    public static boolean status = false;
    public static DropdownValue getSelectDDValue(){

        DropdownValue ddValue = new DropdownValue();
        ddValue.setId("0");
        ddValue.setValue( AppConstants.DD_SELECT_VALUE);
        return ddValue;
    }
    public static DropdownValue getNADDValue(){
        DropdownValue ddValue = new DropdownValue();
        ddValue.setId("2000" );
        ddValue.setValue("NA" );
        return ddValue;
    }

    public static String getDefaultValue(Fields formControl){

        String response = "";
        if(formControl.getDefaultValue()!=null){
            switch(formControl.getDefaultValue().getType()){
                case CONSTANT:
                    response = formControl.getDefaultValue().getValue();
                    break;
                case SESSION:
                    if (formControl.getDefaultValue().getKey()!=null) {
                        response = getSessionValue(formControl.getDefaultValue().getKey());
                    }
                    break;
            }
        }
        return response;
    }

    public static String getSessionValue(String key){
        String response = "";

        switch(key){
            case "uid":
                response = appPreferences.getUserId();
                break;
            case "uname":
                response = appPreferences.getName();
                break;
            case "unumber":
                response = appPreferences.getUserNumber();
                break;
            case "umailid":
                response = appPreferences.getUserMailid();
                break;
            case "uloginid":
                response = appPreferences.getLoginId();
                break;
            case "circle":
                response = appPreferences.getCircleID();
                break;
            case "zone":
                response = appPreferences.getZoneID();
                break;
            case "cluster":
                response = appPreferences.getClusterID();
                break;
            case "vendor":
                response = appPreferences.getPIOMEID();
                break;
            case "ugroup":
                response = appPreferences.getUserGroup();
                break;
            case "taskname":
                response = appPreferences.getTocForm();
                break;
            case "timerCycle":
                response = appPreferences.getTimerCycleCamunda();
                break;
            case "AbloyLockId":
                response = "Tawal";
                break;
            default: break;
        }
        return response;
    }

    /*
        Return the value field which is mentioned in filter param's value property.
        FilterParam - value property is key of any other field on form.
     */
    public static String getValueFromLocalData(String fieldKey,String valKey,boolean isValidation){
        String value = "";
        if(FormCacheManager.getFormConfiguration().getFormFields().containsKey(fieldKey)){

            switch(FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getType()){
                case SELECT:
                    DropdownValue selectedItem = (DropdownValue) FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getSelectCtrl().getSelectedItem();

                    if(valKey==null){
                        value = selectedItem.getId();
                    }else{
                        value = getSelectedItemVal(selectedItem, valKey);
                    }
                    break;
                case MULTISELECT:
                    List<DropdownValue> selectedItems = FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getSelectedVal();

                    for(DropdownValue temval : selectedItems){
                        if(valKey==null){
                            value = value+","+temval.getId();
                        }else{
                            value = value+","+getSelectedItemVal(temval, valKey);
                        }
                    }

                    break;
                case AUTOCOMPLETE:
                    DropdownValue selectedItem2 = FormCacheManager.getFormControls()
                            .get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId())
                            .getSelectedVal().get(0);

                    if(selectedItem2!=null){
                        if(valKey==null){
                            value = selectedItem2.getId();
                        }else{
                            value = getSelectedItemVal(selectedItem2, valKey);
                        }
                    } else{
                        value = FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getAutoCompleteCtrl().getText().toString();
                    }

                    break;
                case DATE:
                case DATETIME:
                    if(isValidation){
                        value = FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getValue();
                    } else{
                        value = FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getTextBoxCtrl().getText().toString();
                    }

                    break;
                case TOGGLE:
                    value = String.valueOf( FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getTgButtonCtrl().isChecked());
                    break;
                case CHECKBOX:
                    value = String.valueOf( FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getCheckBoxControl().isChecked());
                    break;
                case IMAGE:
                    value = String.valueOf( FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getImgGridView());
                    break;
                case QRIMAGE:
                    value = String.valueOf( FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getImgGridView());
                    break;
                default:
                    value = FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId()).getTextBoxCtrl().getText().toString();
                    break;
            }
        }

        return value;
    }

    /*
        Prepare List of DropdownValue using inline value specified in form configuration
     */
    public static List<DropdownValue> getInlineValues(Fields field){
        List<DropdownValue> ddValues = new ArrayList<DropdownValue>();

        ddValues.add( WorkFlowUtils.getSelectDDValue());

        for(Value value: field.getData().getValues()){
            DropdownValue tmp = new DropdownValue();
            tmp.setId(value.getValue());
            tmp.setValue(value.getLabel());
            ddValues.add(tmp);
        }
        return ddValues;
    }

    /*
        Prepare List of DropdownValue using URL specified in form configuration
     */
    public static List<DropdownValue> getResourceValues(Context context, Fields field){
        List<DropdownValue> ddValues = new ArrayList<DropdownValue>();

        if(field.getType().getValue().toString().
                equals( ComponentType.SELECT.getValue().toString())){
            ddValues.add( WorkFlowUtils.getSelectDDValue());
            if (field.getKey().equalsIgnoreCase("assetlist")){
                ddValues.add( WorkFlowUtils.getNADDValue());
            }
        }

        String sql = field.getData().getResource().getSelect().toLowerCase();

        WorkFlowDatabaseHelper db=new WorkFlowDatabaseHelper(context );
        db.open();

        StringBuffer condition = new StringBuffer();

        if(field.getData().getResource().getFilter()!=null && field.getData().getResource().getFilter().size()>0){
            String value = "";

            for(FilterParam filter : field.getData().getResource().getFilter()){
                value = "";

                switch(filter.getType()){
                    case SESSION:

                        /*Based on field key check if value corresponding to particular field is available in session or not
                            As following values are available in session -
                                User id, User Name, Role, User category, Country, Hub, Region, Circle, Zone, Cluster, OME etc.
                         */
                        value = getSessionValue(field.getKey());
                        break;
                    case LOCAL:
                        value = getValueFromLocalData(filter.getField(),filter.getValKey(),false);
                        break;
                    default: break;
                }

                if(value == null || value.equals("") || value.equals("0")){
                    if(filter.getDefVal()!=null){
                        value = filter.getDefVal();
                    } else{
                        continue;
                    }
                }

                if(sql.contains(" where ") || (condition != null && condition.length()>0)){
                    condition.append(" and ");
                } else{
                    condition.append(" where ");
                }

                if(filter.getPrefix()!=null && filter.getPrefix().length()>0
                        && filter.getSuffix()!=null && filter.getSuffix().length()>0){

                    value = filter.getPrefix()+value+filter.getSuffix();
                } else{

                    if(filter.getPrefix()!=null && filter.getPrefix().length()>0){
                        value = filter.getPrefix()+value;
                    }

                    if(filter.getSuffix()!=null && filter.getSuffix().length()>0){
                        value = value+filter.getSuffix();
                    }
                }

                condition.append(filter.getColumn());
                if(filter.getOper()==null){

                    if(value.contains(",")){
                        condition.append(" in (")
                                .append(value)
                                .append(")");
                    } else {
                        condition.append(" = ")
                                .append(value);
                    }
                } else{
                    switch(filter.getOper()){
                        case IN:
                            condition.append(" in (")
                                    .append(value)
                                    .append(")");
                            break;
                        case NOTIN:
                            condition.append(" in (")
                                    .append(value)
                                    .append(")");
                            break;
                        case EQUAL:
                            condition.append(" = ")
                                    .append(value);
                            break;
                        case LIKE:
                            condition.append(" like '%")
                                    .append(value)
                                    .append("%'");
                            break;
                        default:
                            break;
                    }
                }

            }
        }
        sql += condition.toString();

        if(field.getData().getResource().getOrder()!=null && field.getData().getResource().getOrder().length()>0){
            sql=sql+" "+field.getData().getResource().getOrder();
        }
        List<String> binVars = new ArrayList<String>();

        if(field.getData().getResource().getBindVar()!=null && field.getData().getResource().getBindVar().size()>0){
            String value = "";

            for(Parameter var : field.getData().getResource().getBindVar()){
                value = "";

                switch(var.getType()){
                    case SESSION:

                        /*Based on field key check if value corresponding to particular field is available in session or not
                            As following values are available in session -
                                User id, User Name, Role, User category, Country, Hub, Region, Circle, Zone, Cluster, OME etc.
                         */
                        value = getSessionValue(field.getKey());
                        break;
                    case FORM:
                        value = getValueFromLocalData(var.getField(),var.getValKey(),false);
                        break;
                    case TRAN:
                        if(FormCacheManager.getPrvFormData().containsKey(var.getField())){
                            value = (String) FormCacheManager.getPrvFormData().get(var.getField());
                        }
                        break;
                    case EXPRESSION:
                        try{
                            Object tmp = evaluateExpression(var.getExpression(),false);
                            value = org.mozilla.javascript.Context.toString(tmp);
                        } catch(Exception exp){
                            exp.printStackTrace();
                        }
                        break;
                    default: break;
                }

                binVars.add(value);
            }
        }



        ddValues = db.getDropdownList1(sql ,field.getKey().toLowerCase(),field.getIdProperty().toLowerCase(), field.getValProperty().toLowerCase(), binVars.toArray(new String[binVars.size()]));
        if(status && ddValues.size()>1)
        {
            ddValues.remove(1);
            status = false;
        }
        return ddValues;
    }

    /*
        Prepare List of DropdownValue using URL specified in form configuration
     */
    public static List<DropdownValue> getURLValues(Fields field){
        if(ComponentType.AUTOCOMPLETE == field.getType()){
            return null;
        }

        List<DropdownValue> ddValues = new ArrayList<DropdownValue>();

        return ddValues;
    }

    public static List<DropdownValue> getLocalValues(LocalValue local, boolean isValidation){
        List<DropdownValue> ddValues = new ArrayList<DropdownValue>();

        String value = getValueFromLocalData(local.getField(),local.getValKey(),isValidation);
        DropdownValue ddVal = new DropdownValue();

        ddVal.setId(value);
        ddVal.setValue(value);
        ddValues.add(ddVal);
        return ddValues;
    }

    public static List<DropdownValue> getExpressionValues(JavaScriptExpression expression, boolean isValidation){

        List<DropdownValue> ddValues = new ArrayList<DropdownValue>();

         Object output = evaluateExpression(expression,isValidation);
        //System.out.println("expression - "+expression.getFunction()+" - "+org.mozilla.javascript.Context.toString(output));
        if(output!=null) {
            DropdownValue ddVal = new DropdownValue();
            String val = org.mozilla.javascript.Context.toString(output);
            if (!"undefined".equalsIgnoreCase(val)) {
                ddVal.setValue(val);
                ddVal.setId(val);
                ddValues.add(ddVal);
            }
        }

        return ddValues;
    }

    public static List<DropdownValue> setFieldValues(Context context, Fields field,
                                                     FormFieldControl formControl,
                                                     boolean formInitialization){

        if(field.getDataSrc()==null){
            return null;
        }

        List<DropdownValue> ddValues = null;
        DataSource src = field.getDataSrc();

        if(ComponentType.AUTOCOMPLETE == field.getType()){
            //System.out.println(" field id   - "+field.getKey()+", source - "+field.getDataSrc().toString());
            if(src == DataSource.URL) {
                src = null;

                if (field.getData().getResource() != null) {
                    src = DataSource.RESOURCE;
                } else if (field.getData().getExpression() != null) {
                    src = DataSource.EXPRESSION;
                } else if (field.getData().getLocal() != null) {
                    src = DataSource.LOCAL;
                } else if (field.getData().getValues() != null) {
                    src = DataSource.VALUES;
                }
                //System.out.println(" field id   - "+field.getKey()+", source - "+src);
                if (src == null) {
                    return null;
                }
            }
        }

        switch(src){
            case VALUES:
                ddValues = getInlineValues(field);
                break;
            case RESOURCE:
                ddValues = getResourceValues(context,field);
                break;
            case URL:
                ddValues = getURLValues(field);
                break;
            case LOCAL:
                ddValues = getLocalValues(field.getData().getLocal(),false);
                break;
            case EXPRESSION:
                ddValues = getExpressionValues(field.getData().getExpression(),false);
                break;
            default:
                break;
        }
        //System.out.println(" field id   - "+field.getKey()+", source - "+field.getDataSrc().toString()+", Data Value size - "+ddValues.size());
        if(ddValues==null || ddValues.size()==0){
            return null;
        }
        //System.out.println(" field id   - "+field.getKey()+", source - "+field.getDataSrc().toString()+", Data Value size - "+ddValues.size());
        ArrayAdapter<DropdownValue> dataAdapter = null;
        try{
            switch (field.getType()){
                case SELECT:
                    dataAdapter = new ArrayAdapter<DropdownValue>( MyApp.getAppContext(), R.layout.spinner_text,ddValues)
                   {
                    @Override
                    public View getDropDownView ( final int position, View convertView, ViewGroup parent){
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        final TextView finalItem = tv;
                        tv.post( new Runnable() {
                        @Override
                        public void run() {
                            finalItem.setSingleLine( false );
                          }
                      });
                    return tv;
                    }
                   };
                    dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown );
                    formControl.getSelectCtrl().setAdapter( dataAdapter );
                    formControl.getSelectCtrl().setSelection(0,false);
                    //formControl.getSelectCtrl().setOnItemSelectedListener(SelectControl.this);
                    break;
                case MULTISELECT:
                    if(!formInitialization) {
                        formControl.getMultiSelectCtrl().setText( AppConstants.DD_SELECT_VALUE);
                        formControl.getSelectedVal().clear();
                    }
                    break;
                case AUTOCOMPLETE:
                    if(formInitialization){
                        dataAdapter = new ArrayAdapter<DropdownValue>( MyApp.getAppContext(),android.R.layout.select_dialog_item,ddValues);
                        formControl.getAutoCompleteCtrl().setAdapter(dataAdapter);
                    }

                    if(ddValues!=null && ddValues.size()==1){
                        formControl.getAutoCompleteCtrl().setText(ddValues.get(0).getValue());
                    } else{
                        formControl.getAutoCompleteCtrl().setText("");
                    }

                    break;
                case DATE:
                case TIME:
                case FLOAT:
                case STRING:
                case INTEGER:
                case DATETIME:
                    //System.out.println(" field id   - "+field.getKey()+", source - "+field.getDataSrc().toString()+", Data Value size - "+ddValues.size());
                    if(ddValues!=null && ddValues.size()==1){
                        //System.out.println(" field id   - "+field.getKey()+", source - "+field.getDataSrc().toString()+", Data Value size - "+ddValues.size()+", Value - "+ddValues.get(0).getValue());
                        formControl.getTextBoxCtrl().setText(ddValues.get(0).getValue());
                    } else{
                        formControl.getTextBoxCtrl().setText("");
                    }
                    break;
                case TOGGLE:
                    if(ddValues!=null && ddValues.size()==1){
                        formControl.getTgButtonCtrl().setChecked(Boolean.valueOf(ddValues.get(0).getValue()));
                    } else{
                        formControl.getTgButtonCtrl().setChecked(false);
                    }
                    break;
                case CHECKBOX:
                    if(ddValues!=null && ddValues.size()==1){
                        formControl.getCheckBoxControl().setChecked(Boolean.valueOf(ddValues.get(0).getValue()));
                    } else{
                        formControl.getCheckBoxControl().setChecked(false);
                    }
                    break;
                default:
                    break;
            }
        } catch(Exception exp){
            exp.printStackTrace();
        }
        return ddValues;
    }

    public static void resetFieldValues(Context context, Fields field, boolean clear){
        System.out.println("qrcode1");
        FormFieldControl formControl = FormCacheManager.getFormControls().get(field.getId());

        String value = "";
        if(!clear){
            if(FormCacheManager.getPrvFormData().containsKey(field.getKey())) {
                if (FormCacheManager.getPrvFormData().get(field.getKey()) != null && !((String) FormCacheManager.getPrvFormData().get(field.getKey())).equalsIgnoreCase("null")) {
                    value = (String) FormCacheManager.getPrvFormData().get(field.getKey());
                } else {
                    value = WorkFlowUtils.getDefaultValue(field);
                }
            }else {
                value = WorkFlowUtils.getDefaultValue(field);
            }
        }

         switch (field.getType()){
            case SELECT:

                //formControl.getSelectCtrl().setSelection(0,false);

                if(!clear){
                    //int index = 0;
                    if(formControl.getSelectCtrl().getAdapter()==null){
                        if(value.length()>0) {
                            DropdownValue ddVal = new DropdownValue();
                            ddVal.setId( value );

                            if (FormCacheManager.getPrvFormData().containsKey( field.getValProperty() )) {
                                ddVal.setValue( (String) FormCacheManager.getPrvFormData().get( field.getValProperty() ) );
                            } else {
                                ddVal.setValue( value );
                            }

                            List<DropdownValue> ddValList = new ArrayList<DropdownValue>();
                            ddValList.add( ddVal );

                            ArrayAdapter<DropdownValue> dataAdapter = new ArrayAdapter<DropdownValue>( context, R.layout.spinner_text, ddValList )
                            {
                                @Override
                                    public View getDropDownView ( final int position, View convertView, ViewGroup parent){
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView tv = (TextView) view;
                                    final TextView finalItem = tv;
                                    tv.post( new Runnable() {
                                        @Override
                                        public void run() {
                                            finalItem.setSingleLine( false );
                                        }
                                    });
                                    return tv;
                                }
                              };
                            dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown );
                            formControl.getSelectCtrl().setAdapter( dataAdapter );
                            formControl.getSelectCtrl().setSelection(0,false);
                            formControl.getSelectedVal().add(0,ddVal);
                        }
                    } else{
                        int a = 1;
                        int count = formControl.getSelectCtrl().getAdapter().getCount();
                        DropdownValue ddValue;

                        //formControl.getSelectCtrl().setOnItemSelectedListener(null);

                        //System.out.println("Key - "+field.getKey()+", preSelectedVal - "+preSelectedVal+", count - "+count);
                        //System.out.println();
                        for(int i =0;i<count;i++){
                            ddValue = (DropdownValue)formControl.getSelectCtrl().getAdapter().getItem(i);
                            //System.out.println("Key - "+field.getKey()+", ddValue.id - "+ddValue.getId()+", preSelectedVal - "+preSelectedVal);
                            if(ddValue.getId().equalsIgnoreCase(value)){
                                //System.out.println("Key - "+field.getKey()+", Selected Value - "+ddValue.getId()+", index - "+i);
                                formControl.getSelectCtrl().setSelection(i,false);
                                formControl.getSelectCtrl().setTag(""+i);
                                formControl.getSelectedVal().add(0,ddValue);
                                break;
                            }
                        }
                    }

                    //formControl.getSelectCtrl().setOnItemSelectedListener(new SelectControl());
                }
                break;
            case MULTISELECT:
                if(clear){
                    String eValue = formControl.getMultiSelectCtrl().getText().toString();
                    if(eValue!=null && eValue.length()>0){
                        formControl.setValue(formControl.getMultiSelectCtrl().getText().toString());
                    }
                }

               // formControl.getMultiSelectCtrl().setText( AppConstants.DD_SELECT_VALUE);
                if(!clear && value.length()>0){
                    String str[] = value.trim().split(",");
                    List<String> al = new ArrayList<String>();
                    al = Arrays.asList(str);
                    WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(context);
                    db.open();
                    formControl.getSelectedVal().clear();
                    int index = 0;
                    String sql = "";
                    String orderBy = "";
                    if(field.getData().getResource().getInnerselect()!=null && field.getData().getResource().getInnerselect().length()>0){
                        sql = field.getData().getResource().getInnerselect().toLowerCase();
                    }

                    if(field.getData().getResource().getOrder()!=null && field.getData().getResource().getOrder().length()>0){
                        orderBy = " "+field.getData().getResource().getOrder();
                    }


                    //if(field.getKey().equalsIgnoreCase("assigntouserid")
                     //       || field.getKey().equalsIgnoreCase("assigntouseridtxt")) {
                       // String paramType = "200";
                    if(sql.length()>0 && al.size()>0) {
                        for (String s : al) {
                            List<DropdownValue> ddValues = db.getDropdownList1( sql + al.get( index ) + orderBy,
                                    field.getKey().toLowerCase(),field.getIdProperty().toLowerCase(), field.getValProperty().toLowerCase(), null );
                            //List<DropdownValue> ddValues = db.getDropdownList( "Select id as uid,val1 as name,val2,val3,val11 from workflow_meta_data where type='" + paramType + "' AND id='" + al.get( index ) + "' ORDER BY val1 ASC",
                            //        "uid", "name", null );
                            if (ddValues.size() >= 2) {
                                //FormCacheManager.getFormControls().get(field.getId()).getSelectedVal().add(ddValues.get(1));
                                formControl.getSelectedVal().add( ddValues.get( 1 ) );
                                index++;
                            }
                        }
                    }
                    /*}else if(field.getKey().equalsIgnoreCase("personcat")
                            || field.getKey().equalsIgnoreCase("personcattxt")) {
                        String paramType = "-166";
                        for (String s : al) {
                            List<DropdownValue> ddValues = db.getDropdownList( "Select id as paramId,val1 as paramValue from workflow_meta_data where type='" + paramType + "' AND id='" + al.get( index ) + "' ORDER BY val1 ASC",
                                    "paramId", "paramValue", null );
                            if (ddValues.size() >= 2) {
                                //FormCacheManager.getFormControls().get(field.getId()).getSelectedVal().add(ddValues.get(1));
                                formControl.getSelectedVal().add( ddValues.get(1));
                                index++;
                            }
                        }
                    }*/
                    String temvall = al.size()+ AppConstants.DD_SELECTED_VALUE;
                    if(al.size()==0){
                        temvall = AppConstants.DD_SELECT_VALUE;
                    }
                    formControl.getMultiSelectCtrl().setText(temvall);
                    //formControl.getMultiSelectCtrl().setText(value);
                    formControl.setValue(value);
                }

                break;
            case AUTOCOMPLETE:


                formControl.getAutoCompleteCtrl().setText("");

                if(!clear && value.length()>0){
                    //formControl.getAutoCompleteCtrl().setTag("-1");
                    formControl.getAutoCompleteCtrl().setText(value);
                    DropdownValue ddVal = new DropdownValue();
                    ddVal.setId(value);
                    ddVal.setValue(value);
                    formControl.getSelectedVal().add(0,ddVal);
                    //formControl.getSelectCtrl().setTag(field.getId());
                }

                //formControl.getAutoCompleteCtrl().setOnItemClickListener(new AutoCompleteTextViewClickListener(formControl.getAutoCompleteCtrl(), new AutoCompleteTextBocControl()));
                break;
            case FLOAT:
            case STRING:
            case INTEGER:
                if(clear){
                    String eValue = formControl.getTextBoxCtrl().getText().toString();
                    if(eValue!=null && eValue.length()>0){
                        //System.out.println("**********************formControl - "+formControl.getKey()+" - "+formControl.getTextBoxCtrl().getText().toString());
                        formControl.setValue(formControl.getTextBoxCtrl().getText().toString());
                    }
                }
                formControl.getTextBoxCtrl().setText("");
                if(!clear && value.length()>0){
                    formControl.getTextBoxCtrl().setText(value);
                    formControl.setValue(value);
                }

                break;
            case DATE:
            case TIME:
            case DATETIME:

                if(clear){
                    formControl.setValue(formControl.getTextBoxCtrl().getText().toString());
                }

                formControl.getTextBoxCtrl().setText("");
                if(!clear && value.length()>0){

                    formControl.getTextBoxCtrl().setText(value);
                    SimpleDateFormat dateFormatterInternal = new SimpleDateFormat( Constants.DAEFULT_DATETIME_FORMAT, Locale.ENGLISH);

                    try{
                        if(field.getValidations()!=null && field.getValidations().getFormat()!=null){
                            dateFormatterInternal = new SimpleDateFormat(field.getValidations().getFormat(),Locale.ENGLISH);
                            value = DateTimeUtils.dateFormatterInternal.format(dateFormatterInternal.parse(value));
                        } else{
                            value = DateTimeUtils.dateFormatterInternal.format(dateFormatterInternal.parse(value));
                            dateFormatterInternal = new SimpleDateFormat( Constants.DAEFULT_DATETIME_FORMAT,Locale.ENGLISH);
                            value = DateTimeUtils.dateFormatterInternal.format(dateFormatterInternal.parse(value));
                        }
                    } catch(Exception exp){
                        exp.printStackTrace();
                        formControl.setValue(value);
                    }

                    formControl.setValue(value);
                }
                break;
            case IMAGE:
                ImageControl imgControl = new ImageControl(context);
                imgControl.initializePreviousImages(field.getId(),field.getKey());
                break;
            case QRIMAGE:
                //System.out.println("qrcode2");
                QRImageControl qrImgControl = new QRImageControl(context);
                qrImgControl.initializePreviousAssetData(field);
                break;
            case BUTTON:
            case SUBMIT:
                //ImageControl imgControl = new ImageControl(context);
                //imgControl.initializePreviousImages(field.getId(),field.getKey());
                break;
            case CHECKBOX:
                formControl.getCheckBoxControl().setChecked(false);

                if(!clear && value.length()>0){
                    formControl.getCheckBoxControl().setChecked(Boolean.valueOf(value));
                    formControl.setValue(value);
                }
                break;
            case TOGGLE:
                formControl.getTgButtonCtrl().setChecked(false);

                if(!clear && value.length()>0){

                    try{
                        formControl.getTgButtonCtrl().setChecked(Boolean.valueOf(value));
                        formControl.setValue(value);
                    } catch(Exception exp){
                        exp.printStackTrace();
                    }
                    //System.out.println("***********Toggle control - "+formControl.getTgButtonCtrl().isChecked());
                    //System.out.println("***********Toggle control - "+value);
                    if(formControl.getTgButtonCtrl().isChecked() && field.getOntitle()!=null){
                        formControl.getTgButtonCtrl().setText(field.getOntitle());
                    }

                    if((!formControl.getTgButtonCtrl().isChecked()) && field.getOfftitle()!=null){
                        formControl.getTgButtonCtrl().setText(field.getOfftitle());
                    }

                }
                break;
            default:
                break;
        }
    }

    public static String getSelectedItemVal(DropdownValue selectedItem, String valKey){
        String value = "";

        switch(valKey){
            case "id":
                value = selectedItem.getId();
                break;
            case "value":
                value = selectedItem.getValue();
                break;
            case "val1":
                value = selectedItem.getVal1();
                break;
            case "val2":
                value = selectedItem.getVal2();
                break;
            case "val3":
                value = selectedItem.getVal3();
                break;
            case "val4":
                value = selectedItem.getVal4();
                break;
            case "val5":
                value = selectedItem.getVal5();
                break;
            case "val6":
                value = selectedItem.getVal6();
                break;
            case "val7":
                value = selectedItem.getVal7();
                break;
            case "val8":
                value = selectedItem.getVal8();
                break;
            case "val9":
                value = selectedItem.getVal9();
                break;
            case "val10":
                value = selectedItem.getVal10();
                break;
            case "val11":
                value = selectedItem.getVal11();
                break;
            case "val12":
                value = selectedItem.getVal12();
                break;
            case "val13":
                value = selectedItem.getVal13();
                break;
            case "val14":
                value = selectedItem.getVal14();
                break;
            case "val15":
                value = selectedItem.getVal15();
                break;
            case "val16":
                value = selectedItem.getVal16();
                break;
            case "val17":
                value = selectedItem.getVal17();
                break;
            case "val18":
                value = selectedItem.getVal18();
                break;
            case "val19":
                value = selectedItem.getVal19();
                break;
            case "val20":
                value = selectedItem.getVal20();
                break;
            default:
                break;
        }

        return value;
    }

    public static String getFieldValue(Parameter param, boolean isValidation){

        String response = "";

        switch(param.getType()){
            case CONSTANT:
                if("current-date".equalsIgnoreCase(param.getValue())){
                    response = DateTimeUtils.currentDateTime(param.getValKey());
                } else{
                    response = param.getValue();
                }
                break;
            case SESSION:
                response = getSessionValue(param.getKey());
                break;
            case FORM:
                response = getValueFromLocalData(param.getField(),param.getValKey(),isValidation);
                break;
            case TRAN:
                response = (String) FormCacheManager.getPrvFormData().get(param.getField());
                break;
        }

        return response;
    }

    public static Object evaluateExpression(JavaScriptExpression requiredExp, boolean isValidation){
        Object result = null;

        if(rhinoAndroidHelper==null){
            initializeJSEngine();
        }

        List<Object> functionArgs = new ArrayList<Object>();

        if(requiredExp.getField()!=null){
            int i = 0;
            for(Parameter param : requiredExp.getField()){
                Object tmp = WorkFlowUtils.getFieldValue(param,isValidation);

                if(param.getKey()!=null){
                    scope.put(param.getKey(),scope,tmp);
                } else{
                    scope.put(param.getField(),scope,tmp);
                }

                System.out.println("Expression - "+requiredExp.getExpression()+", Variable - "+param.getField()+" - "+param.getType()+" - "+scope.get(param.getField(),scope)+" - "+scope.get(param.getKey(),scope));

                if(requiredExp.getFunction()!=null){
                    functionArgs.add(i,tmp);
                    i++;
                }

                /*System.out.println("************************************************");
                System.out.println("Variable - "+param.getField()+" - "+scope.get(param.getField(),scope));
                System.out.println("************************************************");*/

            }
        }

        try {


            //Object[] functionParams = new Object[] {"Other parameters",new Storage()};
            //rhino.setOptimizationLevel(-1);
            //jsContext.enter();
            //System.out.println("Expression  *******************************= " + requiredExp.getExpression());
            result = jsContext.evaluateString(scope, requiredExp.getExpression(), "<hello_world>", 1, null);

            if(requiredExp.getFunction()!=null){

                Object fObj = scope.get(requiredExp.getFunction(), scope);

                if (fObj instanceof Function) {
                    Function f = (Function)fObj;
                    result = f.call(jsContext, scope, scope, functionArgs.toArray());
                    //String report = "f('my args') = " + jsContext.toString(result);
                    //System.out.println(requiredExp.getFunction() +" = " + jsContext.toString(result));
                }
            }/*else{
                System.out.println("************************************************");
                System.out.println("Expression - "+requiredExp.getExpression());
                System.out.println("Result is - "+org.mozilla.javascript.Context.toBoolean(result));
                System.out.println("************************************************");
            }*/
            //Toast.makeText(this, org.mozilla.javascript.Context.toString(result), Toast.LENGTH_LONG).show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static void initializeJSEngine(){

        rhinoAndroidHelper = new RhinoAndroidHelper( MyApp.getAppContext());
        jsContext = rhinoAndroidHelper.enterContext();
        jsContext.setOptimizationLevel(1);
        //scope = new ImporterTopLevel(jsContext);
        scope = jsContext.initStandardObjects();
    }

    public static void resetEnableControl(Context context, Fields field){
        /*if (field==null){
            return;
        }*/
        String a = field.getId();
        FormFieldControl formControl = FormCacheManager.getFormControls().get(field.getId());
        boolean isDisable = field.isDisabled();

        if(field.getRule()!=null){
            Object output;
            if(field.getRule().getDisable()!=null && field.getRule().getDisable().getExpression()!=null){
                output = WorkFlowUtils.evaluateExpression(field.getRule().getDisable(),true);

                if(output!=null){
                    isDisable = org.mozilla.javascript.Context.toBoolean(output);
                }

                System.out.println("*********** Disable Rule11 - "+field.getRule().getDisable().getExpression()+", result - "+isDisable);
            }
        }

        switch(field.getType()){
            case SELECT:
                formControl.getSelectCtrl().setEnabled( !isDisable );
                break;
            case MULTISELECT:
                formControl.getMultiSelectCtrl().setEnabled( !isDisable );
                break;
            case FLOAT:
            case INTEGER:
            case STRING:
            case DATE:
            case DATETIME:
            case TIME:
                formControl.getTextBoxCtrl().setEnabled( !isDisable );
                break;
            case AUTOCOMPLETE:
                formControl.getAutoCompleteCtrl().setEnabled( !isDisable );

                if(FormCacheManager.getPrvFormData().containsKey("srcProcess") &&
                        field.getKey().equalsIgnoreCase( "implname")){
                    FormCacheManager.getFormControls().get(field.getId()).getAutoCompleteCtrl().setEnabled(false);

                }
                break;
            case TOGGLE:
                formControl.getTgButtonCtrl().setEnabled( !isDisable );
                break;
            case CHECKBOX:
                formControl.getCheckBoxControl().setEnabled( !isDisable );
                break;
            case IMAGE:
                formControl.getButtonCtrl().setEnabled( !isDisable );
                break;
            case QRIMAGE:
                formControl.getButtonCtrl().setEnabled( !isDisable );
                break;
            case BUTTON:
            case SUBMIT:
                formControl.getButtonCtrl().setEnabled( !isDisable );
                if(isDisable){
                    formControl.getButtonCtrl().setBackgroundResource( R.drawable.disable);
                } else{
                    formControl.getButtonCtrl().setBackgroundResource( R.drawable.button_9_blue);
                }
                break;
            default: break;
        }
    }

    public static void resetShowHideControl(Context context, Fields field){
        String formName = (String)FormCacheManager.getPrvFormData().get(Constants.FORM_KEY);
        String oper = (String)FormCacheManager.getPrvFormData().get(Constants.OPERATION);
        String editRights = (String)FormCacheManager.getPrvFormData().get(Constants.EDIT_RIGHTS);
        FormFieldControl formControl = FormCacheManager.getFormControls().get(field.getId());
        boolean isHidden = field.isHidden();
        //109
        if(UIUtils.isMatch && UIUtils.key) {
            if (field.getTitle().equalsIgnoreCase("Request Status")) {
                isHidden = true;
            }
        }
     //109

        if(field.getRule()!=null){
            Object output;

            if(field.getRule().getHide()!=null && field.getRule().getHide().getExpression()!=null){
                output = WorkFlowUtils.evaluateExpression(field.getRule().getHide(),false);

                if(output!=null){
                    isHidden = org.mozilla.javascript.Context.toBoolean(output);
                }
                //System.out.println("**************** Field - "+formControl.getKey()+", Hide - "+isHidden);
                //System.out.println("**************** Field - "+formControl.getKey()+", Hide Rule - "+field.getRule().getHide().getExpression());
            }
        }


        if(formControl.getCaptionCtrl()!=null){
            if(isHidden) {
                formControl.getCaptionCtrl().setVisibility( View.GONE );
            }else{
                formControl.getCaptionCtrl().setVisibility( View.VISIBLE );
            }
        }

        switch(field.getType()){
            case SELECT:
                if(isHidden) {
                    formControl.getSelectCtrl().setVisibility( View.GONE );
                }else{
                    formControl.getSelectCtrl().setVisibility( View.VISIBLE );
                }
                break;
            case MULTISELECT:
                if(isHidden) {
                    formControl.getMultiSelectCtrl().setVisibility( View.GONE );
                }else{
                    formControl.getMultiSelectCtrl().setVisibility( View.VISIBLE );
                }
                break;
            case FLOAT:
            case INTEGER:
            case STRING:
            case DATE:
            case DATETIME:
            case TIME:
                if(isHidden) {
                    formControl.getTextBoxCtrl().setVisibility( View.GONE );
                }else{
                    formControl.getTextBoxCtrl().setVisibility( View.VISIBLE );
                }
                break;
            case CHECKBOX:
                if(isHidden) {
                    formControl.getCheckBoxControl().setVisibility( View.GONE );
                }else{
                    formControl.getCheckBoxControl().setVisibility( View.VISIBLE );
                }
                break;
            case IMAGE:
            case QRIMAGE:
            case BUTTON:
            case SUBMIT:
                if(isHidden) {
                    formControl.getButtonCtrl().setVisibility( View.GONE );
                }else{
                    if(oper.equals("E") && formName.contains("HSSE")
                            && editRights!=null
                            && !editRights.contains("E")){
                        formControl.getButtonCtrl().setVisibility( View.GONE );
                    }else{
                        formControl.getButtonCtrl().setVisibility( View.VISIBLE );
                    }
                }
                break;
            default: break;
        }
    }

    public static void showDependentFields(Context context, Fields formControl, boolean isClick){

        List<String> showList = null;
        if(isClick){
            showList = formControl.getOnClick().getShow();
        } else{
            showList = formControl.getOnChange().getShow();
        }

        //List<String> s = field.getOnChange().getShow();
        // int s1 = field.getOnChange().getShow().indexOf("locks");
        //String ss = field.getOnChange().getShow().get( s1 );

        for (String fieldKey : showList) {

            resetShowHideControl(context, FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey));
        }
    }

    public static void hideDependentFields(Context context, Fields formControl, boolean isClick){

        List<String> hideList = null;
        if(isClick){
            hideList = formControl.getOnClick().getHide();
        } else{
            hideList = formControl.getOnChange().getHide();
        }

        for (String fieldKey : hideList) {

            resetShowHideControl(context, FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey));
        }
    }

    public static void enableDependentFields(Context context, Fields formControl, boolean isClick){

        List<String> enableList = null;
        if(isClick){
            enableList = formControl.getOnClick().getEnable();
        } else{
            enableList = formControl.getOnChange().getEnable();
        }

        for (String fieldKey : enableList) {

            resetEnableControl(context, FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey));
        }
    }

    public static void disableDependentFields(Context context, Fields formControl, boolean isClick){

        List<String> disableList = null;
        if(isClick){
            disableList = formControl.getOnClick().getDisable();
        } else{
            disableList = formControl.getOnChange().getDisable();
        }

        for (String fieldKey : disableList) {
            //String s = fieldKey;
           // Map<String,Fields>  link = FormCacheManager.getFormConfiguration().getFormFields();
            //FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey);
            resetEnableControl(context, FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey));
        }

    }

    public static void resetDependentFields(Context context, Fields formControl, DropdownValue selectedValue){

        for (ShowHideValues resetFieldDetail : formControl.getOnChange().getReset()) {

            if(resetFieldDetail.getSelectedVal()!=null && resetFieldDetail.getSelectedVal().length()>0){
                if(!selectedValue.getId().equals(resetFieldDetail.getSelectedVal())){
                    continue;
                }
            }

            for(String resetFieldKey:resetFieldDetail.getFieldKey()){
                //System.out.println("Resetting following fields - "+resetFieldKey);
                FormFieldControl dependentFormFieldControl = FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(resetFieldKey).getId());


                if(FormCacheManager.getFormConfiguration().getFormFields().get(resetFieldKey).getType().toString().equals( ComponentType.SELECT.toString())){
                    dependentFormFieldControl.getSelectCtrl().setOnItemSelectedListener(null);
                }
                WorkFlowUtils.resetFieldValues(context, FormCacheManager.getFormConfiguration().getFormFields().get(dependentFormFieldControl.getKey()),true);

                if(FormCacheManager.getFormConfiguration().getFormFields().get(resetFieldKey).getType().toString().equals( ComponentType.SELECT.toString())){
                    //dependentFormFieldControl.getSelectCtrl().setSelection(0,false);
                    dependentFormFieldControl.getSelectCtrl().setOnItemSelectedListener(new SelectControl());
                }
            }
        }
    }

    public static void refreshDependentFields(Context context, Fields formControl,
                                              boolean isClick){

        List<String> refreshList = null;
        if(isClick){
            refreshList = formControl.getOnClick().getRefresh();
        } else{
            refreshList = formControl.getOnChange().getRefresh();
        }

        for (String fieldKey : refreshList) {
             FormFieldControl dependentFormFieldControl = FormCacheManager.getFormControls().get( FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getId());
             WorkFlowUtils.setFieldValues(context, FormCacheManager.getFormConfiguration().getFormFields().get(dependentFormFieldControl.getKey()), dependentFormFieldControl,false);
        }
    }
}
