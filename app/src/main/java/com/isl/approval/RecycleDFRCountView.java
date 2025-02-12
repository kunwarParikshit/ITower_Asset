package com.isl.approval;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.isl.modal.DfrCountLists;
import com.isl.util.Utils;

import infozech.itower.R;
/**
 * Created by dhakan on 6/6/2019.
 */

public class RecycleDFRCountView extends RecyclerView.Adapter<RecycleDFRCountView.MyViewHolder> {
    DfrCountLists list;
    Context context;
    public RecycleDFRCountView(Context context, DfrCountLists list) {
        this.list = list;
        this.context = context;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_count,tv_date;
        public MyViewHolder(View view) {
            super(view);
            tv_count = (TextView) view.findViewById( R.id.tv_count);
            tv_count.setTypeface( Utils.typeFace(context));
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_date.setTypeface(Utils.typeFace(context));
            }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_dfr_count, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (list.getGetDFRCount().get(position).getPENDING_SITES() != null) {
                holder.tv_count.setText(list.getGetDFRCount().get(position).getPENDING_SITES());
            } else {
                holder.tv_count.setText("");
            }

            if (list.getGetDFRCount().get(position).getTRANSACTION_DATE() != null) {
               holder.tv_date.setText(list.getGetDFRCount().get(position).getTRANSACTION_DATE());
            } else {
                holder.tv_date.setText("");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DFRApprovalDetails.class);
                i.putExtra( "tran_date",list.getGetDFRCount().get(position).getTRANSACTION_DATE());
                context.startActivity(i);


            }
        });

    }
    @Override
    public int getItemCount()
    {
        return list.getGetDFRCount().size();
        //return 20;
    }
}
