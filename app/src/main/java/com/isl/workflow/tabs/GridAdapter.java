package com.isl.workflow.tabs;
import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isl.constant.AppConstants;
import com.isl.util.Utils;
import com.isl.workflow.FormActivity;
import com.isl.workflow.constant.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infozech.itower.R;

/**
 * Created by dhakan on 6/24/2020.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    List<HashMap<String, Object>> requestList;
    Context context;
    String sName;
    String source;

    public GridAdapter(Context context, List<HashMap<String, Object>> requestList, String source) {
        this.requestList = requestList;
        this.context = context;
        this.source = source;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvField1,tvField2,tvField3,tvField4,tvField5;
        public RelativeLayout mainView;
        public MyViewHolder(View vi) {
            super(vi);

            mainView = (RelativeLayout) vi.findViewById( R.id.main_view);

            tvField1 = (TextView) vi.findViewById( R.id.txt_site_id);
            tvField1.setTypeface( Utils.typeFace(context));

            tvField2 = (TextView) vi.findViewById(R.id.txt_requester_date);
            tvField2.setTypeface(Utils.typeFace(context));

            tvField3 = (TextView) vi.findViewById(R.id.txt_change_Id);
            tvField3.setTypeface(Utils.typeFace(context));

            tvField4 = (TextView) vi.findViewById(R.id.txt_product_type);
            tvField4.setTypeface(Utils.typeFace(context));

            tvField5 = (TextView) vi.findViewById(R.id.txt_status);
            tvField5.setTypeface(Utils.typeFace(context));
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.access_mnt_tt_grid, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        sName="";
        Map<String,Object> txnData = requestList.get(position);

        for(Map.Entry<String, Object> entry : txnData.entrySet()){

            switch(entry.getKey()){
                case AppConstants.SITE_ID_ALIAS:

                    if(entry.getValue() != null){
                        holder.tvField1.setText(entry.getValue()+ sName);
                    }else{
                        holder.tvField1.setText("");
                    }
                    break;
                case Constants.TXN_ID:
                    if(entry.getValue() != null){
                        holder.tvField3.setText((String)entry.getValue());
                    }else{
                        holder.tvField3.setText("");
                    }
                    break;
                case Constants.TXT_PRODUCTTYPE_ALIAS:
                    if(entry.getValue() != null){
                        holder.tvField4.setText("Product Type : "+entry.getValue());
                    }else{
                        holder.tvField4.setText("Product Type : ");
                    }
                    break;
                case Constants.TXT_DISPLAY_STATUS:
                    if(entry.getValue() != null){
                        holder.tvField5.setText("Request Status : "+entry.getValue());
                    }else{
                        holder.tvField5.setText("Request Status : ");
                    }
                    break;
                case Constants.REQUESTER_DATE:
                    if(entry.getValue() != null){
                        holder.tvField2.setText((String)entry.getValue());
                    }else{
                        holder.tvField2.setText("");
                    }
                    break;
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity(requestList.get(position));
            }
        });

    }
    @Override
    public int getItemCount()
    {
        return requestList.size();
    }

    private void nextActivity(HashMap<String,Object> selected) {

        /*HashMap<String,String> tranData = new HashMap<String,String>();
        tranData.put(AppConstants.TRAN_ID_ALIAS,(String)selected.get(AppConstants.TRAN_ID_ALIAS));
        tranData.put(Constants.FORM_NAME,(String)selected.get(Constants.FORM_NAME));
        */
        selected.put(Constants.OPERATION,"E");
        selected.put(Constants.TXN_SOURCE,source);

        Intent i = new Intent( context, FormActivity.class );
        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, selected);
        context.startActivity(i);
    }
}