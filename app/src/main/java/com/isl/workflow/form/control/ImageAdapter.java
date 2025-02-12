package com.isl.workflow.form.control;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.isl.photo.camera.ViewVideoVideoView;
import com.isl.photo.camera.ViewVideoWebView;
import com.isl.util.Utils;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.UploadDocDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.util.Collections;
import java.util.List;

import infozech.itower.R;

/**
 * Created by dhakan on 6/29/2020.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    public List<UploadDocDetail> imageList = Collections.emptyList();
    Context context;
    public ImageLoader loader;
    DisplayImageOptions op;
    String id = "201";
    private boolean mHasStableIds = false;


    public ImageAdapter(List<UploadDocDetail> imageList, Context context, String id,Boolean mHasStableIds) {
        this.imageList = imageList;
        this.context = context;
        this.loader = ImageLoader.getInstance();
        this.mHasStableIds =mHasStableIds;
        this.id = id;
        op = new DisplayImageOptions.Builder()
                .showStubImage( R.drawable.no_media_default )
                .showImageForEmptyUri( R.drawable.no_media_default )
                .showImageOnFail( R.drawable.no_media_default ).cacheInMemory()
                .cacheOnDisc().displayer( new RoundedBitmapDisplayer( 1 ) )
                .build();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView delete,grid_image,play_video,doc_image;
        TextView tvTag,tvTimestamp,tvLat,tvLong;
        ConstraintLayout imageGrid;

        public MyViewHolder(View view) {
            super(view);
            imageGrid=view.findViewById( R.id.image_grid);
            grid_image = (ImageView) view.findViewById( R.id.grid_image);
            doc_image = (ImageView) view.findViewById( R.id.grid_image1);
            delete = (ImageView) view.findViewById( R.id.delete);
            play_video = (ImageView) view.findViewById( R.id.play_video);
            tvLat = (TextView) view.findViewById( R.id.tv_lati);
            tvLong = (TextView) view.findViewById( R.id.tv_longi);
            tvTag = (TextView) view.findViewById( R.id.tv_tag);
            tvTimestamp = (TextView) view.findViewById( R.id.tv_time_stamp);
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate( R.layout.checklist_img, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.delete.setVisibility(View.GONE);
        holder.delete.setImageResource( R.drawable.delete_icon);
        holder.grid_image.setVisibility(View.GONE);
        holder.play_video.setVisibility( View.GONE);
        holder.tvLat.setText(" ");
        holder.tvLong.setText(" ");
        holder.tvTimestamp.setText(" ");
        holder.tvTag.setText(" ");

        if(imageList!=null && imageList.size()>0 && imageList.get(position).getPath()!=null) {
            if(imageList.get(position).getName()!=null){
                holder.tvTag.setText( Utils.msg(context,"473")+" "+imageList.get(position).getTag());
            }else{
                holder.tvTag.setText( Utils.msg(context,"473")+" ");
            }

            if(imageList.get( position).getTime()!=null){
                holder.tvTimestamp.setText( Utils.msg(context,"474")+" "+imageList.get(position).getTime());
            }else{
                holder.tvTimestamp.setText( Utils.msg(context,"474")+" ");
            }

            if(imageList.get( position).getLatitude()!=null){
                holder.tvLat.setText( Utils.msg(context,"215")+" : "+imageList.get(position).getLatitude());
            }else{
                holder.tvLat.setText( Utils.msg(context,"215")+" : ");
            }

            if(imageList.get( position).getLongitude()!=null){
                holder.tvLong.setText( Utils.msg(context,"216")+" : "+imageList.get(position).getLongitude());
            }else{
                holder.tvLong.setText( Utils.msg(context,"216")+" : ");
            }

            Bitmap bm = null;
            String path = imageList.get( position ).getPath();
            File isfile = new File( path );

            if (isfile.exists() && !path.contains( "http" )) {
                if (path.contains( ".jpeg" ) || path.contains( ".JPEG" )
                        || path.contains( ".jpg" ) || path.contains( ".JPG" )
                        || path.contains( ".png" ) || path.contains( ".PNG" )) {
                    bm = Utils.decodeFile( path );
                    holder.play_video.setTag( "1" );
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.play_video.setVisibility( View.VISIBLE);
                    holder.play_video.setImageResource( R.drawable.fullview);
                } else if (path.contains( ".mp4" ) || path.contains( ".MP4" )) {
                    bm = Utils.createVideoThumbNail( path );
                    holder.play_video.setTag( "2" );
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.play_video.setVisibility( View.VISIBLE);
                    holder.play_video.setImageResource( R.drawable.stop_video);
                }
                else if (path.contains(".doc") || path.contains(".DOC")
                        || path.contains(".txt") || path.contains(".TXT")
                        || path.contains(".pdf") || path.contains(".PDF")
                        || path.contains(".xlsx") || path.contains(".XLSX")
                        || path.contains(".pptx") || path.contains(".PPTX")
                        || path.contains(".xls") || path.contains(".XLS")
                        || path.contains(".ppt") || path.contains(".PPT")
                        || path.contains(".csv") || path.contains(".CSV")) {
                    holder.play_video.setTag("4");
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.VISIBLE);
                    holder.grid_image.setVisibility(View.GONE);
                    if (path.contains(".doc") || path.contains(".DOC")) {
                        holder.doc_image.setImageResource(R.drawable.image);
                    } else if (path.contains(".txt") || path.contains(".TXT")) {
                        holder.doc_image.setImageResource(R.drawable.image_txt);
                    } else if (path.contains(".pdf") || path.contains(".PDF")) {
                        holder.doc_image.setImageResource(R.drawable.image_pdf);
                    } else if (path.contains(".xlsx") || path.contains(".XLSX") || path.contains(".xls") || path.contains(".XLS")) {
                        holder.doc_image.setImageResource(R.drawable.image_xls);
                    } else if (path.contains(".pptx") || path.contains(".PPTX") || path.contains(".ppt") || path.contains(".PPT")) {
                        holder.doc_image.setImageResource(R.drawable.image_ppt);
                    } else if (path.contains(".csv") || path.contains(".CSV")) {
                        holder.doc_image.setImageResource(R.drawable.image_csv);
                    } else {
                        holder.doc_image.setImageResource(R.drawable.reports);
                    }
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.play_video.setImageResource(R.drawable.fullview);
                    //loader.init( ImageLoaderConfiguration.createDefault( context ) );
                    //loader.displayImage( path, holder.grid_image, op, null );
                }
                if (bm != null) {
                    holder.grid_image.setImageBitmap( bm );
                } else {
                    holder.grid_image.setBackgroundColor( Color.parseColor( "#000000" ) );
                }
            } else if (path.contains( "http" )) {
                if (path.contains( ".jpeg" ) || path.contains( ".JPEG" )
                        || path.contains( ".jpg" ) || path.contains( ".JPG" )
                        || path.contains( ".png" ) || path.contains( ".PNG" )) {
                    holder.play_video.setTag( "3" );
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.play_video.setVisibility( View.VISIBLE);
                    holder.play_video.setImageResource( R.drawable.fullview);
                    loader.init( ImageLoaderConfiguration.createDefault( context ) );
                    loader.displayImage( path, holder.grid_image, op, null );
                } else if (path.contains( ".mp4" ) || path.contains( ".MP4" )) {
                    holder.play_video.setTag( "4" );
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.grid_image.setBackgroundColor( Color.parseColor( "#000000" ) );
                    holder.play_video.setVisibility( View.VISIBLE);
                    holder.play_video.setImageResource( R.drawable.stop_video);
                }
                else if (path.contains(".doc") || path.contains(".DOC")
                        || path.contains(".txt") || path.contains(".TXT")
                        || path.contains(".pdf") || path.contains(".PDF")
                        || path.contains(".xlsx") || path.contains(".XLSX")
                        || path.contains(".pptx") || path.contains(".PPTX")
                        || path.contains(".xls") || path.contains(".XLS")
                        || path.contains(".ppt") || path.contains(".PPT")
                        || path.contains(".csv") || path.contains(".CSV")) {
                    holder.play_video.setTag("4");
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.VISIBLE);
                    holder.grid_image.setVisibility(View.GONE);
                    if (path.contains(".doc") || path.contains(".DOC")) {
                        holder.doc_image.setImageResource(R.drawable.image);
                    } else if (path.contains(".txt") || path.contains(".TXT")) {
                        holder.doc_image.setImageResource(R.drawable.image_txt);
                    } else if (path.contains(".pdf") || path.contains(".PDF")) {
                        holder.doc_image.setImageResource(R.drawable.image_pdf);
                    } else if (path.contains(".xlsx") || path.contains(".XLSX")||path.contains(".xls") || path.contains(".XLS")) {
                        holder.doc_image.setImageResource(R.drawable.image_xls);
                    } else if (path.contains(".pptx") || path.contains(".PPTX")||path.contains(".ppt") || path.contains(".PPT")) {
                        holder.doc_image.setImageResource(R.drawable.image_ppt);
                    } else if (path.contains(".csv") || path.contains(".CSV")) {
                        holder.doc_image.setImageResource(R.drawable.image_csv);
                    } else {
                        holder.doc_image.setImageResource(R.drawable.reports);
                    }
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.play_video.setImageResource(R.drawable.fullview);
                    //loader.init( ImageLoaderConfiguration.createDefault( context ) );
                    //loader.displayImage( path, holder.grid_image, op, null );
                }
            }
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imageList.remove(position);
                confirmAlert(context,"512",imageList,position);
               // confirmAlert(context,"512",imageList.get(position).getName());
               // backButtonAlert( 3, "512", "63", "64",
                //        gridId,photoType,imageList.get(position).getName(),imageList.get(position).getPath(),holder.play_video.getTag().toString());
            }
        });



        holder.play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaView(holder.play_video.getTag().toString(),imageList.get(position).getPath());
            }
        });

    }
    @Override
    public int getItemCount()
    {
        return imageList.size();
    }

    public void mediaView(String flag,String urlPath) {
        if (flag.equals( "1" )) {
            final Dialog nagDialog = new Dialog(context,
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen );
            nagDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
            nagDialog.setCancelable( true );
            nagDialog.setContentView( R.layout.image_zoom );
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom( nagDialog.getWindow().getAttributes() );
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            nagDialog.getWindow().setAttributes( lp );
            Button btnClose = (Button) nagDialog.findViewById( R.id.btnIvClose );
            ImageView imageView = (ImageView) nagDialog.findViewById( R.id.imageView1 );
            Bitmap bm = Utils.decodeFile( urlPath );
            if (bm != null) {
                imageView.setImageBitmap( bm );
            } else {
                imageView.setBackgroundColor( Color.parseColor( "#000000" ) );
            }
            btnClose.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            } );
            nagDialog.show();
        } else if (flag.equals( "2" )) {
            Intent i = new Intent( context, ViewVideoVideoView.class );
            i.putExtra( "path", urlPath );
            context.startActivity( i );
        } else if (flag.equals( "3" )) {
            final Dialog nagDialog = new Dialog( context,
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen );
            nagDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
            nagDialog.setCancelable( true );
            nagDialog.setContentView( R.layout.image_zoom );
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom( nagDialog.getWindow().getAttributes() );
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            nagDialog.getWindow().setAttributes( lp );
            Button btnClose = (Button) nagDialog.findViewById( R.id.btnIvClose );
            ImageView imageView = (ImageView) nagDialog.findViewById( R.id.imageView1 );
            loader.init( ImageLoaderConfiguration.createDefault( context) );
            loader.displayImage( urlPath, imageView, op, null );
            btnClose.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            } );
            nagDialog.show();
        } else if (flag.equals( "4" )) {
            Intent i = new Intent(context, ViewVideoWebView.class );
            i.putExtra( "path", urlPath );
            context.startActivity( i );
        }
    }



    public void confirmAlert(Context context,String confirmID,List<UploadDocDetail> imageList,int pos) {
        final Dialog actvity_dialog;


        actvity_dialog = new Dialog( context, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        // UI
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
        tv_header.setTypeface( Utils.typeFace( context ) );
        positive.setTypeface( Utils.typeFace( context) );
        negative.setTypeface( Utils.typeFace( context) );
        title.setTypeface( Utils.typeFace( context) );
        title.setText( Utils.msg(context,confirmID)+" "+imageList.get(pos).getTag());

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(context);
                db.open();
                db.retakeMedia(imageList.get(pos).getName());
                db.close();
                ImageControl imageControl = new ImageControl(context);
                imageControl.setImages(id);
                actvity_dialog.cancel();
            }
        } );
        negative.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        } );
    }

}
