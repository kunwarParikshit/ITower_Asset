package com.isl.itower;

import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;
import infozech.itower.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SessionExpired extends Activity {
	AppPreferences mAppPreferences;
	TextView ok;
	TextView pass_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session);
		ok = (TextView) findViewById(R.id.tvSubmit);
		ok.setTypeface(Utils.typeFace(SessionExpired.this));
		String msg = getIntent().getExtras().getString("msg");
		pass_msg = (TextView) findViewById(R.id.pass_msg);
		pass_msg.setTypeface(Utils.typeFace(SessionExpired.this));
		// pass_msg.setText(""+msg+"Your current session has expired.Please re-login");
		pass_msg.setText("" + msg);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//stopService(new Intent(SessionExpired.this,AppVersionService.class));
				Intent i = new Intent(SessionExpired.this, ValidateUDetails.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();
			}
		});
	}
}
