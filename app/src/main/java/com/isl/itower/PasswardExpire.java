package com.isl.itower;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.modal.Response;
import infozech.itower.R;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

public class PasswardExpire extends Activity {
	Dialog changePassPop;
	AppPreferences mAppPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.passward_expire);
		String msg = getIntent().getExtras().getString("msg");
		mAppPreferences=new AppPreferences(PasswardExpire.this);
		pwdExpire(msg);
		
	}
	
	 public void pwdExpire(String msg){ //open pop up for passward expire next day
		  AlertDialog.Builder alert = new AlertDialog.Builder(PasswardExpire.this);
		  LayoutInflater inflater = getLayoutInflater();
	   	  View view=inflater.inflate(R.layout.custom_alert, null);
	   	  Button positive = (Button)view.findViewById(R.id.bt_ok);
	   	  Button negative = (Button)view.findViewById(R.id.bt_cancel);
	   	  TextView title = (TextView)view.findViewById(R.id.tv_title);
	   	  EditText et= (EditText)view.findViewById(R.id.et_ip);
	   	  et.setVisibility(View.GONE);
	   	  positive.setTypeface(Utils.typeFace(PasswardExpire.this));
	   	  negative.setTypeface(Utils.typeFace(PasswardExpire.this));
	   	  title.setTypeface(Utils.typeFace(PasswardExpire.this));
	   	  title.setText(msg);
	   	  positive.setText(Utils.msg(PasswardExpire.this, "7"));
	   	  negative.setText(Utils.msg(PasswardExpire.this, "8"));
	   	  alert.setView(view);
	   	  final AlertDialog alertDialog = alert.create();
		  alertDialog.show();
		  
		  positive.setOnClickListener(new OnClickListener() {
		  @Override
			public void onClick(View arg0) {
			  alertDialog.cancel();
			  changePassPopup();
			 }
		   });
		  
		  negative.setOnClickListener(new OnClickListener() {
			 @Override
			  public void onClick(View arg0) {
				 alertDialog.cancel();
				 finish();
			 }
		  });
		  }
	 
	 public void changePassPopup(){
        changePassPop = new Dialog(PasswardExpire.this, R.style.FullHeightDialog);
	    changePassPop.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    changePassPop.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
	    changePassPop.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    changePassPop.setContentView(R.layout.change_passward);
 		final Window window_SignIn = changePassPop.getWindow();
 		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
 		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
 		changePassPop.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION);
 		changePassPop.show();
 		final TextInputEditText et_old_pwd = (TextInputEditText) changePassPop.findViewById(R.id.et_old_pwd);
  		et_old_pwd.setFocusableInTouchMode(true);
  		final TextInputEditText et_new_pwd = (TextInputEditText) changePassPop.findViewById(R.id.et_new_pwd);
  		final TextInputEditText et_retry_pwd = (TextInputEditText) changePassPop.findViewById(R.id.et_retry_pwd);
  		Button bt_save = (Button) changePassPop.findViewById(R.id.bt_save);
  		Button bt_cancel = (Button) changePassPop.findViewById(R.id.bt_cancel);
  		TextView tv_brand_logo=(TextView)changePassPop.findViewById(R.id.tv_brand_logo);
 		//TextView tv_old_pwd =(TextView)changePassPop.findViewById(R.id.tv_old_pwd);
 		//TextView tv_new_pwd =(TextView)changePassPop.findViewById(R.id.tv_new_pwd);
 		//TextView tv_retry_pwd =(TextView)changePassPop.findViewById(R.id.tv_retry_pwd);
  		
  		Utils.msgText(PasswardExpire.this,"22",tv_brand_logo);    
 		//WorkFlowUtils.msgText(PasswardExpire.this,"23",tv_old_pwd);
 		//WorkFlowUtils.msgText(PasswardExpire.this,"24",tv_new_pwd);
 		//WorkFlowUtils.msgText(PasswardExpire.this,"25",tv_retry_pwd);
 		Utils.msgButton(PasswardExpire.this,"26",bt_cancel); 
 		Utils.msgButton(PasswardExpire.this,"27",bt_save); 
  		
  		
  		 		
 		bt_cancel.setOnClickListener(new OnClickListener() {
 		@Override
 		public void onClick(View arg0) {
 		changePassPop.dismiss(); 
 		finish();
 		}
 		});
 		bt_save.setOnClickListener(new OnClickListener() {
 		@Override
 		public void onClick(View arg0) {
 		if (et_old_pwd.getText().toString().trim().length() == 0) {
 		    //Toast.makeText(PasswardExpire.this, "Enter Old Password.",Toast.LENGTH_LONG).show();
 			Utils.toast(PasswardExpire.this, "28");
 		} else if (et_new_pwd.getText().toString().trim().length() == 0){
 			//Toast.makeText(PasswardExpire.this, "Enter New Password.",Toast.LENGTH_LONG).show();
 			Utils.toast(PasswardExpire.this, "29");
 		}else if (et_retry_pwd.getText().toString().trim().length() == 0) {
 			//Toast.makeText(PasswardExpire.this,"Enter Retype Password.",Toast.LENGTH_LONG).show();
 			Utils.toast(PasswardExpire.this, "30");
 		}else if (!(et_new_pwd.getText().toString().trim().equals(et_retry_pwd.getText().toString().trim()))) {
    		//Toast.makeText(PasswardExpire.this,"Password does not match with Retype Password.",Toast.LENGTH_LONG).show();
 			Utils.toast(PasswardExpire.this, "31");
 		}else if (et_old_pwd.getText().toString().trim().equals(et_new_pwd.getText().toString().trim())) {
   			//Toast.makeText(PasswardExpire.this,"Enter New Password different from Old Password.",Toast.LENGTH_LONG).show();
 			  Utils.toast(PasswardExpire.this, "32");
 		}else {
 		if (Utils.isNetworkAvailable(PasswardExpire.this)) {
 		new ChangePassward(PasswardExpire.this,mAppPreferences.getUserId(),
 				   et_old_pwd.getText().toString(),
 				   et_new_pwd.getText().toString()).execute();
 		} else {
 		//WorkFlowUtils.ToastMessage(PasswardExpire.this,Constants.netConnection);
 			Utils.toast(PasswardExpire.this, "17");	
 		}}
 		}});
      }
		  
		  //call webservice for change passward
		  public class ChangePassward extends AsyncTask<Void, Void, Void> {
				ProgressDialog pd;
				Context con;
				String user_id;
				String pwd;
				String newpwd;
				Response response;
				public ChangePassward(Context con, String user_id, String pwd,String newpwd) {
				this.con = con;
				this.user_id = user_id;
				this.pwd = pwd;
				this.newpwd = newpwd;
				}
				@Override
				protected void onPreExecute() {
				pd = ProgressDialog.show(con, null, "Loading...");
				super.onPreExecute();
				}
				@Override
				protected Void doInBackground(Void... params) {
				try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("userID", user_id));
				nameValuePairs.add(new BasicNameValuePair("pwd", pwd));
				nameValuePairs.add(new BasicNameValuePair("newPwd", newpwd));
				nameValuePairs.add(new BasicNameValuePair("languageCode",""+mAppPreferences.getLanCode()));
				String res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+"/Service.asmx/ChangPwd", nameValuePairs);
				String new_res = res.replace("[", "").replace("]", "");
				response = new Gson().fromJson(new_res,Response.class);
			    } catch (Exception e) {
				e.printStackTrace();
				response = null;
			    }
			    return null;
		        }
				@Override
				protected void onPostExecute(Void result) {
				if (pd !=null && pd.isShowing()) {
				pd.dismiss();
				}
				if (response != null) {
				   Utils.toastMsg(PasswardExpire.this, response.getMessage());
				   if (response.getSuccess().equals("true")) {
				   changePassPop.dismiss();
				   loginState();
				   }
				}else{
				  Utils.toast(PasswardExpire.this,"13"); //Server Not Available
				}
				super.onPostExecute(result);
				}
			    };
			    public void loginState(){
			    	//stopService(new Intent(PasswardExpire.this, AppVersionService.class));
			    	DataBaseHelper dbHelper = new DataBaseHelper(PasswardExpire.this);
			     	dbHelper.open();
					dbHelper.clearFormRights();
					dbHelper.close();
					mAppPreferences.setLoginState(0);
					mAppPreferences.saveSyncState(0);
					mAppPreferences.setGCMRegistationId("");
			    	Intent i = new Intent(PasswardExpire.this, ValidateUDetails.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
					finish();
			    }
}
