package com.isl.approval;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;

import infozech.itower.R;
/**
 * Created by dhakan on 6/7/2019.
 */

public class SparePartTabs  extends FragmentActivity {
    private FragmentTabHost mTabHost;
    TextView tv_brand_logo;
    String pendingTabRight,approvedTabRight,rejectTabRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab );
        tv_brand_logo = (TextView) findViewById( R.id.tv_brand_logo);
        Utils.msgText(SparePartTabs.this, "407", tv_brand_logo);
        Button bt_back = (Button)findViewById( R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(SparePartTabs.this, Approval.class);
                startActivity(i);
                finish();

            }
        });

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        getMenuRights();
        if(pendingTabRight.contains("V") || pendingTabRight.contains("A")) {
            mTabHost.addTab( mTabHost.newTabSpec( "Pending" )
                    .setIndicator( prepareTabView( "404" ) ), FragPendingSparePart.class, null );
        }
        if(approvedTabRight.contains("V")){
            mTabHost.addTab(mTabHost.newTabSpec("Approved")
                    .setIndicator(prepareTabView("405")),FragApprovedSparePart.class, null);
        }

        if(rejectTabRight.contains("V")) {
            mTabHost.addTab( mTabHost.newTabSpec( "Rejected" )
                    .setIndicator( prepareTabView( "406" ) ), FragRejectedSparePart.class, null );
        }

        /*if(rejectTabRight.contains("V")) {
            mTabHost.addTab( mTabHost.newTabSpec( "Rejected" )
                    .setIndicator( prepareTabView( "406" ) ), FragRejectedSparePart.class, null );
        }*/

    }

    private View prepareTabView(String id) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_header_custom, null);

        RelativeLayout relativelayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        if(getApplicationContext().getPackageName().equalsIgnoreCase("infozech.tawal")
                || getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa")){
            relativelayout.setBackgroundResource(R.drawable.tab_bg_selector_tawal);
        }else{
            relativelayout.setBackgroundResource(R.drawable.tab_bg_selector);
        }

        TextView tv = (TextView) view.findViewById(R.id.TabTextView);
        //tv.setText(text);
        Utils.msgText(SparePartTabs.this,id,tv);
        return view;
    }

    public void getMenuRights() {
        DataBaseHelper dbHelper = new DataBaseHelper(SparePartTabs.this);
        dbHelper.open();
        pendingTabRight = dbHelper.getSubMenuRight("SparePartPendingTab", "Approval");
        approvedTabRight = dbHelper.getSubMenuRight("SparePartApprovedTab", "Approval");
        rejectTabRight = dbHelper.getSubMenuRight("SparePartRejectedTab", "Approval");
        dbHelper.close();
     }
}
