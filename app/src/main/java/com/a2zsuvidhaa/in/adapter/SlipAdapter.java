package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2zsuvidhaa.in.model.PaymentSlipDMT1;
import com.a2zsuvidhaa.in.R;

import java.util.ArrayList;

public class SlipAdapter extends RecyclerView.Adapter<SlipAdapter.ViewHolder> {

    private ArrayList<PaymentSlipDMT1> list;
    private Context context;

    public SlipAdapter(ArrayList<PaymentSlipDMT1> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_payment_slip,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        PaymentSlipDMT1 slipDMT1 = list.get(i);
        viewHolder.tv_amount.setText(slipDMT1.getAmount());
        viewHolder.tv_txnId.setText(slipDMT1.getTxnId());
        viewHolder.tv_refNo.setText(slipDMT1.getRefNo());
        viewHolder.tv_status.setText(slipDMT1.getStatus());
        viewHolder.tv_itemCount.setText(String.valueOf((i+1)));

        if(slipDMT1.getStatus().equalsIgnoreCase("FAILED")){
            viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
        }
        else if(slipDMT1.getStatus().equalsIgnoreCase("PENDING")){
            viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.yellow_dark));
        }
        else if(slipDMT1.getStatus().equalsIgnoreCase("SUCCESS")){
            viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_amount;
        TextView tv_txnId;
        TextView tv_refNo;
        TextView tv_status;
        TextView tv_itemCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_txnId = itemView.findViewById(R.id.tv_txnId);
            tv_refNo = itemView.findViewById(R.id.tv_refNo);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_itemCount = itemView.findViewById(R.id.tv_itemCount);


        }
    }
}
