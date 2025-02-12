package com.isl.taskform;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.isl.dao.cache.ConfigurationCacheManager;
import com.isl.constant.AppConstants;
import com.isl.itower.GPSTracker;
import com.isl.modal.FormControl;
import com.isl.photo.camera.ViewImage64;
import com.isl.util.FilePathFinder;
import com.isl.util.Utils;
import com.isl.workflow.utils.DateTimeUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import infozech.itower.R;

public class ImageControl implements OnClickListener {

    Button imgButton;
    Context ctx;
    FormControl imgControl;
    String latitude = "latitude",longitude = "longitude",location="";


    public ImageControl (Context ctx,FormControl imgControl,Button imgButton){
        this.ctx = ctx;
        this.imgButton = imgButton;
        this.imgControl=imgControl;

    }

    public void initliazeImageControl(){
        imgButton.setOnClickListener(this);
        imgButton.setVisibility( View.VISIBLE );
        imgControl.setImgCounter( 0 );
    }

    @Override
    public void onClick(View arg0) {
       GPSTracker gps = new GPSTracker( ctx);
      // String latitude = "latitude",longitude = "longitude",location="";
       if(!Utils.hasPermissions(ctx,AppConstants.PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Toast.makeText( ctx,"Permission denied for take pictures or access photos,media,files,device's location. Please Re-login.",Toast.LENGTH_LONG).show();
       }else if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
        }else{
            if(imgControl.getImgCounter() < ConfigurationCacheManager.getImageControl().getAfter()){
                if (gps.canGetLocation() == true) {
                    latitude = String.valueOf( gps.getLatitude() );
                    longitude = String.valueOf( gps.getLongitude() );
                    if ((latitude == null || latitude.equalsIgnoreCase( "0.0" ) || latitude.isEmpty())
                          || (longitude == null || longitude.equalsIgnoreCase( "0.0" ) || longitude.isEmpty())){
                    } else {
                        latitude = String.valueOf( gps.getLatitude() );
                        longitude = String.valueOf( gps.getLongitude() );
                    }
                    location = latitude+"$"+longitude;
                }

                if (getSiteID().equalsIgnoreCase("")||getSiteID().isEmpty()){
                    Utils.toastMsg(ctx, Utils.msg(ctx, "830"));
                }else {
                    if (ConfigurationCacheManager.getImageControl().getImgMsg().size() > imgControl.getImgCounter()) {
                        displayImageName(location);
                    } else {
                        imageCapture(location);
                    }
                }
            }else{
                String s = Utils.msg(ctx, "253") + " " + ConfigurationCacheManager.getImageControl().getAfter() + " " +
                        Utils.msg(ctx, "254");
                Utils.toastMsg(ctx, s);
            }
        }
    }

    public String getSiteID() {
        String siteID = "";
        for (FormControl formControl : ConfigurationCacheManager.getFormControlList().values()) {
            String key = formControl.getKeyItem();
            int dataType = formControl.getDataType();
            if (key.equalsIgnoreCase("sid") && dataType == 8) {
                siteID = formControl.getAutoCompleteTextView().getText().toString();
            } else if (key.equalsIgnoreCase("sid") && dataType == 1) {
                siteID = formControl.getEditText().getText().toString();
            }else {
                siteID="NA";
            }
        }
        return siteID;
    }

