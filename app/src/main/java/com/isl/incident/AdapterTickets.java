package com.isl.incident;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.BeansTicketList;
import com.isl.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import infozech.itower.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AdapterTickets extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeansTicketList data_list;
	AppPreferences mAppPreferences;
	String sName,module;
	ProgressDialog progressDialog;
	AlertDialog alertDialog;

	public AdapterTickets(Context con, String data, String module) {
		this.con = con;
		this.module = module;
		Gson g = new Gson();
		this.data_list = g.fromJson(data, BeansTicketList.class);
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAppPreferences=new AppPreferences(con);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data_list.getTicket_list().size();
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
		vi = inflater.inflate(R.layout.list_item_ticket, null);
		progressDialog = new ProgressDialog(con);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);




		if(mAppPreferences.getSiteNameEnable()==1 && data_list.getTicket_list().get(position).getsName()!=null){
			sName="("+data_list.getTicket_list().get(position).getsName()+")";
		}else{
			sName="";
		}

		RelativeLayout ticket_info = (RelativeLayout)  vi.findViewById(R.id.ticket_info);
		RelativeLayout relative_ra = vi.findViewById(R.id.relative_ra);
		RelativeLayout relative_main = vi.findViewById(R.id.relative_main);
		Button btn_reject = vi.findViewById(R.id.btn_reject);
		Button btn_acknowledge = vi.findViewById(R.id.btn_acknowledge);

		if(data_list.getTicket_list().get(position).getDigital_dis()!=null)
		{
			//relative_main.setBackgroundColor(con.getResources().getColor(R.color.light_orange));
			relative_ra.setVisibility(View.VISIBLE);
			relative_ra.setBackgroundColor(con.getResources().getColor(R.color.light_orange));
			ticket_info.setBackgroundColor(con.getResources().getColor(R.color.light_orange));
			if(data_list.getTicket_list().get(position).getTktaction()!=null)
			{
				btn_acknowledge.setVisibility(View.GONE);
				btn_reject.setVisibility(View.GONE);
			}
			else
			{
				btn_acknowledge.setVisibility(View.VISIBLE);
				btn_reject.setVisibility(View.VISIBLE);
			}
		}
		else
		{
		//	relative_main.setBackground(con.getResources().getDrawable(R.drawable.list_bg));
			relative_ra.setBackgroundColor(con.getResources().getColor(R.color.white));
			ticket_info.setBackgroundColor(con.getResources().getColor(R.color.white));
			relative_ra.setVisibility(View.GONE);
		}
