package com.isl.itower;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.util.Utils;

import infozech.itower.R;

public class CustomGrid extends BaseAdapter {
	private Context mContext;
	AppPreferences mAppPreferences;

	public CustomGrid(Context c) {
		mContext = c;
		mAppPreferences = new AppPreferences(mContext);
	}

	@Override
	public int getCount() {
		return AppConstants.moduleList.size();
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
			grid = inflater.inflate( R.layout.griditem, null);
			ImageView iv = (ImageView) grid.findViewById(R.id.grid_image);
			TextView name = (TextView) grid.findViewById(R.id.name);
			name.setTypeface(Utils.typeFace(mContext));
			String s = "";
			if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")
					&& AppConstants.moduleList.get(position).getCaption()!=null
					&& AppConstants.moduleList.get(position).getCaption().length()>0){
				s = AppConstants.moduleList.get(position).getCaption();
			} else{
				s = Utils.msg(mContext,AppConstants.moduleList.get(position).getModuleId()+"");
			}

			name.setText( Html.fromHtml(s.replaceAll(" ","<br/>")));
			iv.setImageResource(AppConstants.moduleList.get(position).getModuleImg());

		} else {
			grid = (View) convertView;
		}
		return grid;
	}

	public void clear() {
		notifyDataSetChanged();
	}
}