package com.isl.incident;
import java.util.List;

import com.isl.dao.cache.AppPreferences;
import com.nostra13.universalimageloader.core.ImageLoader;
import infozech.itower.R;
import com.isl.modal.BeanRemarks;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterRemarks extends BaseAdapter {
	protected ImageLoader loader = ImageLoader.getInstance();
	Context con;
	private LayoutInflater inflater = null;
	List<BeanRemarks> remarks_list;
	AppPreferences mAppPreferences;
	public AdapterRemarks(Context con, List<BeanRemarks> data) {
		this.con = con;
		remarks_list = data;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAppPreferences = new AppPreferences(con);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return remarks_list.size();
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
	public View getView(int position, View arg1, ViewGroup parent) {
		View vi = arg1;
		if (arg1 == null)
		vi = inflater.inflate(R.layout.list_item_remarks, null);
		TextView tv_update_time = (TextView) vi.findViewById(R.id.tv_update_time);
		TextView tv_update_by= (TextView) vi.findViewById(R.id.tv_update_by);
		TextView tv_update_field = (TextView) vi.findViewById(R.id.tv_update_field);
		TextView tv_update_doc = (TextView) vi.findViewById(R.id.tv_update_doc);

		if(remarks_list.get(position).getDate()!=null){
			tv_update_time.setText(Html.fromHtml("<b>Updated On : </b> "+remarks_list.get(position).getDate()));
		}else{
			tv_update_time.setText(Html.fromHtml("<b>Updated On : </b> "));
		}

		if(remarks_list.get(position).getUser()!=null){
			tv_update_by.setText(Html.fromHtml("<b>Updated By : </b> "+remarks_list.get(position).getUser()));
		}else{
			tv_update_by.setText(Html.fromHtml("<b>Updated By : </b> "));
		}

		if(remarks_list.get(position).getRemarks().contains("Refrence ticket for resolution-TT")){
			final String [] ref_link = remarks_list.get(position).getRemarks().split("-");
			if(ref_link.length>=2){
				String s =ref_link[0]+" - "+"<a href=\"\">"+ref_link[1]+"</a>";
				tv_update_field.setText( Html.fromHtml(s));

				tv_update_field.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(con,TicketDetailsTabs.class);
						mAppPreferences.SetBackModeNotifi123(2);
						i.putExtra("id", ref_link[1]);
						i.putExtra("enableRca", "1");
						con.startActivity(i);

					}
				});

			}
		}else{
			tv_update_field.setText(Html.fromHtml("<b>Updated : </b> "+remarks_list.get(position).getRemarks()
					.replaceAll("<br>", "\n")));
		}
		if(remarks_list.get(position).getDocumentPath()!=null &&
				!remarks_list.get(position).getDocumentPath().equalsIgnoreCase("null")&&
				remarks_list.get(position).getDocumentPath().length()!=0){
			String s[] = remarks_list.get(position).getDocumentPath().split("\\\\");
			tv_update_doc.setText(Html.fromHtml("<b>Uploaded Document : </b> "+s[s.length-1]));
		}else{
			tv_update_doc.setText(Html.fromHtml("<b>Uploaded Document : </b> "));
		}
		return vi;
	}
}