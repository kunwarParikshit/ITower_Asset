package com.isl.workflow.form.control;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.isl.constant.AppConstants;
import com.isl.dao.cache.AppPreferences;
import com.isl.hsse.HsseConstant;
import com.isl.hsse.HsseFrame;
import com.isl.itower.GPSTracker;
import com.isl.photo.camera.ViewImage64;
import com.isl.util.FilePathFinder;
import com.isl.util.Utils;
import com.isl.workflow.FormActivity;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.UploadDocDetail;
import com.isl.workflow.utils.DateTimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

public class ImageControl {

    Context ctx;
    private Uri imageUri;
    AppPreferences mAppPreferences;
    private String source;
    GPSTracker gps;
    String siteID = "";

    public ImageControl() {
    }

    public ImageControl(Context ctx) {
        this.ctx = ctx;
        this.mAppPreferences = new AppPreferences(ctx);
        gps = new GPSTracker(ctx);
    }

    public Button cameraBt(Context context, Fields field) {

        Button bt = new Button(ctx);
        bt.setId(Integer.parseInt(field.getId()));
        bt.setTag(field.getKey());
        imageButtonProperty(bt, field.getId(), field.getKey());
        FormCacheManager.getFormControls().get(field.getId()).setButtonCtrl(bt);
        FormCacheManager.getFormControls().get(field.getId()).setImgCounter(0);
        if (field.isDisabled()) {
            FormCacheManager.getFormControls().get(field.getId())
                    .getButtonCtrl().setEnabled(false);
        }
        if (field.isHidden()) {
            FormCacheManager.getFormControls().get(field.getId())
                    .getButtonCtrl().setVisibility(View.INVISIBLE);
        }
        return bt;
    }

    public RecyclerView recyclerView(Context context, Fields field) {

        RecyclerView imgGrid = new RecyclerView(ctx);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL,
                false);
        imgGrid.setLayoutManager(horizontalLayoutManager);
        FormCacheManager.getFormControls().get(field.getId()).setImgGridView(imgGrid);