    public void displayImageName(final String location) {
        final String imgTag = imgControl.getImgMsg().get(imgControl.getImgCounter());
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(ctx, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        TextView tv_confirmation = (TextView) actvity_dialog.findViewById( R.id.tv_header);
        tv_confirmation.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        title.setTypeface( Utils.typeFace( ctx ) );
        title.setText( Utils.msg( ctx, "228" ) + " " + imgTag );
        imgControl.setCurrName( imgTag );
        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        positive.setTypeface( Utils.typeFace( ctx ) );
        positive.setText( Utils.msg( ctx, "7" ) );
        positive.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                imageCapture(location);
            }
        });

        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        negative.setTypeface( Utils.typeFace(ctx));
        negative.setText( Utils.msg( ctx,"8"));
        negative.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }

    private void imageCapture(String location) {
        try{
            imgControl.setImgLocation(location);
            ContentValues values = new ContentValues();
            values.put( MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imgControl.setCurrImageUri(ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, imgControl.getCurrImageUri());
            ((ActivityTaskForm)ctx).startActivityForResult(camera_intent, 2);
        }catch (Exception e) {
        }
    }


    public ViewImage64 onActivityResult(int requestCode, int resultCode, Intent data,String siteId) {

        ViewImage64 viewImg = null;

        try{

            if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

                String filePath = null;
                String imgName;
                if(siteId.length()>0){
                    imgName=siteId+"-"+imgControl.getImageNameTemplate()+"-"+Utils.getdateTime()+"_"+imgControl.getImgCounter() + ".jpg";
                }else{
                    imgName=imgControl.getImageNameTemplate()+"-"+Utils.getdateTime()+"_"+imgControl.getImgCounter() + ".jpg";
                }

                if(imgName.contains("/")){
                    imgName = imgName.replaceAll( "/","" );
                }

                if(imgName.contains("\\")){
                    imgName = imgName.replaceAll( "\\\\","" );
                }

                if(imgName.contains(":")){
                    imgName = imgName.replaceAll( ":","" );
                }

                JSONObject jsonImgObject = new JSONObject();

                if (imgControl.getCurrImageUri() != null)
                    filePath = FilePathFinder.getPath( ctx, imgControl.getCurrImageUri() );
                else {
                    Cursor cursor = ctx.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC" );
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Uri uri = Uri.parse( cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA ) ) );
                            filePath = uri.toString();
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }

                //ByteArrayOutputStream bos;
                FileOutputStream fos = null;
                File file = null;

                try {
                    //filePath = destination.getAbsolutePath();

                    Bitmap bm = Utils.decodeFile(filePath);
                    /*bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                    */

                    file = ctx.getExternalFilesDir( Environment.DIRECTORY_PICTURES );
                    file.mkdirs();
                    file = new File( file, imgName);
                    fos = new FileOutputStream( file );
                    String currTime = DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss")    ;
                    String waterMark;
                    if(siteId.equalsIgnoreCase("")||siteId.isEmpty()||siteId==null){
                         waterMark = Utils.msg(ctx, "809")+"-"+latitude+" \n"+Utils.msg(ctx, "810")+"-"+longitude+"" +
                                " \n"+Utils.msg(ctx, "829")+"-"+currTime+"\n"+Utils.msg(ctx, "828")+"-"+imgControl.getCurrName();

                    }else {
                        waterMark  = Utils.msg(ctx, "77") + "-" + siteId + " " +
                                "\n" + Utils.msg(ctx, "809") + "-" + latitude + " \n" + Utils.msg(ctx, "810") + "-" + longitude + "" +
                                " \n" + Utils.msg(ctx, "829") + "-" + currTime + "\n" + Utils.msg(ctx, "828") + "-" + imgControl.getCurrName();

                    }
                    Bitmap bitmap=Utils.mark(bm,waterMark);

                    bitmap.compress( Bitmap.CompressFormat.JPEG, 100, fos );

                   // bm.compress( Bitmap.CompressFormat.JPEG, 100, fos );

                    try {
                        /*view post images in grid*/
                        viewImg=new ViewImage64();
                        viewImg.setBitmap(bitmap);
                        viewImg.setTimeStamp(Utils.CurrentDateTime());
                        viewImg.setName(imgControl.getCurrName());
                        //viewImg.setName(data.getExtras().getString(AppConstants.IMG_MSG_ALIAS));

                        /*images*/
                        jsonImgObject.put("path",file);
                        jsonImgObject.put("name",imgName);
                        if(imgControl.getCurrName()==null){
                            imgControl.setCurrName(" ");
                        }
                        imgControl.setImgData(imgName+"$"+imgControl.getCurrName()+"$"+Utils.CurrentDateTime()+"$"+
                                imgControl.getImgLocation());
                        imgControl.getImgInfoArray().put(jsonImgObject);
                        imgControl.increaseImgCounter();
                        imgControl.setCurrName(" ");


                    } catch (Exception e) {
                        Toast.makeText(ctx, "Try again for capturing photo", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx,"Try again for capturing photo",Toast.LENGTH_LONG).show();
                } finally{

                    if(fos!=null){
                        fos.flush();
                        fos.close();
                    }
                }
            }

        }catch (Exception e) {
            //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return viewImg;
      }

    }
