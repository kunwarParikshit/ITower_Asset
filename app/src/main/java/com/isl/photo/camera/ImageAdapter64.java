package com.isl.photo.camera;
import infozech.itower.R;

import java.util.LinkedHashMap;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter64 extends BaseAdapter {
	private Context mContext;
	//ArrayList<Bitmap> Image;
	LinkedHashMap<String, ViewImage64> lhmImages;
	public ImageAdapter64(Context c,LinkedHashMap<String, ViewImage64> lhmImages) {
	this.lhmImages=lhmImages;
	mContext = c;
	}

	@Override
	public int getCount() {
	return lhmImages.size();
	}

	@Override
	public Object getItem(int position) {
	return null;
	}
	@Override
	public long getItemId(int position) {
	return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)throws OutOfMemoryError {
	View grid;
	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	if (convertView == null) {
	grid = new View(mContext);
	grid = inflater.inflate(R.layout.view_img_64, null);
	ImageView iv = (ImageView) grid.findViewById(R.id.grid_image);
	TextView tv_time_stamp = (TextView) grid.findViewById(R.id.tv_time_stamp);
	TextView tv_name = (TextView) grid.findViewById(R.id.tv_name);
	int a=position+1;
	iv.setImageBitmap(lhmImages.get(""+a).getBitmap());
	//iv.setImageURI( lhmImages.get(""+a).getUri() );
	tv_time_stamp.setText(lhmImages.get(""+a).getTimeStamp());
	tv_name.setText(lhmImages.get(""+a).getName());
	} else {
	grid = (View) convertView;
	}
	return grid;
	}
	public void clear() {
	notifyDataSetChanged();
	}
    }