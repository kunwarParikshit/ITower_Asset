package com.isl.incident;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.BeansTicketList;
import com.isl.modal.QRList;
import com.isl.util.Utils;
import infozech.itower.R;

public class AdapterAssets extends BaseAdapter {
    Context con;
    private LayoutInflater inflater = null;
    QRList data_list;
	public AdapterAssets(Context con, String data) {
        this.con = con;
        Gson g = new Gson();
        this.data_list = g.fromJson(data, QRList.class);
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data_list.getQRDetails().size();
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
            vi = inflater.inflate(R.layout.tt_asset_adapter, null);

        RelativeLayout ticket_info = (RelativeLayout)  vi.findViewById(R.id.ticket_info);
        ticket_info.setBackgroundColor( Color.parseColor( "#FFFFFF" ));

        TextView txt_qrcode = (TextView) vi.findViewById(R.id.txt_ticket_id);
        txt_qrcode.setText(Utils.msg(con,"880")+" : "
                +data_list.getQRDetails().get(position).getASSET_QRCODE());
        txt_qrcode.setTypeface(Utils.typeFace(con));

        TextView txt_action = (TextView) vi.findViewById(R.id.txt_action);
        txt_action.setText(Utils.msg(con,"911")+" : "
                +data_list.getQRDetails().get(position).getASSET_ACTION());
        txt_action.setTypeface(Utils.typeFace(con));


        TextView txt_asset_id = (TextView) vi.findViewById(R.id.txt_asset_id);
        txt_asset_id.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getASSET_ID()!=null
                && data_list.getQRDetails().get(position).getASSET_ID().length()>0){
            txt_asset_id.setText(Utils.msg(con,"885")+" : "
                    +data_list.getQRDetails().get(position).getASSET_ID());
        }else{
            txt_asset_id.setText(Utils.msg(con,"885")+" : ");
        }

        TextView txt_remarks = (TextView) vi.findViewById(R.id.txt_remarks);
        txt_remarks.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getREMARKS()!=null
                && data_list.getQRDetails().get(position).getREMARKS().length()>0){
            txt_remarks.setText(Utils.msg(con,"924")+" : "
                    +data_list.getQRDetails().get(position).getREMARKS());
        }else{
            txt_remarks.setText(Utils.msg(con,"924")+" : ");
        }


        //txt_asset_id.setVisibility(View.GONE);

        TextView txt_asset_type = (TextView) vi.findViewById(R.id.txt_asset_type);
        txt_asset_type.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getASSET_TYPE()!=null
                && data_list.getQRDetails().get(position).getASSET_TYPE().length()>0){
            txt_asset_type.setText(Utils.msg(con,"891")+" : "
                    +data_list.getQRDetails().get(position).getASSET_TYPE());
        }else{
            txt_asset_type.setText(Utils.msg(con,"891")+" : ");
        }



        TextView txt_manufacturer = (TextView) vi.findViewById(R.id.txt_manufacturer);
        txt_manufacturer.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getMANUFATURER()!=null
                && data_list.getQRDetails().get(position).getMANUFATURER().length()>0){
            txt_manufacturer.setText(Utils.msg(con,"913")+" : "
                    +data_list.getQRDetails().get(position).getMANUFATURER());
        }else{
            txt_manufacturer.setText(Utils.msg(con,"913")+" : ");
        }


        TextView txt_model = (TextView) vi.findViewById(R.id.txt_model);
        txt_model.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getMODEL()!=null
                && data_list.getQRDetails().get(position).getMODEL().length()>0){
            txt_model.setText(Utils.msg(con,"878")+" : "
                    +data_list.getQRDetails().get(position).getMODEL());
        }else{
            txt_model.setText(Utils.msg(con,"878")+" : ");
        }



        TextView txt_serial_no = (TextView) vi.findViewById(R.id.txt_serial_no);
        txt_serial_no.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getSERIALNUMBER()!=null
                && data_list.getQRDetails().get(position).getSERIALNUMBER().length()>0){
            txt_serial_no.setText(Utils.msg(con,"879")+" : "
                    +data_list.getQRDetails().get(position).getSERIALNUMBER());
        }else{
            txt_serial_no.setText(Utils.msg(con,"879")+" : ");
        }

        TextView txt_capacity = (TextView) vi.findViewById(R.id.txt_capacity);
        txt_capacity.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getCAPACITY()!=null
                && data_list.getQRDetails().get(position).getCAPACITY().length()>0){
            txt_capacity.setText(Utils.msg(con,"881")+" : "
                    +data_list.getQRDetails().get(position).getCAPACITY());
        }else{
            txt_capacity.setText(Utils.msg(con,"881")+" : ");
        }

        TextView txt_warenty_date = (TextView) vi.findViewById(R.id.txt_warenty_date);
        txt_warenty_date.setTypeface(Utils.typeFace(con));
        if(data_list.getQRDetails().get(position).getWARRANTY_DATE()!=null
                && data_list.getQRDetails().get(position).getWARRANTY_DATE().length()>0){
            txt_warenty_date.setText(Utils.msg(con,"925")+" : "
                    +data_list.getQRDetails().get(position).getWARRANTY_DATE());
        }else{
            txt_warenty_date.setText(Utils.msg(con,"925")+" : ");
        }
        return vi;
    }
}
