package com.isl.itower;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import infozech.itower.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Version extends Activity {
	AppPreferences mAppPreferences;
	Button cancel, download;
	TextView version_msg;
    ImageView iv_logo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.version);
		mAppPreferences = new AppPreferences(Version.this);
		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		cancel = (Button) findViewById(R.id.btn_cancel);
		cancel.setTypeface(Utils.typeFace(Version.this));
		download = (Button) findViewById(R.id.btn_download);
		download.setTypeface(Utils.typeFace(Version.this));
		version_msg = (TextView) findViewById(R.id.version_msg);
		Utils.msgText(Version.this, "5080", version_msg);
		clientIcon(getApplicationContext().getPackageName());
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.setLoginState(1);
				Intent i = new Intent(Version.this, HomeActivity.class);
				startActivity(i);
				finish();
			}
		});

		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loginState();
				final String appPackageName = getPackageName();
				/*startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://play.google.com/store/apps/details?id="
								+ appPackageName)));
				finish();*/
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + appPackageName)));
					finish();
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("https://play.google.com/store/apps/details?id="
									+ appPackageName)));
					finish();
				}
			}
		});
	}

	public void loginState() {
		mAppPreferences.setTTAssignRb("off");
		mAppPreferences.setTTUpdateRb("off");
		mAppPreferences.setTTEscalateRb("off");
		mAppPreferences.setPMScheduleRb("off");
		mAppPreferences.setPMEscalateRb("off");
		DataBaseHelper dbHelper = new DataBaseHelper(Version.this);
		dbHelper.open();
		dbHelper.clearFormRights();
		dbHelper.close();
		mAppPreferences.setLoginState(0);
		mAppPreferences.saveSyncState(0);
		mAppPreferences.setGCMRegistationId("");
	}

	public void clientIcon(String appId){
		switch (appId) {
			case "tawal.com.sa" :
				iv_logo.setBackgroundResource(R.drawable.tawal_icon);
				break;
			case "infozech.tawal" :
				iv_logo.setBackgroundResource(R.drawable.midc_logo);
				break;
			case "infozech.safari" :
				iv_logo.setBackgroundResource(R.drawable.infozech_logo);
				break;
			case "apollo.com.sa" :
				iv_logo.setBackgroundResource(R.drawable.appollo_logo);
				break;
			case "voltalia.com.sa" :
				iv_logo.setBackgroundResource(R.drawable.voltalia_logo);
				break;
			case "ock.com.sa" :
				iv_logo.setBackgroundResource(R.drawable.ock_logo);
				break;
			case "eft.com.sa" :
				iv_logo.setBackgroundResource(R.drawable.eft_logo);
				break;
		}
	}
}
