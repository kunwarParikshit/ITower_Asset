package com.isl.audit.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
//import com.isl.app.auth.GlideApp;
import com.isl.audit.model.ImageModel;
import com.isl.audit.util.ImageRemoveListener;
import com.isl.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import infozech.itower.R;

public class AdapterCapturedImages extends RecyclerView.Adapter<AdapterCapturedImages.CustomViewHolder> {
    private List<ImageModel> imagesList;
    private Activity activity;
    private int attributePos ;
    private ImageRemoveListener imageRemoveListener;
    private boolean isViewOnly;
    private String imgBaseUrl;

    public AdapterCapturedImages(List<ImageModel> imagesList, Activity activity, ImageRemoveListener imageRemoveListener, int attributePos , boolean isViewOnly,String imgBaseUrl){
        this.activity=activity;
        this.imgBaseUrl=imgBaseUrl;
        this.imagesList=imagesList;
        this.attributePos=attributePos;
        this.isViewOnly=isViewOnly;
        this.imageRemoveListener=imageRemoveListener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(!TextUtils.isEmpty(imagesList.get(position).getPath())){
            Glide.with(activity).load(imagesList.get(position).getPath()).into(holder.ivCaptured);
           /* File file=new File(imagesList.get(position).getPath());
            if(file != null){
                Glide.with(activity).load(imagesList.get(position).getPath()).into(holder.ivCaptured);
            }*/
        }else if(!TextUtils.isEmpty(imagesList.get(position).getUrlPath())){
            String path=imagesList.get(position).getUrlPath();
            //path="https://203.122.7.134:5002/"+path;
            path=imgBaseUrl+path;
            Log.d("IMAGE_URL---",path);

            //GlideApp.with(activity).
            //        load(path)
           //         .into(holder.ivCaptured);


        }
        holder.ivRemove.setOnClickListener(v -> imageRemoveListener.onImageRemoveListener(holder.ivRemove,position,attributePos));

        holder.tvLat.setText(activity.getResources().getString(R.string.latitude)+" "+imagesList.get(position).getLatitude());
        holder.tvLong.setText(activity.getResources().getString(R.string.longitude)+" "+imagesList.get(position).getLongitude());
        holder.tvTimestamp.setText(activity.getResources().getString(R.string.timestamp)+" "+imagesList.get(position).getTime());
        holder.tvImgTag.setText(activity.getResources().getString(R.string.img_tag)+" "+imagesList.get(position).getTag());
        holder.ivFullView.setOnClickListener(v -> {
            final Dialog nagDialog = new Dialog(activity,
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
            ImageView ivFullImage = (ImageView) nagDialog.findViewById( R.id.iv_full_image );
            String path=imagesList.get(position).getPath();
            if(TextUtils.isEmpty(path)){
                //path="https://203.122.7.134:9000/"+imagesList.get(position).getUrlPath();
                //path="https://203.122.7.134:5002/"+imagesList.get(position).getUrlPath();
                path=imgBaseUrl+imagesList.get(position).getUrlPath();
            }
            Bitmap bm = Utils.decodeFile( path );
            if (bm != null) {
                imageView.setImageBitmap( bm );
                ivFullImage.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                ivFullImage.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                //GlideApp.with(activity).
                  //      load(path)
                 //       .into(ivFullImage);
               // imageView.setBackgroundColor( Color.parseColor( "#000000" ) );
            }
            btnClose.setOnClickListener(arg0 -> nagDialog.dismiss());
            nagDialog.show();
        });
        if(isViewOnly){
            holder.ivRemove.setVisibility(View.INVISIBLE);
        }else{
            holder.ivRemove.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.inflater_image_single_item;
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_remove)
        ImageView ivRemove;
        @BindView(R.id.iv_captured)
        ImageView ivCaptured;
        @BindView(R.id.iv_full_view)
        ImageView ivFullView;
        @BindView(R.id.tv_lat)
        TextView tvLat;
        @BindView(R.id.tv_long)
        TextView tvLong;
        @BindView(R.id.tv_timestamp)
        TextView tvTimestamp;
        @BindView(R.id.tv_img_tag)
        TextView tvImgTag;


        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
