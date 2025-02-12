package com.isl.preventive;

import com.isl.modal.BeanGetImage;
import infozech.itower.R;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

//class ItemAdapter extends BaseAdapter {
class ItemAdapter extends BaseAdapter {
	protected ImageLoader loader = ImageLoader.getInstance();
	Context con;
	private LayoutInflater inflater = null;
	List<BeanGetImage> imageList;
	DisplayImageOptions op;
	ItemAdapter(Context con, List<BeanGetImage> imageList, DisplayImageOptions op) {
		this.con = con;
		this.op = op;
		this.imageList = imageList;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private class ViewHolder {
		public TextView tv_lat, tv_log, tv_img_name,tv_img_time_stamp,tv_defalt;
		public ImageView image;
	}

	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ViewHolder holder;
		if (convertView == null) {
			v = inflater.inflate(R.layout.custom_adapter, parent, false);
			holder = new ViewHolder();
			holder.tv_lat = (TextView) v.findViewById(R.id.tv_lat);
			holder.tv_log = (TextView) v.findViewById(R.id.tv_log);
			holder.image = (ImageView) v.findViewById(R.id.image);
			holder.tv_img_name = (TextView) v.findViewById(R.id.tv_img_name);
			holder.tv_img_time_stamp = (TextView) v.findViewById(R.id.tv_img_time_stamp);
			holder.tv_defalt = (TextView) v.findViewById(R.id.tv_defalt);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		if (imageList.get(position).getIMAGE_PATH() != null && imageList.get(position).getIMAGE_PATH() != "") {
			if (imageList.get(position).getLATITUDE() != null) {
				holder.tv_lat.setText("Latitude:" + imageList.get(position).getLATITUDE());
			} else {
				holder.tv_lat.setText("");
			}
			if (imageList.get(position).getLONGITUDE() != null) {
				holder.tv_log.setText("Longitude:" + imageList.get(position).getLONGITUDE());
			} else {
				holder.tv_log.setText("");
			}

			if (imageList.get(position).getIMAGENAME() != null) {
				holder.tv_img_name.setText("Image Tag:"	+ imageList.get(position).getIMAGENAME());
			} else {
				holder.tv_img_name.setText("");
			}
			
			if (imageList.get(position).getImgTimeStamp() != null) {
	            holder.tv_img_time_stamp.setText("Time Stamp:"+ imageList.get(position).getImgTimeStamp());
	            holder.tv_defalt.setBackgroundColor(Color.parseColor("#000000"));
				holder.tv_defalt.setVisibility( View.GONE );
			}else{
				holder.tv_defalt.setBackgroundColor(Color.parseColor("#ffffff"));
				holder.tv_defalt.setVisibility( View.GONE );
			}
			
			loader.init(ImageLoaderConfiguration.createDefault(con));
			//loader.displayImage(imageList.get(position).getImageURL()+imageList.get(position).getIMAGE_PATH(), holder.image, op, null);
			loader.displayImage(imageList.get(position).getIMAGE_PATH(), holder.image, op, null);

			holder.image.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					final Dialog nagDialog = new Dialog(con,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
					nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					nagDialog.setCancelable(true);
					nagDialog.setContentView(R.layout.image_zoom );
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(nagDialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					lp.height = WindowManager.LayoutParams.MATCH_PARENT;
					lp.gravity = Gravity.CENTER;
					nagDialog.getWindow().setAttributes(lp);
					Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
					ImageView imageView1 = (ImageView)nagDialog.findViewById(R.id.imageView1);
					loader.init(ImageLoaderConfiguration.createDefault(con));
					loader.displayImage(imageList.get(position).getIMAGE_PATH(), imageView1, op, null);
				    btnClose.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							nagDialog.dismiss();
						}
					});
					nagDialog.show();
				}
			});
		
		} else {
			holder.image.setVisibility(View.GONE);
			holder.tv_lat.setVisibility(View.GONE); 
			holder.tv_log.setVisibility(View.GONE); 
			holder.tv_img_name.setVisibility(View.GONE);
			holder.tv_img_time_stamp.setVisibility(View.GONE); 
		}
		return v;
	}

}
