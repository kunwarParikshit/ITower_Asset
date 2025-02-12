package com.isl.approval;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isl.dao.cache.AppPreferences;
import com.isl.modal.DFRDetails;
import com.isl.util.Utils;

import infozech.itower.R;

/**
 * Created by dhakan on 6/12/2019.
 */

public class RecycleDFRDetails extends RecyclerView.Adapter<RecycleDFRDetails.MyViewHolder> {
    DFRDetails list;
    Context context;
    AppPreferences mAppPreferences;
    public RecycleDFRDetails(Context context, DFRDetails list) {
        this.list = list;
        this.context = context;
        mAppPreferences=new AppPreferences(context);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_site_id,tv_filler,tv_genset_type,tv_date,tv_vendor;
        public MyViewHolder(View view) {
            super(view);
            tv_site_id = (TextView) view.findViewById( R.id.tv_site_id);
            tv_site_id.setTypeface(Utils.typeFace(context));

            tv_filler = (TextView) view.findViewById(R.id.tv_filler);
            tv_filler.setTypeface(Utils.typeFace(context));

            tv_genset_type = (TextView) view.findViewById( R.id.tv_genset_type);
            tv_genset_type.setTypeface(Utils.typeFace(context));

            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_date.setTypeface(Utils.typeFace(context));

            tv_vendor = (TextView) view.findViewById(R.id.tv_vendor);
            tv_vendor.setTypeface(Utils.typeFace(context));
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_dfr_details, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String sName="";
        if(mAppPreferences.getSiteNameEnable()==1 && list.getGetDFRApproval().get(position).getSITE_NAME()!=null){
            sName="("+ list.getGetDFRApproval().get(position).getSITE_NAME()+")";
        }else{
            sName="";
        }

         if (list.getGetDFRApproval().get(position).getSITE_ID() != null) {
            holder.tv_site_id.setText(list.getGetDFRApproval().get(position).getSITE_ID()+sName);
        } else {
            holder.tv_site_id.setText("");
        }

        if (list.getGetDFRApproval().get(position).getFILLER_NAME() != null) {
            holder.tv_filler.setText(Utils.msg( context,"410")+list.getGetDFRApproval().get(position).getFILLER_NAME());
        } else {
            holder.tv_filler.setText(Utils.msg( context,"410"));
        }

        if (list.getGetDFRApproval().get(position).getDG_TYPE() != null) {
            holder.tv_genset_type.setText(Utils.msg( context,"411")+list.getGetDFRApproval().get(position).getDG_TYPE());
        } else {
            holder.tv_genset_type.setText(Utils.msg( context,"411"));
        }

        if (list.getGetDFRApproval().get(position).getTRAN_DATE() != null) {
            holder.tv_date.setText(list.getGetDFRApproval().get(position).getTRAN_DATE());
        } else {
            holder.tv_date.setText("");
        }

        if (list.getGetDFRApproval().get(position).getOME_NAME() != null) {
            holder.tv_vendor.setText(Utils.msg(context,"439" )+list.getGetDFRApproval().get(position).getOME_NAME());
        } else {
            holder.tv_vendor.setText(Utils.msg(context,"439" ));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SaveDFRDetails.class);
                i.putExtra( "tran_date",list.getGetDFRApproval().get(position).getTRAN_DATE());
                i.putExtra( "siteId",list.getGetDFRApproval().get(position).getSITE_ID());
                i.putExtra( "dgType",list.getGetDFRApproval().get(position).getDG_TYPE());
                i.putExtra( "dgType1",list.getGetDFRApproval().get(position).getDG_TYPE1());
                i.putExtra( "fwoid",list.getGetDFRApproval().get(position).getFWO_TRAN_ID());
                context.startActivity(i);
            }
        });

    }
    @Override
    public int getItemCount()
    {
        return list.getGetDFRApproval().size();
        //return 20;
    }
}
