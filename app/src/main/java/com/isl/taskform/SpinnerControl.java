package com.isl.taskform;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.isl.dao.cache.AppPreferences;
import com.isl.dao.cache.ConfigurationCacheManager;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.MyApp;
import com.isl.modal.FormControl;
import com.isl.util.UtilsTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import infozech.itower.R;

public class SpinnerControl implements OnItemSelectedListener {

    public static HashMap<String, String> prvValues;

    AppPreferences mAppPreferences = new AppPreferences(MyApp.getAppContext());

    public SpinnerControl(HashMap<String, String> prvValues){
        SpinnerControl.prvValues = prvValues;
    }

    public Spinner addSpinner(Context context, FormControl formControl) {

        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins( 10, 5, 10, 0 );
        Spinner spinner = new Spinner( context );
        DataBaseHelper db=new DataBaseHelper(context );
        db.open();

        String sql=formControl.getDropDownValue();

        if(AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == formControl.getDropDownValType() && sql!=null){
            String where="";

            if(formControl.getKeyItem().equalsIgnoreCase( AppConstants.CIRCLE_ID_ALIAS )) {
                if(mAppPreferences.getCircleID().equalsIgnoreCase( AppConstants.ZERO )){
                    where="";
                }else {
                    where = mAppPreferences.getCircleID();
                }
            }else if(formControl.getKeyItem().equalsIgnoreCase( AppConstants.ZONE_ID_ALIAS )){
                 sql=UtilsTask.getZoneSql(sql,mAppPreferences.getCircleID(),mAppPreferences.getZoneID());
            }else if(formControl.getKeyItem().equalsIgnoreCase( AppConstants.CLUSTER_ID_ALIAS )){
                sql=UtilsTask.getClusterSql(sql,mAppPreferences.getCircleID(),mAppPreferences.getZoneID(),
                        mAppPreferences.getClusterID());
            }else if(formControl.getKeyItem().equalsIgnoreCase( AppConstants.OME_ID_ALIAS )) {
                if (mAppPreferences.getPIOMEID().equalsIgnoreCase( AppConstants.ZERO )) {
                    where = "";
                } else {
                    where = mAppPreferences.getPIOMEID();
                }
            }

            ddSpinnerData(db.getCursor(sql,where),formControl.getDdValues(),formControl.getDdIds());
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( MyApp.getAppContext(),R.layout.spinner_text,formControl.getDdValues() );
            dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown );
            spinner.setAdapter( dataAdapter );
            int pos= formControl.getDdIds().indexOf(prvValues.get(formControl.getKeyItem()));
            spinner.setSelection(pos);
            setInitializeValue(pos,spinner,formControl);
        }else if(AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() == formControl.getDropDownValType() && sql!=null){
            addItemsOnSpinner( spinner, sql,prvValues.get(formControl.getKeyItem()));
        }

        spinner.setBackgroundResource(R.drawable.doted);
        spinner.setId(Integer.parseInt( formControl.getFieldId()));
        spinner.setLayoutParams( InputParam );
        final float scale = context.getResources().getDisplayMetrics().density;
        spinner.setMinimumHeight( (int)(35 * scale) );

        if(AppConstants.FIELD_TYPE.READ_ONLY.getValue() == formControl.getFieldType()) {
            spinner.setEnabled( false );

        }else if(AppConstants.FIELD_TYPE.HIDDEN.getValue() == formControl.getFieldType()||
                AppConstants.PARENT_LEVEL.CHILD.getValue()==formControl.getPlevel()) {
            spinner.setVisibility( View.GONE );
        }else{
            spinner.setEnabled( true );
        }

        spinner.setOnItemSelectedListener(SpinnerControl.this);
        formControl.setSpinner( spinner );
        db.close();
        return  spinner;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

        FormControl formControl = ConfigurationCacheManager.getFormControlList().get(arg0.getId());

