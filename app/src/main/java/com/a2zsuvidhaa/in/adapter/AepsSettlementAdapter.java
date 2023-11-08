package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.model.BankDetail;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AepsSettlementAdapter extends RecyclerView.Adapter<AepsSettlementAdapter.ViewHolder> {

    private ArrayList<BankDetail> list;
    private Context context;

    public AepsSettlementAdapter(ArrayList<BankDetail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_aeps_sett,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        BankDetail bankDetail = list.get(i);
        viewHolder.tv_name.setText(bankDetail.getBeneName());
        viewHolder.tv_bank_name.setText(bankDetail.getBankName());
        viewHolder.tv_branch.setText(bankDetail.getBranchName());
        viewHolder.tv_accountNo.setText(bankDetail.getAccount_number());
        viewHolder.tv_ifsc.setText(bankDetail.getIfsc());
        viewHolder.tv_balnce.setText(bankDetail.getBalance());
        viewHolder.tv_minimum_bal.setText(bankDetail.getAeps_bloack_amount());
        viewHolder.tv_charge.setText(bankDetail.getAeps_charge());

//        viewHolder.status.setText(bankDetail.getStatus());

        viewHolder.btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.transfer(i,"");
            }
        });


    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_balnce;
        TextView tv_name;
        TextView tv_minimum_bal;
        TextView tv_charge;
        TextView tv_bank_name;
        TextView tv_ifsc;
        TextView tv_accountNo;
        TextView tv_branch;
        Button btn_send;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_name=itemView.findViewById(R.id.tv_beneName);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_ifsc = itemView.findViewById(R.id.tv_ifsc);
            tv_accountNo = itemView.findViewById(R.id.tv_accountNo);
            tv_branch = itemView.findViewById(R.id.tv_branch_code);
            tv_balnce=itemView.findViewById(R.id.tv_balance);
            tv_minimum_bal=itemView.findViewById(R.id.tv_minimum_bal);
            tv_charge=itemView.findViewById(R.id.tv_charge);

            btn_send=itemView.findViewById(R.id.btn_send);



        }
    }

    private OnAepsAdapterClickListener listener;

    public interface OnAepsAdapterClickListener{
         void transfer(int pos,String amount);
    }
    public void setupListener(OnAepsAdapterClickListener listener){
        this.listener = listener;
    }
}
