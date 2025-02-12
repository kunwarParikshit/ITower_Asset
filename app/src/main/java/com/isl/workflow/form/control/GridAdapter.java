package com.isl.workflow.form.control;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.photo.camera.ViewVideoVideoView;
import com.isl.photo.camera.ViewVideoWebView;
import com.isl.util.Utils;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.UploadAssestDetail;
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

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    public List<UploadAssestDetail> imageList = Collections.emptyList();
    Context context;
    public ImageLoader loader;
    String id = "34";
    boolean hidden = false;

    public GridAdapter(List<UploadAssestDetail> imageList, Context context, String id,boolean hidden) {
        this.imageList = imageList;
        this.context = context;
        this.loader = ImageLoader.getInstance();
        this.id = id;
        this.hidden = hidden;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView delete,grid_image,play_video,doc_image;
        TextView tvTag,tvTimestamp,tvLat,tvLong;
        ConstraintLayout image_grid;

        public MyViewHolder(View view) {
            super(view);
            image_grid= view.findViewById( R.id.image_grid);
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate( R.layout.checklist_img_2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.delete.setVisibility(View.GONE);
        holder.delete.setImageResource( R.drawable.delete_icon);
        holder.grid_image.setVisibility(View.GONE);
        holder.image_grid.setVisibility(View.GONE);
        holder.play_video.setVisibility( View.GONE);
        holder.tvLat.setText(" ");
        holder.tvLong.setText(" ");
        holder.tvTimestamp.setText(" ");
        holder.tvTag.setText(" ");
        holder.grid_image.setVisibility(View.GONE);
        holder.play_video.setVisibility(View.GONE);
        if(!hidden){
            holder.delete.setVisibility(View.VISIBLE);
        }



        if(imageList!=null && imageList.size()>0 && imageList.get(position).getAssestid()!=null) {
            if(imageList.get(position).getAssestDetails()!=null){
                holder.tvTag.setText(  Utils.msg(context,"824")+" : "+imageList.get(position).getAssestDetails());
            }else{
                holder.tvTag.setVisibility(View.GONE);
            }

            if(imageList.get( position).getAssestListName()!=null){
                holder.tvTimestamp.setText(  Utils.msg(context,"823")+" : "+imageList.get(position).getAssestListName());
            }else{
                holder.tvTimestamp.setText(  Utils.msg(context,"823")+" : ");
            }

            if(imageList.get( position).getAssestid()!=null){
                holder.tvLat.setText(  Utils.msg(context,"821")+" : "+imageList.get(position).getAssestid());
            }else{
                holder.tvLat.setText(  Utils.msg(context,"821")+" : ");
            }

            if(imageList.get( position).getAssestName()!=null){
                holder.tvLong.setText(  Utils.msg(context,"822")+" : "+imageList.get(position).getAssestName());
            }else{
                holder.tvLong.setText(  Utils.msg(context,"822")+" : ");
            }
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAlert(context,"826",imageList,position);
                }
        });

    }
    @Override
    public int getItemCount()
    {
        return imageList.size();
    }





    public void confirmAlert(Context context,String confirmID,List<UploadAssestDetail> imageList,int pos) {
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
        title.setText( Utils.msg(context,confirmID)+" "+imageList.get(pos).getAssestName());

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(context);
                db.open();
                db.retakeMedia(imageList.get(pos).getAssestName());
                db.close();
                QRImageControl imageControl = new QRImageControl(context);
                imageControl.setData(id,false);
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