        if (AppConstants.FIELD_TYPE.READ_WRITE.getValue() == formControl.getFieldType()){

            String selectId = formControl.getDdIds().get( position );
            String selectValue = formControl.getDdValues().get(position);

            if (selectValue.equalsIgnoreCase(AppConstants.DD_SELECT_VALUE )) {
                if(formControl.getFieldId().equalsIgnoreCase("72")) {
                    FormControl showControl = null;
                    showControl = ConfigurationCacheManager.getFormControlList().get(73);
                    showControl.getTv().setVisibility(View.VISIBLE);
                    if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == showControl.getDropDownValType()
                            || AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() == showControl.getDropDownValType()) {
                        //if (AppConstants.DATA_TYPE.DROPDOWN.getValue() == showControl.getDataType()) {
                        showControl.getSpinner().setVisibility(View.GONE);
                    } else {
                        showControl.getEditText().setVisibility(View.GONE);
                        showControl.getTv().setVisibility(View.GONE);
                    }
                }
                return;
            }
            if (selectValue.equalsIgnoreCase("Emergency")){
                FormControl showControl = null;
                showControl = ConfigurationCacheManager.getFormControlList().get(73);
                showControl.getTv().setVisibility( View.VISIBLE );
                if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue()== showControl.getDropDownValType()
                        ||AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue()== showControl.getDropDownValType()) {
                    //if (AppConstants.DATA_TYPE.DROPDOWN.getValue() == showControl.getDataType()) {
                    showControl.getSpinner().setVisibility( View.VISIBLE );
                } else {
                    showControl.getEditText().setVisibility( View.VISIBLE );
                }
            }if (selectValue.equalsIgnoreCase("Unplanned")){
                FormControl showControl = null;
                showControl = ConfigurationCacheManager.getFormControlList().get(73);
                showControl.getTv().setVisibility( View.VISIBLE );
                if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue()== showControl.getDropDownValType()
                        ||AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue()== showControl.getDropDownValType()) {
                    //if (AppConstants.DATA_TYPE.DROPDOWN.getValue() == showControl.getDataType()) {
                    showControl.getSpinner().setVisibility( View.GONE );
                } else {
                    showControl.getEditText().setVisibility( View.GONE );
                    showControl.getTv().setVisibility( View.GONE );
                }
            }

            if(formControl.getPCRelation()!=null) {
                String[] allDDChild = formControl.getPCRelation().split( "\\$" );
                FormControl showControl = null;
                String shoChildId[] = null;
                for (int i = 0; i < allDDChild.length; i++) {
                    if (!allDDChild[i].contains( selectValue )) {
                        String[] DDChild = allDDChild[i].split( "\\~" );
                        String childId[] = DDChild[1].split( "\\," );
                        for (int k = 0; k < childId.length; k++) {
                            showControl = ConfigurationCacheManager.getFormControlList().get( Integer.parseInt( childId[k] ) );
                            showControl.getTv().setVisibility( View.GONE );
                            if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue()== showControl.getDropDownValType()
                                    ||AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue()== showControl.getDropDownValType()) {
                                showControl.getSpinner().setSelection(0);
                                showControl.getSpinner().setVisibility( View.GONE );
                            } else {
                                showControl.getEditText().setText("");
                                showControl.getEditText().setVisibility( View.GONE );
                            }
                        }
                    } else {
                        String[] DDChild = allDDChild[i].split( "\\~" );
                        shoChildId = DDChild[1].split( "\\," );
                    }
                }
              if(shoChildId!=null && shoChildId.length>0){
                for (int k = 0; k < shoChildId.length; k++) {
                    showControl = ConfigurationCacheManager.getFormControlList().get( Integer.parseInt( shoChildId[k] ) );
                    showControl.getTv().setVisibility( View.VISIBLE );
                    if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue()== showControl.getDropDownValType()
                            ||AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue()== showControl.getDropDownValType()) {
                    //if (AppConstants.DATA_TYPE.DROPDOWN.getValue() == showControl.getDataType()) {
                        showControl.getSpinner().setVisibility( View.VISIBLE );
                    } else {
                        showControl.getEditText().setVisibility( View.VISIBLE );
                    }
                 }
                }
               }

            if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == formControl.getDropDownValType() && formControl.getChangeReload() != null) {
                String[] arr = formControl.getChangeReload().split( "\\," );
                String sql = "";
                String where = "";
                FormControl changedControl = null;

                for (int a = 0; a < arr.length; a++) {
                    sql = "";
                    where = "";

                    changedControl = ConfigurationCacheManager.getFormControlList().get(Integer.parseInt( arr[a]));

                    sql = changedControl.getDropDownValue();

                    if(changedControl.getKeyItem().equalsIgnoreCase( AppConstants.ZONE_ID_ALIAS)){
                        sql=UtilsTask.getZoneSql(sql,selectId,mAppPreferences.getZoneID());
                    }else if(changedControl.getKeyItem().equalsIgnoreCase( AppConstants.CLUSTER_ID_ALIAS)){
                        sql=UtilsTask.getClusterSql(sql,mAppPreferences.getCircleID(),selectId,mAppPreferences.getClusterID());
                    } else {
                        where = selectId;
                    }

                    DataBaseHelper db = new DataBaseHelper(MyApp.getAppContext());
                    db.open();

                    changedControl.getDdValues().clear();
                    ddSpinnerData(db.getCursor(sql,where),changedControl.getDdValues(),changedControl.getDdIds());

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( MyApp.getAppContext(),R.layout.spinner_text, changedControl.getDdValues() );
                    dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown );
                    changedControl.getSpinner().setAdapter( dataAdapter );
                    int pos = changedControl.getDdValues().indexOf(prvValues.get(formControl.getKeyItem()));
                    changedControl.getSpinner().setSelection( pos );
                    ConfigurationCacheManager.getFormControlList().put(Integer.parseInt( arr[a] ), changedControl );
                    db.close();
                }
            }
        }
    }

    public void ddSpinnerData(Cursor c, List<String> items, List<String> itemId) {

        int counter = 0;
        items.add(counter, "Select" );
        itemId.add(counter, "-1" );
        counter++;

        if (c != null) {
            while (c.moveToNext()) {
                if (!c.getString(0).toString().isEmpty()
                        && c.getString(0).toString() != null) {
                    if (prvValues.get(AppConstants.ClassModule).equalsIgnoreCase("EM")){
                        if (c.getString(0).equalsIgnoreCase("Planned")){

                        }else {
                            items.add(counter, c.getString(0));
                            itemId.add(counter, c.getString(1));
                            counter++;
                        }
                    }else {
                        items.add(counter, c.getString(0));
                        itemId.add(counter, c.getString(1));
                        counter++;
                    }

                }
            }
        }
    }

    public void addItemsOnSpinner(Spinner spinner, String dropDownValue,String selectValur ) {

        String[] arr = dropDownValue.split(AppConstants.COMMA);

        ArrayList<String> arrList = new ArrayList<String>();
        arrList.add( "Select" );
        if (arr.length > 0) {
            for (int aSize = 0; aSize < arr.length; aSize++) {
                    arrList.add(arr[aSize]);
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( MyApp.getAppContext(),R.layout.spinner_text, arrList );
        dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown );
        spinner.setAdapter( dataAdapter );
        int pos= arrList.indexOf(prvValues.get(selectValur));
        spinner.setSelection(pos);
    }

    public void setInitializeValue(int prePos,Spinner spinner,FormControl formControl){
        if(formControl.getInitialize()!=null && prePos == -1){
            int pos=formControl.getDdValues().indexOf(formControl.getInitialize());
            spinner.setSelection(pos);
        }

    }

}
