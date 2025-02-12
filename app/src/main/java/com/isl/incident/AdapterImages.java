package com.isl.incident;
import java.util.List;

import com.isl.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import infozech.itower.R;
import com.isl.dao.cache.AppPreferences;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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

@SuppressLint("InflateParams")
public class AdapterImages extends BaseAdapter {
	protected ImageLoader loader = ImageLoader.getInstance();
	Context con;
	private LayoutInflater inflater = null;
	List<String> img_list,uploadTime,imgName,imgLatitute,imgLongitude;
	DisplayImageOptions op;
	AppPreferences mAppPreferences;


	public AdapterImages(Context con, List<String> data,List<String> time,List<String> imgName,List<String> imgLatitute,
						 List<String> imgLongitude,DisplayImageOptions op) {
		mAppPreferences = new AppPreferences(con);
		this.con = con;
		this.img_list = data;
		this.uploadTime = time;
		this.imgName = imgName;
		this.imgLatitute = imgLatitute;
		this.imgLongitude = imgLongitude;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.op = op;
		}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup parent) {
		View vi = arg1;
		if (arg1 == null)
			vi = inflater.inflate(R.layout.tt_image, null);
		    ImageView image = (ImageView) vi.findViewById(R.id.image);
		    TextView tv_img_name = (TextView) vi.findViewById(R.id.tv_img_name);
		    TextView tv_img_time_stamp = (TextView) vi.findViewById(R.id.tv_img_time_stamp);
		    TextView tv_img_lat = (TextView) vi.findViewById(R.id.tv_img_lat);
		    TextView tv_img_long = (TextView) vi.findViewById(R.id.tv_img_long);
		    TextView tv_defalt = (TextView) vi.findViewById(R.id.tv_defalt);

		    
		if (position<uploadTime.size() && uploadTime.get(position)!=null && !uploadTime.get(position).equalsIgnoreCase("null")) {
	           tv_img_time_stamp.setText( Utils.msg(con,"474" )+ uploadTime.get(position));
			   tv_defalt.setVisibility( View.GONE );
		}else{
			   tv_img_time_stamp.setText(Utils.msg(con,"474" ));
			   tv_defalt.setVisibility( View.GONE );
		}
		
		if (position<imgName.size() && imgName.get(position)!=null && !imgName.get(position).equalsIgnoreCase("null")) {
			tv_img_name.setText(Utils.msg(con,"473" )+ imgName.get(position));
	  	}else{
	  		tv_img_name.setText(Utils.msg(con,"473" ));
	  	}

		if (position<imgLatitute.size() && imgLatitute.get(position)!=null && !imgLatitute.get(position).equalsIgnoreCase("null")) {
			tv_img_lat.setText(Utils.msg(con,"215" )+": "+ imgLatitute.get(position));
		}else{
			tv_img_lat.setText(Utils.msg(con,"215" )+": ");
		}

		if (position<imgLongitude.size() && imgLongitude.get(position)!=null && !imgLongitude.get(position).equalsIgnoreCase("null")) {
			tv_img_long.setText(Utils.msg(con,"216" )+": "+ imgLongitude.get(position));
		}else{
			tv_img_long.setText(Utils.msg(con,"216" )+": ");
		}

		if (mAppPreferences.getLanCode().equalsIgnoreCase("my")) {
			image.setImageResource(R.drawable.no_media_bur);
		} else {
			image.setImageResource(R.drawable.no_media);
		}
		loader.init(ImageLoaderConfiguration.createDefault(con));
		loader.displayImage(img_list.get(position), image, op, null);
		image.setOnClickListener(new View.OnClickListener() {

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
				loader.displayImage(img_list.get(position), imageView1, op, null);
				btnClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
			      nagDialog.dismiss();
				  }
				});
				nagDialog.show();
			}
		});
		return vi;

	}
}
