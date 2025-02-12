package com.isl.workflow.form.control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.itower.GPSTracker;
import com.isl.util.HttpUtils;
import com.isl.videocompressor.MediaController;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.UploadDocDetail;
import com.isl.workflow.utils.DateTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CompressFile extends AsyncTask<Void, Void, File> {

    ProgressDialog pd;
    Context con;
    File file;
    AppPreferences mAppPreferences;
    UploadDocDetail docDetail;

    public CompressFile(Context con,File file,UploadDocDetail docDetail){
        this.con = con;
        this.file = file;
        mAppPreferences = new AppPreferences(con);
        this.docDetail = docDetail;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mAppPreferences.getIsVideoCompress() == 1 && docDetail.getType().equalsIgnoreCase("mp4")){
            pd = ProgressDialog.show( con, null, "Start compression..." );
        }else{
            pd = ProgressDialog.show( con, null, "Please wait..." );
        }
    }

    @Override
    protected File doInBackground(Void... voids) {
        if(mAppPreferences.getIsVideoCompress() == 1 && docDetail.getType().equalsIgnoreCase("mp4")){
            return MediaController.getInstance().convertVideo(file.getPath(),docDetail.getName());
        }else{
            return file;
        }

    }

    @Override
    protected void onPostExecute(File compressed) {
        super.onPostExecute(compressed);
        if(docDetail.getType().equalsIgnoreCase("mp4") && compressed != null){
            double fileSize = (double) compressed.length()/(1024 * 1024);
            double maxSize = (double) mAppPreferences.getVideoUploadMaxSize();
            DecimalFormat df2 = new DecimalFormat("#.##");
            df2.setRoundingMode( RoundingMode.UP);

            if(fileSize>maxSize){
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                if(fileSize>maxSize){
                   return;
                }
                return;
            }
        }else if(docDetail.getType().equalsIgnoreCase("mp4") && compressed == null){
            compressed = file;
        }

        try{
            WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper( con );
            db.open();
            GPSTracker gps = new GPSTracker( con);
            if (gps.canGetLocation() == true) {
                docDetail.setLatitude(String.valueOf( gps.getLatitude()));
                docDetail.setLongitude(String.valueOf( gps.getLongitude()));
            }
            String currTime = DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss")    ;
            docDetail.setTime(currTime);
            docDetail.setPath(compressed.toString());

            db.insertImages(docDetail,"1");

            JSONObject otherDetailObj = new JSONObject();
            otherDetailObj.put("module", AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleName());
            otherDetailObj.put(Constants.TXN_SOURCE,"M");
            otherDetailObj.put(AppConstants.USER_ID_ALIAS,mAppPreferences.getUserId());
            otherDetailObj.put(AppConstants.LANGUAGE_CODE_ALIAS,mAppPreferences.getLanCode());
            otherDetailObj.put(AppConstants.OPERATION,"A");

            AsynTaskUploadIMG task = new AsynTaskUploadIMG(con,docDetail,otherDetailObj.toString());
            task.execute(mAppPreferences.getConfigIP()+ WebAPIs.saveImage);

            ImageControl control = new ImageControl(con);
            control.setImages(docDetail.getId());
            db.close();

        } catch (Exception e) {

        }
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public class AsynTaskUploadIMG extends AsyncTask<String, Void, String> {
        public String res="",otherInfo;
        Context con;
        UploadDocDetail imgDetail;
        public AsynTaskUploadIMG(Context con, UploadDocDetail imgDetail, String otherInfo) {
            this.con = con;
            this.imgDetail=imgDetail;
            this.otherInfo = otherInfo;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(String... urls) {
            try {
                res = HttpUtils.httpMultipartBackground(urls[0],imgDetail,null,otherInfo);
                res = res.replace("[", "").replace("]", "");
            } catch (Exception e) {
                res = null;
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (res != null) {
                if(res.contains("success") && res.contains("data")){
                    try {
                        JSONObject reader = new JSONObject(res);
                        String success = reader.getString("success");
                        String data = reader.getString( "data" );
                        if(success.equalsIgnoreCase("S")) {
                            JSONObject reader1 = new JSONObject( data );
                            WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(con);
                            db.open();
                            db.updateImages(reader1.getString("id") ,
                                    reader1.getString("name"));
                            db.close();
                        }
                    }catch (JSONException e) {
                    }
                }
            }else {

            }
        }
    }
}