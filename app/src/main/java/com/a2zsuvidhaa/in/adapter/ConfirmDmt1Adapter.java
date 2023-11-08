package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2zsuvidhaa.in.model.ConfirmDm1Transfer;
import com.a2zsuvidhaa.in.R;

import java.util.ArrayList;

public class ConfirmDmt1Adapter extends RecyclerView.Adapter<ConfirmDmt1Adapter.ViewHolder> {

    private ArrayList<ConfirmDm1Transfer> list;
    private Context context;

    public ConfirmDmt1Adapter(ArrayList<ConfirmDm1Transfer> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_confirm_dmt1_transfer,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        ConfirmDm1Transfer confirmDm1Transfer = list.get(i);
        viewHolder.tv_charge.setText(confirmDm1Transfer.getCharge());
        viewHolder.tv_txnAmount.setText(confirmDm1Transfer.getTxnAmount());
        viewHolder.tv_total.setText(confirmDm1Transfer.getTotal());
        viewHolder.tv_itemCount.setText(String.valueOf((i+1)));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_charge;
        TextView tv_txnAmount;
        TextView tv_total;
        TextView tv_itemCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_charge = itemView.findViewById(R.id.tv_charge);
            tv_txnAmount = itemView.findViewById(R.id.tv_txnAmount);
            tv_total = itemView.findViewById(R.id.tv_total);
            tv_itemCount = itemView.findViewById(R.id.tv_itemCount);


        }
    }
}