        if (FormCacheManager.getPrvFormData().containsKey(field.getKey())) {
            initializePreviousImages(field.getId(), field.getKey());
        }
        source = (String) FormCacheManager.getPrvFormData().get(Constants.TXN_SOURCE);
        return imgGrid;
    }

    public void displayImageName(String id, String key) {
        final String mediaType;
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(ctx, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        TextView tv_confirmation = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_confirmation.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        title.setTypeface(Utils.typeFace(ctx));

        if (FormCacheManager.getFormConfiguration().getFormFields().get(key).getImgTag().size() >
                FormCacheManager.getFormControls().get(id).getImgCounter()) {
            //int index = 0;
            String tag = "";
            for (String tempvalue : FormCacheManager.getFormConfiguration().getFormFields().get(key).getImgTag()) {
                WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(ctx);
                db.open();
                db.isAvailable(tempvalue);
                if (!db.isAvailable(tempvalue)) {
                    tag = tempvalue;
                    break;
                }
                db.close();
                // index++;
            }

            title.setText(Utils.msg(ctx, "228") + " " + tag);
            //title.setText( Utils.msg( ctx, "228" ) + " " + FormCacheManager.getFormConfiguration().getFormFields().get(key).getImgTag()
            //        .get(FormCacheManager.getFormControls().get(id).getImgCounter()));

        } else {
            title.setText(Utils.msg(ctx, "228") + " media" + FormCacheManager.getFormControls().get(id).getImgCounter());
        }

        if (FormCacheManager.getFormConfiguration().getFormFields().get(key).getMediaFormate() != null
                && FormCacheManager.getFormConfiguration().getFormFields().get(key).getMediaFormate().size() >
                FormCacheManager.getFormControls().get(id).getImgCounter()) {
            mediaType = FormCacheManager.getFormConfiguration().getFormFields().get(key).getMediaFormate()
                    .get(FormCacheManager.getFormControls().get(id).getImgCounter());

        } else {
            mediaType = "jpg";
        }

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(ctx));
        positive.setText(Utils.msg(ctx, "7"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                imageCapture(id, mediaType);
                //  setImages(id);
            }
        });

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(ctx));
        negative.setText(Utils.msg(ctx, "8"));
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }

    private void imageCapture(String field, String mediaType) {
        try {
            System.out.println("************* id - " + field);
            Intent camera_intent;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (mediaType.equalsIgnoreCase("mp4")) {
                camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);// set the image file name
                camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
            } else {
                camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }

            if (AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleId()
                    == HsseConstant.HSSE_MODULE_ID) {
                ((HsseFrame) ctx).startActivityForResult(camera_intent, Integer.parseInt(field));
            } else {
                ((FormActivity) ctx).startActivityForResult(camera_intent, Integer.parseInt(field));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    public void getResult(int requestCode, int resultCode, Intent data){
        onActivityResult(requestCode, resultCode)
    }*/

    public void onActivityResult(int requestCode, int resultCode) {
        // onActivityResult(requestCode, resultCode);
        try {
            System.out.print("************ resultCode " + resultCode);
            if (resultCode == Activity.RESULT_OK) {
                //String tag = "media";

                List<String> imgTags = FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get("" + requestCode).getKey()).getImgTag();
                //List<String> fileFormate = FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(""+requestCode).getKey()).getMediaFormate();

                UploadDocDetail docDetail = new UploadDocDetail();
                docDetail.setId(requestCode + "");

                //System.out.print("************ Image Id "+docDetail.getId());

                if (imgTags.size() > FormCacheManager.getFormControls().get("" + requestCode).getImgCounter()) {
                    String tag = "";
                    for (String tempvalue : imgTags) {
                        WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(ctx);
                        db.open();
                        db.isAvailable(tempvalue);
                        if (!db.isAvailable(tempvalue)) {
                            tag = tempvalue;
                            break;
                        }
                        db.close();
                    }
                    docDetail.setTag(tag);
                    //docDetail.setTag(imgTags.get(FormCacheManager.getFormControls().get(""+requestCode).getImgCounter()));
                } else {
                    docDetail.setTag("media" + FormCacheManager.getFormControls().get("" + requestCode).getImgCounter());
                }

                if (FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get("" + requestCode).getKey()).getMediaFormate() != null
                        && FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get("" + requestCode).getKey())
                        .getMediaFormate().size() > FormCacheManager.getFormControls().get("" + requestCode).getImgCounter()) {
                    docDetail.setType(FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get("" + requestCode).getKey()).getMediaFormate().get(FormCacheManager.getFormControls().get("" + requestCode).getImgCounter()));
                } else {
                    docDetail.setType("jpg");
                }


                //System.out.print("************ Image Tag "+docDetail.getTag());

                docDetail.setName(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getShortName() + "_" + docDetail.getTag() + "_" + System.currentTimeMillis());

                if (imageUri != null) {
                    docDetail.setPath(FilePathFinder.getPath(ctx, imageUri));
                } else {

                    Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                            docDetail.setPath(uri.toString());
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }

                FileOutputStream fos = null;
                File newfile = null;
                try {
                    //String rootPath = Environment.getExternalStorageDirectory() + AppConstants.PIC_PATH;
                    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (docDetail.getType().equalsIgnoreCase("jpg")
                            || docDetail.getType().equalsIgnoreCase("jpeg")
                            || docDetail.getType().equalsIgnoreCase("png")) {
                        docDetail.setName(docDetail.getName() + ".jpg");

                        Bitmap bm = Utils.decodeFile(docDetail.getPath());
                        String currTime = DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss");
                        String latitude = String.valueOf(gps.getLatitude());
                        String longitude = String.valueOf(gps.getLongitude());

                        if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                                || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                            return;
                        }
                        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("sid")) {
                            Fields fields = FormCacheManager.getFormConfiguration().getFormFields().get("sid");
                            if (FormCacheManager.getFormControls().get(fields.getId()).getValue() != null) {
                                siteID = FormCacheManager.getFormControls().get(fields.getId()).getValue();
                            } else {
                                siteID = FormCacheManager.getFormControls().get(fields.getId()).getAutoCompleteCtrl().getText().toString();
                            }
                        }
                        String waterMark = Utils.msg(ctx, "77") + "-" + siteID + " " +
                                "\n" + Utils.msg(ctx, "809") + "-" + latitude + " \n" + Utils.msg(ctx, "810") + "-" + longitude + "" +
                                " \n" + Utils.msg(ctx, "829") + "-" + currTime + "\n" + Utils.msg(ctx, "828") + "-" + docDetail.getTag();

                        Bitmap bitmap = Utils.mark(bm, waterMark);
                        newfile = folder;

                        //newfile = new File( rootPath );
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        newfile = new File(newfile, docDetail.getName());
                        fos = new FileOutputStream(newfile);
                        newfile.createNewFile();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }
                    } else if (docDetail.getType().equalsIgnoreCase("mp4")) {
                        //String tempPath = Environment.getExternalStorageDirectory() + AppConstants.MEDIA_TEMP_PATH;
                        File folder1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                        docDetail.setName(docDetail.getName() + ".mp4");
                        File currentFile = new File(docDetail.getPath());
                        //File directory = new File(tempPath);
                        File directory = folder1;
                        newfile = new File(directory, docDetail.getName());
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        if (currentFile.exists()) {
                            InputStream in = new FileInputStream(currentFile);
                            OutputStream out = new FileOutputStream(newfile);
                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            if (in != null) {
                                in.close();
                            }
                            if (out != null) {
                                out.close();
                            }
                        }
                    }
                    new CompressFile(ctx, newfile, docDetail).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, "Try again for capturing photo", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void imageButtonProperty(Button ib, String id, String fieldKey) {

        final float scale = ctx.getResources().getDisplayMetrics().density;
        int margin = (int) (50 * scale);
        LinearLayout.LayoutParams CapturingPicIbParam = new LinearLayout.LayoutParams(margin, margin);
        CapturingPicIbParam.gravity = Gravity.CENTER_HORIZONTAL;
        CapturingPicIbParam.setMargins(12, 20, 12, 12);
        ib.setLayoutParams(CapturingPicIbParam);
        ib.setBackgroundResource(R.drawable.camera);

        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!Utils.hasPermissions(ctx, AppConstants.PERMISSIONS)
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Toast.makeText(ctx, "Permission denied for take pictures or access photos,media,files,device's location. Please Re-login.", Toast.LENGTH_LONG).show();
                } else if (gps.canGetLocation() == false) {
                    gps.showSettingsAlert();
                } else {
                    if (FormCacheManager.getFormControls().get(id).getImgCounter()
                            < FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getValidations().getMax()) {
                        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("sid")) {
                            Fields fields = FormCacheManager.getFormConfiguration().getFormFields().get("sid");
                            if (FormCacheManager.getFormControls().get(fields.getId()).getValue() != null) {
                                siteID = FormCacheManager.getFormControls().get(fields.getId()).getValue();
                            } else {
                                siteID = FormCacheManager.getFormControls().get(fields.getId()).getAutoCompleteCtrl().getText().toString();
                            }
                        }
                        if (siteID.isEmpty() || siteID.equalsIgnoreCase("")) {
                            Utils.toastMsg(ctx, Utils.msg(ctx, "830"));
                        } else {
                            displayImageName(String.valueOf(ib.getId()), ib.getTag().toString());
                        }
                    } else {
                        String s = Utils.msg(ctx, "253") + " " + FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getValidations().getMax() + " " +
                                Utils.msg(ctx, "254");
                        Utils.toastMsg(ctx, s);
                    }
                }
            }
        });
    }

    public void setImages(String id) {

        WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(ctx);
        db.open();
        Cursor cursor = db.getWorkFlowImages(id, 0);
        if (cursor != null) {
            ArrayList imgViewList = new ArrayList<ViewImage64>();
            //FromCacheManager.getFormControls().get(id).getImgViewList().clear();
            FormCacheManager.getFormControls().get(id).setImgCounter(0);
            while (cursor.moveToNext()) {
                UploadDocDetail viewImg = null;

                if (cursor.getString(1) != null && cursor.getString(1).length() > 0) {
                    viewImg = new UploadDocDetail();
                    viewImg.setPath(cursor.getString(1));
                    viewImg.setName(cursor.getString(2));
                    viewImg.setTag(cursor.getString(3));
                    viewImg.setTime(cursor.getString(4));
                    viewImg.setLatitude(cursor.getString(5));
                    viewImg.setLongitude(cursor.getString(6));
                    imgViewList.add(viewImg);
                    //System.out.println("*********************Adding image in list - "+cursor.getString(2 ));
                    //FormCacheManager.getFormControls().get(id).setImgViewList(imgViewList);
                    FormCacheManager.getFormControls().get(id).increaseImgCounter();
                }
            }
            FormCacheManager.getFormControls().get(id).setImgCounter(imgViewList.size());
            FormCacheManager.getFormControls().get(id).getImgGridView().setLayoutManager(new LinearLayoutManager(ctx));
            ImageAdapter imageAdapter = new ImageAdapter(imgViewList, ctx, id, false);
            FormCacheManager.getFormControls().get(id).getImgGridView().setAdapter(imageAdapter);

        }
        db.close();
    }

    public void initializePreviousImages(String fieldId, String fieldKey) {

        if (FormCacheManager.getPrvFormData().get(fieldKey) != null && !((String) FormCacheManager.getPrvFormData().get(fieldKey)).equalsIgnoreCase("null")) {
            Type listType = new TypeToken<List<UploadDocDetail>>() {
            }.getType();
            List<UploadDocDetail> imgList = Constants.gson.fromJson((String) FormCacheManager.getPrvFormData().get(fieldKey), listType);

            if (imgList != null && imgList.size() > 0) {
                WorkFlowDatabaseHelper dbHelper = new WorkFlowDatabaseHelper(ctx);
                dbHelper.open();
                dbHelper.deleteAssetDataImage(fieldId);

                for (UploadDocDetail docDetail : imgList) {
                    docDetail.setId(fieldId);
                    docDetail.setPath(FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getData().getService().getUrl() + docDetail.getName());
                    dbHelper.insertImages(docDetail, "3");
                }
                dbHelper.close();
                setImages(fieldId);
            }
        } else {
            WorkFlowDatabaseHelper dbHelper = new WorkFlowDatabaseHelper(ctx);
            dbHelper.open();
            dbHelper.deleteAssetDataImage(fieldId);
            setImages(fieldId);
        }
    }

}


