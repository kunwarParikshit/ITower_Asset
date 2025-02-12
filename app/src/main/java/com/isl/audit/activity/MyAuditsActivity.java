package com.isl.audit.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.audit.adapter.AdapterAuditMenuItems;
import com.isl.audit.util.ItemClickListener;
import com.isl.constant.AppConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import infozech.itower.R;

/*This activity displays types of audits*/
public class MyAuditsActivity extends AppCompatActivity implements ItemClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    private final List<String> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_audits);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.button_back)
    void goToback() {
        finish();
    }

    private void init() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.header));
        }
        tvTitle.setTypeface(null, Typeface.BOLD);
        itemsList.add(getString(R.string.pending_audits));
        itemsList.add(getString(R.string.done_audits));
        itemsList.add(getString(R.string.upcoming_audits));
        itemsList.add(getString(R.string.today_audits));
        itemsList.add(getString(R.string.rescheduled_audits));
        setAdapter();

    }

    private void setAdapter() {
        AdapterAuditMenuItems adapterAuditMenuItems = new AdapterAuditMenuItems(itemsList, this, this);
        rvItems.setAdapter(adapterAuditMenuItems);
        rvItems.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public void onItemClickListener(View view, int position) {
        Intent intent=new Intent(MyAuditsActivity.this,AuditListActivity.class);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE,position);
        startActivity(intent);
    }
}
