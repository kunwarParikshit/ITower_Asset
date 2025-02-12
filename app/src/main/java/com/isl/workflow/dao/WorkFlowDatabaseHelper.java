package com.isl.workflow.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.isl.constant.AppConstants;
import com.isl.modal.MenuDetail;
import com.isl.modal.MetaDataObject;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.UploadAssestDetail;
import com.isl.workflow.modal.UploadDocDetail;
import com.isl.workflow.utils.WorkFlowUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkFlowDatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDB = null;
    public static final int version = 47;
    Context context;

    public WorkFlowDatabaseHelper(Context con) {
        super(con, AppConstants.db_name, null, version);
        this.context = con;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public WorkFlowDatabaseHelper open() throws SQLException {
        sqLiteDB = getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteDB.close();
    }

    public void clearWorkFlowMetaData(String modifiedMetaDatas) {
        sqLiteDB.execSQL("DELETE FROM WORKFLOW_META_DATA WHERE TYPE IN (" + modifiedMetaDatas + ")");
    }

    public void insertWorkFlowMetaData(ArrayList<MetaDataObject> dataList) {
        for (MetaDataObject metaData : dataList) {

            ContentValues values = new ContentValues();
            values.put("TYPE", metaData.getType());
            values.put("ID", metaData.getId());
            values.put("VAL1", metaData.getVal1());
            values.put("VAL2", metaData.getVal2());
            values.put("VAL3", metaData.getVal3());
            values.put("VAL4", metaData.getVal4());
            values.put("VAL5", metaData.getVal5());
            values.put("VAL6", metaData.getVal6());
            values.put("VAL7", metaData.getVal7());
            values.put("VAL8", metaData.getVal8());
            values.put("VAL9", metaData.getVal9());
            values.put("VAL10", metaData.getVal10());
            values.put("VAL11", metaData.getVal11());
            values.put("VAL12", metaData.getVal12());
            values.put("VAL13", metaData.getVal13());
            values.put("VAL14", metaData.getVal14());
            values.put("VAL15", metaData.getVal15());

            sqLiteDB.insert("WORKFLOW_META_DATA", null, values);

        }
    }

    public void updateDataTimestamp(String dataTypeID) {
        sqLiteDB.execSQL("UPDATE WORKFLOW_DATA_TS SET SAVE_TIME=LOGIN_TIME WHERE TYPE_ID IN (" + dataTypeID + ")");
    }

    public List<DropdownValue> getDropdownList1(String SQL, String filedKey, String idProperty, String valProperty, String[] binVars) {

        List<DropdownValue> ddValues = new ArrayList<DropdownValue>();
        ddValues.add(WorkFlowUtils.getSelectDDValue());

        if (filedKey.equalsIgnoreCase("assetlist")) {
            ddValues.add(WorkFlowUtils.getNADDValue());
           if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("sid")) {
                Fields siteFields = FormCacheManager.getFormConfiguration().getFormFields().get("sid");
                FormFieldControl formControl = FormCacheManager.getFormControls().get(siteFields.getId());
               String etsId = ""; String value = "";
                if (formControl.getSelectedVal() != null && formControl.getSelectedVal().size() > 0) {
                    etsId = formControl.getSelectedVal().get(0).getId();
                    SQL = SQL.replaceAll("and","and val2 ="+ etsId +" and");

                }
            }
        }




        // String formControl1 = ""+ FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(String.valueOf(32)).getKey());
        System.out.println("===SQL11===" + SQL);  //superadmin tms@1234
        Cursor c = sqLiteDB.rawQuery(SQL, binVars);
        System.out.println("===SQL22===" + SQL);
        if (c.getCount() > 0) {
            //ddValues = new ArrayList<DropdownValue>();
            DropdownValue value = null;

            final int idIndex = c.getColumnIndex(idProperty);
            final int valIndex = c.getColumnIndex(valProperty);

            // If moveToFirst() returns false then cursor is empty
            /*if (!c.moveToFirst()) {
                return ddValues;
            }*/
            String[] colName = c.getColumnNames();

            while (c.moveToNext()) {
                value = new DropdownValue();
                value.setId(c.getString(idIndex));
                value.setValue(c.getString(valIndex));
                // String s = c.getString(idIndex) ;
                // String s2 = c.getString(valIndex) ;

                //System.out.println("===iddddd==="+c.getString(idIndex));
                //System.out.println("===Value==="+c.getString(valIndex));

                for (int index = 0; index < colName.length; index++) {
                    if (index == idIndex || index == valIndex) {
                        continue;
                    }
                    //System.out.print("Column Name - "+colName[index]);

                    switch (colName[index].toLowerCase()) {
                        case "val1":
                            value.setVal1(c.getString(index));
                            break;
                        case "val2":
                            value.setVal2(c.getString(index));
                            break;
                        case "val3":
                            value.setVal3(c.getString(index));
                            break;
                        case "val4":
                            value.setVal4(c.getString(index));
                            break;
                        case "val5":
                            value.setVal5(c.getString(index));
                            break;
                        case "val6":
                            value.setVal6(c.getString(index));
                            break;
                        case "val7":
                            value.setVal7(c.getString(index));
                            break;
                        case "val8":
                            value.setVal8(c.getString(index));
                            break;
                        case "val9":
                            value.setVal9(c.getString(index));
                            break;
                        case "val10":
                            value.setVal10(c.getString(index));
                            break;
                        case "val11":
                            value.setVal11(c.getString(index));
                            break;
                        case "val12":
                            value.setVal12(c.getString(index));
                            break;
                        case "val13":
                            value.setVal13(c.getString(index));
                            break;
                        case "val14":
                            value.setVal14(c.getString(index));
                            break;
                        case "val15":
                            value.setVal15(c.getString(index));
                            break;
                    }
                }
                ddValues.add(value);
                //System.out.println(value.getId()+" - "+value.getValue());
            }
        }
        return ddValues;
    }

    public List<DropdownValue> getDropdownList(String SQL, String idProperty, String valProperty, String[] binVars) {

        List<DropdownValue> ddValues = new ArrayList<DropdownValue>();
        ddValues.add(WorkFlowUtils.getSelectDDValue());
        // ddValues.add(WorkFlowUtils.getNADDValue());
        // String formControl1 = ""+ FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(String.valueOf(32)).getKey());
        System.out.println("===SQL11===" + SQL);  //superadmin tms@1234
        Cursor c = sqLiteDB.rawQuery(SQL, binVars);
        System.out.println("===SQL22===" + SQL);
        if (c.getCount() > 0) {
            //ddValues = new ArrayList<DropdownValue>();
            DropdownValue value = null;

            final int idIndex = c.getColumnIndex(idProperty);
            final int valIndex = c.getColumnIndex(valProperty);

            // If moveToFirst() returns false then cursor is empty
            /*if (!c.moveToFirst()) {
                return ddValues;
            }*/
            String[] colName = c.getColumnNames();

            while (c.moveToNext()) {
                value = new DropdownValue();
                value.setId(c.getString(idIndex));
                value.setValue(c.getString(valIndex));
                // String s = c.getString(idIndex) ;
                // String s2 = c.getString(valIndex) ;

                //System.out.println("===iddddd==="+c.getString(idIndex));
                //System.out.println("===Value==="+c.getString(valIndex));

                for (int index = 0; index < colName.length; index++) {
                    if (index == idIndex || index == valIndex) {
                        continue;
                    }
                    //System.out.print("Column Name - "+colName[index]);

                    switch (colName[index].toLowerCase()) {
                        case "val1":
                            value.setVal1(c.getString(index));
                            break;
                        case "val2":
                            value.setVal2(c.getString(index));
                            break;
                        case "val3":
                            value.setVal3(c.getString(index));
                            break;
                        case "val4":
                            value.setVal4(c.getString(index));
                            break;
                        case "val5":
                            value.setVal5(c.getString(index));
                            break;
                        case "val6":
                            value.setVal6(c.getString(index));
                            break;
                        case "val7":
                            value.setVal7(c.getString(index));
                            break;
                        case "val8":
                            value.setVal8(c.getString(index));
                            break;
                        case "val9":
                            value.setVal9(c.getString(index));
                            break;
                        case "val10":
                            value.setVal10(c.getString(index));
                            break;
                        case "val11":
                            value.setVal11(c.getString(index));
                            break;
                        case "val12":
                            value.setVal12(c.getString(index));
                            break;
                        case "val13":
                            value.setVal13(c.getString(index));
                            break;
                        case "val14":
                            value.setVal14(c.getString(index));
                            break;
                        case "val15":
                            value.setVal15(c.getString(index));
                            break;
                    }
                }
                ddValues.add(value);
                //System.out.println(value.getId()+" - "+value.getValue());
            }
        }
        return ddValues;
    }


    // *****get save time stamp*****************
    public String getModifedMetaDataTypeIds(String dataTypeIds) {

        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select TYPE_ID,LOGIN_TIME,SAVE_TIME from WORKFLOW_DATA_TS WHERE TYPE_ID IN ("
                        + dataTypeIds + ")", null);
        String dataTypeId = "";

        if (c != null) {

            String pattern = "dd-MMM-yyyy HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern,Locale.ENGLISH);
            Date saveDate = null;
            Date loginInDate = null;

            while (c.moveToNext()) {
                try {
                    //System.out.println("TYPE_ID - "+c.getString(0)+" : "+c.getString(1)+" : "+c.getString(2));
                    //System.out.println("dataTypeId - "+dataTypeId);
                    if (c.getString(2) == null) {
                        dataTypeId = dataTypeId + c.getString(0) + ",";
                        continue;
                    }

                    saveDate = formatter.parse(c.getString(2));
                    loginInDate = formatter.parse(c.getString(1));

                    if (loginInDate.compareTo(saveDate) > 0) {
                        dataTypeId = dataTypeId + c.getString(0) + ",";
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }

            if (dataTypeId.length() > 0) {
                dataTypeId = dataTypeId.substring(0, dataTypeId.length() - 1);
            }
        } else {
            dataTypeId = dataTypeIds;
        }
        return dataTypeId;
    }

    public void updateParamDesc(String PARAM_NAME, String PARAM_DESC, String TYPE, int flag) {
        if (flag == 1) {
            sqLiteDB.execSQL("UPDATE WORKFLOW_META_DATA SET VAL2 = " + PARAM_DESC + " WHERE VAL1 = ? AND TYPE = ?", new String[]{PARAM_NAME, TYPE});
        } else {
            sqLiteDB.execSQL("UPDATE WORKFLOW_META_DATA SET VAL2 = " + PARAM_DESC + " WHERE TYPE = ?", new String[]{TYPE});
        }
    }


    public boolean isSitelock(String PARAM_ID, String TYPE) {
        boolean isLock = false;
        Cursor c = sqLiteDB.rawQuery("Select VAL2 from WORKFLOW_META_DATA WHERE ID ='" + PARAM_ID + "'", null);
        String response = null;

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                response = c.getString(0);
            }
        }
        if (response != null && response.equalsIgnoreCase("0")) {
            isLock = true;
        }
        return isLock;
    }

    public String getProductId(String name, String TYPE) {
        boolean isLock = false;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(ID) from WORKFLOW_META_DATA WHERE VAL1='"
                        + name + "' AND TYPE='" + TYPE + "'", null );
        String response = null;

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                response = c.getString(0);
            }
        }

        return response;
    }


    // *****get save time stamp*****************
    public boolean isFormConfigurationModified(String formName) {
        boolean modified = false;

        Cursor c = sqLiteDB.rawQuery(
                "Select LOGIN_TIME,SAVE_TIME from WORKFLOW_DATA_TS WHERE NAME = '" + formName + "'", null);

        boolean isNewForm = true;

        if (c != null) {

            String pattern = "dd-MMM-yyyy HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
            Date saveDate = null;
            Date loginInDate = null;

            while (c.moveToNext()) {
                //System.out.println("Form Name - "+formName+", Login Time - "+c.getString(0)+", Save Time - "+c.getString(1));
                isNewForm = false;
                try {

                    if (c.getString(1) == null) {
                        modified = true;
                        break;
                    }

                    saveDate = formatter.parse(c.getString(1));
                    loginInDate = formatter.parse(c.getString(0));

                    if (loginInDate.compareTo(saveDate) > 0) {
                        modified = true;
                        break;
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }

        if (isNewForm) {
            modified = true;
        }
        return modified;
    }
    /*
    public Typeface typeface(String languageCode) {
        Typeface mynFont = null;
        if (languageCode.equalsIgnoreCase("my")) {
            mynFont = Typeface.createFromAsset(context.getAssets(),
                    "Myanmar3.ttf");
        } else {
            if(context.getApplicationContext().getPackageName().
                    equalsIgnoreCase("infozech.tawal")||
                    context.getApplicationContext().getPackageName().
                            equalsIgnoreCase("iz.tawal")){
                mynFont = Typeface.createFromAsset(context.getAssets(),
                        "TeshrinARLT-Regular.ttf");
            }else{
                mynFont = Typeface.DEFAULT;
            }
        }
        return mynFont;
    }
    */

    public List<MenuDetail> getSubMenuRight(String parentMenu) {
        List<MenuDetail> subMenuList = new ArrayList<MenuDetail>();
        Cursor c = null;
        c = sqLiteDB.rawQuery("Select MENU_ID,SUB_MENU,RIGHTS,MENU_CAPTION,SUBMENU_LINK from FORM_RIGHTS WHERE MAIN_MENU='"
                + parentMenu + "' ORDER BY SUB_MENU_SEQ", null);
        int a = c.getCount();
        if (c.getCount() > 0) {
            MenuDetail menu = null;

            while (c.moveToNext()) {
                menu = new MenuDetail();
                menu.setId(c.getString(0));
                menu.setName(c.getString(1));
                menu.setRights(Arrays.asList(c.getString(2).split(",")));
                menu.setCaption(c.getString(3));
                menu.setMenuLink(c.getString(4));
                subMenuList.add(menu);
            }
        }
        return subMenuList;
    }

    public void clearWorkFlowForm(String formName) {
        sqLiteDB.delete("MST_WORKFLOW_FORM", "FORM_NAME=?", new String[]{formName});
    }

    public void insertWorkFlowForm(String formConfiguration, String formName, boolean updateTimestamp) {
        ContentValues values = new ContentValues();
        values.put("FORM_NAME", formName);
        values.put("FORM_CONFIGURATION", formConfiguration);

        sqLiteDB.insert("MST_WORKFLOW_FORM", null, values);
        values.clear();

        //Update Form Modified Time.
        if (updateTimestamp) {
            //System.out.println("Updating timestamp for formName - "+formName);
            sqLiteDB.execSQL("UPDATE WORKFLOW_DATA_TS SET SAVE_TIME=LOGIN_TIME WHERE NAME = '" + formName + "'");

        }
    }

    public void updateWorkFlowForm(String formConfiguration, String formName, boolean updateTimestamp) {
        clearWorkFlowForm(formName);
        insertWorkFlowForm(formConfiguration, formName, updateTimestamp);
    }

    public String getWorkFlowForm(String formName) {
        Cursor c = sqLiteDB.rawQuery("Select FORM_CONFIGURATION from MST_WORKFLOW_FORM WHERE FORM_NAME ='" + formName + "'", null);
        String response = null;

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                response = c.getString(0);
            }
        }
        return response;
    }

    public void insertDataLocally(String flag, String txnData, String imgData, String userId) {
        ContentValues values = new ContentValues();
        values.put("FLAG", flag);
        values.put("USER_ID", userId);
        values.put("ACTIVITY_DATA", txnData);
        values.put("DATE", Utils.date());
        values.put("ALL_IMGS", imgData);
        sqLiteDB.insert("TXN_LOCAL_DATA", null, values);
    }

    public Cursor getWorkFlowImages(String id, int flag) {
        Cursor cursor = null;

        if (flag == 0) {
            cursor = sqLiteDB.rawQuery("Select ID,PATH,NAME,TAG,TIME_STAMP,LAT,LONGI from WORKFLOW_IMAGES WHERE ID ='" + id + "'", null);
        } else {
            cursor = sqLiteDB.rawQuery(
                    "Select * from WORKFLOW_IMAGES WHERE IMG_STATUS=1", null);
        }
        return cursor;
    }

    public boolean isAvailable(String tag) {
        Cursor cursor = null;
        boolean flag = false;
        cursor = sqLiteDB.rawQuery("Select * from WORKFLOW_IMAGES WHERE TAG ='" + tag + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            flag = true;
        }
        return flag;
    }

    public void insertImages(UploadDocDetail imgDetail, String status) {// ID is 201
        try {

            sqLiteDB.execSQL("INSERT INTO WORKFLOW_IMAGES (ID,PATH,NAME,TAG,TIME_STAMP,LAT,LONGI,STATUS) VALUES(?,?,?,?,?,?,?,?)",
                    new String[]{imgDetail.getId(), imgDetail.getPath(), imgDetail.getName(), imgDetail.getTag(), imgDetail.getTime(),
                            imgDetail.getLatitude(), imgDetail.getLongitude(), status});
        } catch (Exception ex) {
            Log.d("fdgh", "avi"+ex);

        }

    }

    public boolean insertImages1(UploadAssestDetail imgDetail, String status) { // ID is 34

        try {
        sqLiteDB.execSQL("INSERT INTO WORKFLOW_IMAGES (ID,PATH,NAME,TAG,TIME_STAMP,LAT,LONGI,STATUS) VALUES(?,?,?,?,?,?,?,?)",
                    new String[]{imgDetail.getId(), imgDetail.getAssestid(), imgDetail.getAssestName(), imgDetail.getAssetListid(), imgDetail.getAssestListName(),
                            imgDetail.getAssestDetails(), "", status});
            return true;
        } catch (Exception ex) {
            Log.d("fdgh", "avi"+ex);
            return false;
        }
    }

   public void deleteAssetDataImage(String id) {
        sqLiteDB.delete("WORKFLOW_IMAGES", "ID=?", new String[] { id });
    }

    public void clearImage() {
        sqLiteDB.delete("WORKFLOW_IMAGES", null, null);
    }

    public void retakeMedia(String name) {
        sqLiteDB.delete("WORKFLOW_IMAGES", "NAME=?", new String[]{name});
    }

    public void updateImages(String id, String name) {
        sqLiteDB.execSQL("UPDATE WORKFLOW_IMAGES SET STATUS = 3 WHERE ID = ? AND NAME = ?", new String[]{id, name});
    }

    public ArrayList<String> getPrePostAM() {
        String type = "-1276";
        ArrayList<String> inci_grp = null;
        inci_grp = new ArrayList<String>();
        inci_grp.add( "Select Work Done");
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(VAL1) from workflow_meta_data where type ='"
                        + type + "' ORDER BY UPPER(VAL1) ASC", null );
        if (c != null) {
            while (c.moveToNext()) {
                inci_grp.add( c.getString( c.getColumnIndex( "VAL1" ) ) );
            }
        }
        return inci_grp;
    }

    public String  getPrePostAMId(String selectedVal) {
        String id = null;
        String type = "-1276";
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(ID) from workflow_meta_data WHERE type='" + type + "' AND VAL1='" + selectedVal + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                id = c.getString( c.getColumnIndex( "ID" ) );
            }
        }
        return id;
    }
}


