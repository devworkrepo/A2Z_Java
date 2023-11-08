package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.model.BankDetail;

import java.util.ArrayList;

public class BankDetailAdapter extends RecyclerView.Adapter<BankDetailAdapter.ViewHolder> {

    private ArrayList<BankDetail> list;
    private Context context;

    public BankDetailAdapter(ArrayList<BankDetail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_bank_detail,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        BankDetail bankDetail = list.get(i);
        viewHolder.tv_id.setText(bankDetail.getId());
        viewHolder.tv_bank_name.setText(bankDetail.getBankName());
        viewHolder.tv_branch.setText(bankDetail.getBranchName());
       /* if(MainActivity.spos==1 && bankDetail.getId().equalsIgnoreCase("2")) {
            viewHolder.tv_accountNo.setText("EXCE" + SharedPref.getInstance(context).getMobile());
            viewHolder.tv_ifsc.setText("ICIC0000104");
        }
            else {*/
            viewHolder.tv_accountNo.setText("Account No. : "+bankDetail.getAccount_number());
            viewHolder.tv_ifsc.setText("IFSC Code: "+bankDetail.getIfsc());
        //}
        if (bankDetail.getCharges().toLowerCase().contains("charge")){
            viewHolder.tv_charges.setTextColor(context.getResources().getColor(R.color.red));
        }else if (bankDetail.getCharges().toLowerCase().contains("commission")){
            viewHolder.tv_charges.setTextColor(context.getResources().getColor(R.color.success));
        }else {
            viewHolder.tv_charges.setTextColor(context.getResources().getColor(R.color.black));
        }

        viewHolder.tv_charges.setText(bankDetail.getCharges());
        viewHolder.tv_messageOne.setText(bankDetail.getMessageOne());
        viewHolder.tv_messageTwo.setText(bankDetail.getMessageTwo());



        viewHolder.tv_itemCount1.setText((i + 1) + "");
        viewHolder.tv_itemCount2.setText((i + 1) + "");

        viewHolder.ll_main_layout.setOnClickListener(view -> {
            viewHideShow(viewHolder, bankDetail);
        });
        if (list.get(viewHolder.getAdapterPosition()).isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
        }

        viewHolder.update.setOnClickListener(view -> {
          /*  Intent intent=new Intent(context, FundRequestActivity.class);
            intent.putExtra("type","2");
            intent.putExtra("bank",bankDetail);
            intent.putExtra("mode", BankDetailFragment.type);
            context.startActivity(intent);*/
        });

    }


    private void viewHideShow(ViewHolder viewHolder, BankDetail bankDetail) {
        if (bankDetail.isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            list.get(viewHolder.getAdapterPosition()).setHide(false);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
            list.get(viewHolder.getAdapterPosition()).setHide(true);
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_id;
        TextView tv_bank_name;
        TextView tv_ifsc;
        TextView tv_accountNo;
        TextView tv_charges;
        TextView tv_branch;
        TextView tv_messageOne;
        TextView tv_messageTwo;

        private TextView tv_itemCount1;
        private TextView tv_itemCount2;

        private LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        private LinearLayout ll_arrow_hideable;

        Button update;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            update=itemView.findViewById(R.id.btn_update);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_ifsc = itemView.findViewById(R.id.tv_ifsc);
            tv_accountNo = itemView.findViewById(R.id.tv_accountNo);
            tv_charges= itemView.findViewById(R.id.tv_charges);
            tv_branch = itemView.findViewById(R.id.tv_branch);
            tv_messageOne = itemView.findViewById(R.id.tv_messageOne);
            tv_messageTwo = itemView.findViewById(R.id.tv_messageTwo);

            tv_itemCount1 = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);

            ll_hideable = itemView.findViewById(R.id.ll_hideable);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            ll_arrow_hideable = itemView.findViewById(R.id.ll_arrow_hideable);

        }
    }

}
