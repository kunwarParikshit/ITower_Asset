package com.isl.energy.withdrawal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.FuelWithdrawalDataList;

import infozech.itower.R;

/**
 * Created by dhakan on 11/1/2018.
 */

public class AdapterGrid extends BaseAdapter {
    Context con;
    private LayoutInflater inflater = null;
    FuelWithdrawalDataList rptData;
    AppPreferences mAppPreferences;
    String SiteID;
    public AdapterGrid(Context con, String data) {
        this.con = con;
        Gson g = new Gson();
        this.rptData = g.fromJson(data, FuelWithdrawalDataList.class);
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAppPreferences=new AppPreferences(con);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return rptData.getDieselWithdrawl().size();
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
            vi = inflater.inflate( R.layout.adapter_grid_purchase, null);

       /* if(mAppPreferences.getSiteNameEnable()==1 && data_list.getTicket_list().get(position).getsName()!=null){
            sName="("+data_list.getTicket_list().get(position).getsName()+")";
        }else{
            sName="";
        }*/

        TextView txt_request_date = (TextView) vi.findViewById(R.id.txt_request_date);
        if(rptData.getDieselWithdrawl().get( position ).getWrdt()!=null){
            txt_request_date.setText("Request Date-"+rptData.getDieselWithdrawl().get( position ).getWrdt());
        }else{
            txt_request_date.setText("Request Date-");
        }


        TextView txt_app_date = (TextView) vi.findViewById(R.id.txt_app_date);
        if(rptData.getDieselWithdrawl().get(position).getWradt()!=null){
            txt_app_date.setText("Approval Date-"+rptData.getDieselWithdrawl().get(position).getWradt());
        }else{
            txt_app_date.setText("Approval Date-");
        }


        /*TextView txt_site_id = (TextView) vi.findViewById(R.id.txt_site_id);
        if(data_list.getDieselWithdrawl().get(position).getSid()!=null){
            txt_site_id.setText("Site Id-"+data_list.getDieselWithdrawl().get(position).getSid());
        }else{
            txt_site_id.setText("Site Id-");
        }
        txt_site_id.setPaintFlags(txt_site_id.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/

        if(rptData.getDieselWithdrawl().get(position).getSid()!=null){
            SiteID="("+rptData.getDieselWithdrawl().get(position).getSid()+")";
        }else{
            SiteID="";
        }

        TextView txt_supplier = (TextView) vi.findViewById(R.id.txt_supplier);
        if(rptData.getDieselWithdrawl().get(position).getFsname()!=null){
            txt_supplier.setText("Fuel Supplier-"+rptData.getDieselWithdrawl().get(position).getFsname()+SiteID);
        }else{
            txt_supplier.setText("Fuel Supplier-");
        }

        TextView txt_txn_status = (TextView) vi.findViewById(R.id.txt_txn_status);
        if(rptData.getDieselWithdrawl().get(position).getTxnStatus()!=null){
            txt_txn_status.setText("Status-"+rptData.getDieselWithdrawl().get(position).getTxnStatus());
        }else{
            txt_txn_status.setText("Status-");
        }

        return vi;
    }
}
