package com.isl.itower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import infozech.itower.R;

/**
 * Created by dhakan on 7/31/2019.
 */

public class SplashScreen extends Activity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //ImageView img_splash = (ImageView) findViewById(R.id.img_splash);
        //img_splash.setBackgroundResource(R.drawable.splash);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this, ValidateUDetails.class);
                startActivity(intent);
                finish();
            }
        },7000);



    }
}