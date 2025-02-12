package com.isl.audit.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import infozech.itower.R;

/* This activity is for showing Alert to user whenever internet
becomes available and user have unsynced auits. */
public class AlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        displayAlert();
    }

    private void displayAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.sync_now_alert)).setCancelable(
                false).setPositiveButton(getString(R.string.sync_now),
                (dialog, id) -> {
                    dialog.cancel();
                    finish();
                    startActivity(new Intent(AlertActivity.this,MyAuditsActivity.class));
                }).setNegativeButton(getString(R.string.cancel),
                (dialog, id) -> {
                    dialog.cancel();
                    finish();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}