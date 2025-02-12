package com.isl.photo.camera;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import infozech.itower.R;

public class ViewVideoVideoView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        Button btnClose = (Button) findViewById(R.id.btnIvClose);
        final VideoView video_view = (VideoView) findViewById(R.id.video_view);
        String path = getIntent().getExtras().getString("path");

        video_view.setMediaController(new MediaController(ViewVideoVideoView.this));
        video_view.setVideoPath(path);
        video_view.requestFocus();
        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                video_view.start();
            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               finish();
            }
        });



    }

}


