package com.isl.approval;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.isl.modal.BeanSiteSpare;
import com.isl.util.Utils;

import infozech.itower.R;

/**
 * Created by vishal.singh on 6/11/2019.
 */

public class RecycleSparePendingAdapter extends RecyclerView.Adapter<RecycleSparePendingAdapter.MyViewHolder> {
    BeanSiteSpare data_list;
    Context context;
    String sName;
    public RecycleSparePendingAdapter(Context context, BeanSiteSpare data_list) {
        this.data_list = data_list;
        this.context = context;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView site_id,spare_name,qty,vendor,spare_data,type,ticket_id,status;
        public ImageView image;

        public MyViewHolder(View vi) {
            super(vi);

            site_id = (TextView) vi.findViewById(R.id.txt_site_id);
            site_id.setTypeface(Utils.typeFace(context));

            spare_name = (TextView) vi.findViewById(R.id.txt_spare_name);
            spare_name.setTypeface(Utils.typeFace(context));

            qty = (TextView) vi.findViewById(R.id.txt_spare_qty);
            qty.setTypeface(Utils.typeFace(context));

            vendor = (TextView) vi.findViewById(R.id.txt_spare_vendor);
            vendor.setTypeface(Utils.typeFace(context));

            spare_data = (TextView) vi.findViewById(R.id.txt_spare_date);
            spare_data.setTypeface(Utils.typeFace(context));

            type = (TextView) vi.findViewById(R.id.txt_checklist_type);
            type.setTypeface(Utils.typeFace(context));

            ticket_id = (TextView) vi.findViewById(R.id.txt_ticket_id);
            ticket_id.setTypeface(Utils.typeFace(context));

            status = (TextView) vi.findViewById(R.id.txt_status);
            status.setTypeface(Utils.typeFace(context));
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_spare, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if(data_list.getGetSparePart().get(position).getSname()!=null){
            sName="("+data_list.getGetSparePart().get(position).getSname()+")";
        }else{
            sName="";
        }

        if(data_list.getGetSparePart().get(position).getSid()!=null){
            holder.site_id.setText(data_list.getGetSparePart().get(position).getSid()+ sName);
        }else{
            holder.site_id.setText("");
        }

        if(data_list.getGetSparePart().get(position).getSpareName()!=null){
            holder.spare_name.setText( Utils.msg(context,"437" )+data_list.getGetSparePart().get(position).getSpareName());
        }else{
            holder.spare_name.setText(Utils.msg(context,"437" ));
        }
        if(data_list.getGetSparePart().get(position).getStatus().equalsIgnoreCase("2"))
        {
            if(data_list.getGetSparePart().get(position).getaQty()!=null){
                holder.qty.setText(Utils.msg(context,"438" )+data_list.getGetSparePart().get(position).getaQty());
            }else{
                holder.qty.setText(Utils.msg(context,"438" ));
            }
        }
        else
        {
            if(data_list.getGetSparePart().get(position).getQty()!=null){
                holder.qty.setText(Utils.msg(context,"438" )+data_list.getGetSparePart().get(position).getQty());
            }else{
                holder.qty.setText(Utils.msg(context,"438" ));
            }
        }


        if(data_list.getGetSparePart().get(position).getVendor()!=null){
            holder.vendor.setText(Utils.msg(context,"439" )+data_list.getGetSparePart().get(position).getVendor());
        }else{
            holder.vendor.setText(Utils.msg(context,"439" ));
        }

        if(data_list.getGetSparePart().get(position).getTransactionDate()!=null){
            holder.spare_data.setText(data_list.getGetSparePart().get(position).getTransactionDate());
        }else{
            holder.spare_data.setText("");
        }

        if(data_list.getGetSparePart().get(position).getTransactionType()!=null){
            holder.type.setText(data_list.getGetSparePart().get(position).getTransactionType());
        }else{
            holder.type.setText("");
        }

        if(data_list.getGetSparePart().get(position).getaType()!=null){
            holder.ticket_id.setText(data_list.getGetSparePart().get(position).getaType());
        }else{
            holder.ticket_id.setText("");
        }


        if(data_list.getGetSparePart().get(position).getStatus().equalsIgnoreCase("1")){
            holder.status.setText(Utils.msg(context,"440" ));
        }else if(data_list.getGetSparePart().get(position).getStatus().equalsIgnoreCase("2")){
            holder.status.setText(Utils.msg(context,"441" ));
        }else if(data_list.getGetSparePart().get(position).getStatus().equalsIgnoreCase("3")){
            holder.status.setText(Utils.msg(context,"442" ));
        }else{
            holder.status.setText("");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SaveSparePartApproval.class);
                i.putExtra( "id",data_list.getGetSparePart().get(position).getId());
                i.putExtra( "txnId",data_list.getGetSparePart().get(position).getTxnId());
                i.putExtra( "siteId",data_list.getGetSparePart().get(position).getSid());
                i.putExtra( "tran_date",data_list.getGetSparePart().get(position).getTransactionDate());
                i.putExtra( "status",data_list.getGetSparePart().get(position).getStatus());
                context.startActivity(i);
            }
        });

    }
    @Override
    public int getItemCount()
    {
        return data_list.getGetSparePart().size();
        //return 20;
    }
}