//		if(data_list.getTicket_list().get(position).getColor()!=null &&
//				data_list.getTicket_list().get(position).getColor().contains( "#" )){
//			ticket_info.setBackgroundColor( Color.parseColor(data_list.getTicket_list().get(position).getColor().trim()));
//		}else {
//			ticket_info.setBackgroundColor( Color.parseColor( "#FFFFFF" ));
//		}


		TextView txt_date = (TextView) vi.findViewById(R.id.txt_date);
		txt_date.setText(data_list.getTicket_list().get(position).getTICKET_DATE());
		txt_date.setTypeface(Utils.typeFace(con));

		TextView txt_ticket_id = (TextView) vi.findViewById(R.id.txt_ticket_id);
		txt_ticket_id.setText(data_list.getTicket_list().get(position).getTICKET_ID());
		txt_ticket_id.setTypeface(Utils.typeFace(con));

		TextView txt_site_id = (TextView) vi.findViewById(R.id.txt_site_id);
		txt_site_id.setText(data_list.getTicket_list().get(position).getSITE_ID()+sName);
		txt_site_id.setTypeface(Utils.typeFace(con));

		TextView txt_ticket_status = (TextView) vi.findViewById(R.id.txt_ticket_status);
		txt_ticket_status.setTypeface(Utils.typeFace(con));

		if(module.equalsIgnoreCase("955")){
             TextView txt_fLevel = (TextView) vi.findViewById(R.id.txt_fLevel);
			 TextView txt_sLevel = (TextView) vi.findViewById(R.id.txt_sLevel);
			 //if(!data_list.getTicket_list().get(position).getTICKET_STATUS().equalsIgnoreCase("Open")){
			 	 if(data_list.getTicket_list().get(position).getFirstLevel()!=null){
					 txt_fLevel.setVisibility(View.VISIBLE);
					 txt_fLevel.setText(Utils.msg(con,"555")+":"+data_list.getTicket_list().get(position).getFirstLevel());
				 }else{
					 txt_fLevel.setVisibility(View.GONE);
				 }

				 if(data_list.getTicket_list().get(position).getSecondLevel()!=null){
					 txt_sLevel.setVisibility(View.VISIBLE);
					 txt_sLevel.setText(Utils.msg(con,"556")+":"+data_list.getTicket_list().get(position).getSecondLevel());
				 }else{
					 txt_sLevel.setVisibility(View.GONE);
				 }
			 /*}else{
				 txt_fLevel.setVisibility(View.GONE);
				 txt_sLevel.setVisibility(View.GONE);
			 }*/
		}

	  	txt_ticket_status.setText(data_list.getTicket_list().get(position).getTICKET_STATUS());
		TextView txt_description = (TextView) vi.findViewById(R.id.txt_description);
		txt_description.setText(data_list.getTicket_list().get(position).getALARM_DESCRIPTION());
		txt_description.setTypeface(Utils.typeFace(con));

		ticket_info.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(con,TicketDetailsTabs.class);
                mAppPreferences.SetBackModeNotifi123(2);
                i.putExtra("id",data_list.getTicket_list().get(position).getTICKET_ID());
				i.putExtra("sid",data_list.getTicket_list().get(position).getSITE_ID());
				i.putExtra("tktStatus",data_list.getTicket_list().get(position).getTICKET_STATUS());
               con.startActivity(i);
			}
		});



		btn_acknowledge.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressDialog.show();
				digitalDispatch(data_list.getTicket_list().get(position).getTICKET_ID(),"ack","",position,progressDialog);
			}
		});

		   btn_reject.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View view) {
				 showEditDialog(data_list.getTicket_list().get(position).getTICKET_ID(),position);
			   }
		   });



		return vi;
	}

	private void showEditDialog(String ticketid,int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setTitle("Remarks");
		final EditText input = new EditText(con);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		//input.setText("Remarks");
		builder.setView(input);

		builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String editedItem = input.getText().toString();
				if(editedItem.equalsIgnoreCase("")) {
					alertDialog.dismiss();
					Toast.makeText(con,"Please add remarks!!",Toast.LENGTH_SHORT).show();

				}
				else
				{
					alertDialog.dismiss();
					progressDialog.show();
					digitalDispatch(ticketid, "Reject", editedItem, position,progressDialog);
				}
				// Update your item in the list

			}
		});

		 alertDialog = builder.create();
		builder.show();
	}

	public void digitalDispatch(String ticketid,String action,String remarks,int position,ProgressDialog dialog)
	{

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("ticketId",ticketid);
		jsonObject.addProperty("action",action);
		jsonObject.addProperty("rejRemarks",remarks);
		jsonObject.addProperty("userId",mAppPreferences.getUserId());
		IApiRequest request = RetrofitApiClient.getRequest();
		Call<ResponseBody> call = request.TTAckRejDGAssign(jsonObject);
		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
				dialog.dismiss();
				try {
					if (response.isSuccessful()) {
						String response1 = response.body().string();
						JSONObject object = new JSONObject(response1);
						String flag = object.getString("flag");
						String message = object.getString("message");
						if(flag.equalsIgnoreCase("S"))
						{
							Toast.makeText(con,message,Toast.LENGTH_SHORT).show();
							data_list.getTicket_list().get(position).setTktaction(message);
							notifyDataSetChanged();
						}
						else if (flag.equalsIgnoreCase("F"))
						{
							Toast.makeText(con,message,Toast.LENGTH_SHORT).show();
							data_list.getTicket_list().remove(position);
							notifyDataSetChanged();
						}

					}
					else
					{
						Log.d("TAG","issue");
					}

				} catch (IOException e) {
					Log.d("TAG",e.getMessage().toString());
					e.printStackTrace();


				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				dialog.dismiss();
				Log.d("TAG",t.getMessage().toString());
			}
		});

	}


}
