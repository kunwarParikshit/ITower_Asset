package com.isl.workflow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.workflow.modal.ImagesList;
import com.isl.workflow.utils.DataSubmitUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import infozech.itower.R;

public class PreChecklistAdapter extends RecyclerView.Adapter<PreChecklistAdapter.ViewHolder> {
   ArrayList<ImagesList> imagesList;
   Context mcontext;
   String CODE;
   String CAMERA_CODE;

    public PreChecklistAdapter(Context context, ArrayList<ImagesList> imagesList,String CODE,String CAMERA_CODE) {
       this.mcontext = context;
       this.imagesList = imagesList;
       this.CODE = CODE;
       this.CAMERA_CODE = CAMERA_CODE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_pre_checklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position1) {
      ImagesList images = imagesList.get(position1);
       holder.txtview_fieldName.setText(images.getFiledkey());
      if(images.getImageUri() != null)
      {
          Picasso.with(mcontext).load(images.getImageUri()).into(holder.imgview_show);
      }
      else {
          Picasso.with(mcontext).load(R.drawable.ic_placeholder_image).placeholder(R.drawable.ic_placeholder_image).into(holder.imgview_show);
      }
      holder.imgview_upload.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              DataSubmitUtils.position = holder.getAdapterPosition();
              Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                      android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
              ((FormActivity) mcontext).startActivityForResult(pickPhoto,Integer.parseInt(CODE));
          }
      });

      holder.imgview_camera.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              DataSubmitUtils.position = holder.getAdapterPosition();
              Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
              ((FormActivity) mcontext).startActivityForResult(cameraIntent,Integer.parseInt(CAMERA_CODE));
          }
      });

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
         TextView txtview_fieldName;
         ImageView imgview_upload,imgview_show,imgview_camera;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtview_fieldName = itemView.findViewById(R.id.txtview_fieldName);
            imgview_upload = itemView.findViewById(R.id.imgview_upload);
            imgview_show = itemView.findViewById(R.id.imgview_show);
            imgview_camera = itemView.findViewById(R.id.imgview_camera);
        }
    }

}
