package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.model.PaymentReport;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.util.enums.PaymentReportType;

import java.util.ArrayList;
import java.util.List;


public class PaymentFundReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<PaymentReport> reportList;
    private Context context;
    private PaymentReportType paymentReportType;

    private boolean isLoadingAdded = false;

    public PaymentFundReportAdapter(Context context, PaymentReportType paymentReportType) {
        this.context = context;
        reportList = new ArrayList<>();
        this.paymentReportType = paymentReportType;
    }

    public List<PaymentReport> getReportList() {
        return reportList;
    }

    public void setReportList(List<PaymentReport> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_payment_report, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        PaymentReport report = reportList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.tv_mode_closingBal.setText(report.getMode());
                viewHolder.tv_id.setText(report.getId());
                viewHolder.tv_amount.setText("Rs. "+report.getAmount());
                viewHolder.tv_shopName_requestTo.setText(report.getFirm_name());

                if(paymentReportType==PaymentReportType.PAYMENT_REPORT){
                    viewHolder.ll_fund_transfer.setVisibility(View.GONE);
                    viewHolder.tv_shopName_requestTo.setText(report.getRequest_to());
                    viewHolder.ll_payment_report.setVisibility(View.VISIBLE);
                }else if(paymentReportType == PaymentReportType.FUND_TRANSFER_REPORT){
                    viewHolder.ll_fund_transfer.setVisibility(View.VISIBLE);
                    viewHolder.ll_payment_report.setVisibility(View.GONE);
                    viewHolder.tv_amount.setText("Rs. "+report.getCredit_amount());
                    viewHolder.tv_mode_closingBal.setText("Rs. "+report.getClosing_bal());

                }



                //view for payment report
                viewHolder.tv_request_to.setText(report.getRequest_to());
                viewHolder.tv_bank_name.setText(report.getBank_name());
                viewHolder.tv_branch_code.setText(report.getBranch_code());
                viewHolder.tv_deposit_date.setText(report.getDeposit_date());
                viewHolder.tv_deposit_slip.setText(report.getDeposit_slip());
                viewHolder.tv_customer_remark.setText(report.getCustomer_remark());
                viewHolder.tv_updated_remark.setText(report.getUpdated_remark());

                //for both
                viewHolder.tv_ref_id.setText(report.getRef_id());
                viewHolder.tv_remark.setText(report.getRemark());

                //view for fund transfer report
                viewHolder.tv_bankCharge.setText(report.getBank_charge());
                viewHolder.tv_closingBalance.setText(report.getClosing_bal());
                viewHolder.tv_openingBalance.setText(report.getOpening_bal());
                viewHolder.tv_description.setText(report.getDescription());
                viewHolder.tv_transferFromTo.setText(report.getTransfer_to_from());
                viewHolder.tv_user.setText(report.getUser());
                viewHolder.tv_updateDate.setText(report.getUpdate_date());
                viewHolder.tv_requestDate.setText(report.getRequest_date());



                viewHolder.tv_status.setText(report.getStatus());
                viewHolder.tv_date.setText(report.getDate());
                viewHolder.tv_itemCount1.setText((position+1)+"");
                viewHolder.tv_itemCount2.setText((position+1)+"");
                if(report.getStatus().equalsIgnoreCase("success") ||
                        report.getStatus().equalsIgnoreCase("complete")||
                        report.getStatus().equalsIgnoreCase("active")||
                        report.getStatus().equalsIgnoreCase("successfully submitted")||
                        report.getStatus().equalsIgnoreCase("credit")){
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));
                }
                else if(
                        report.getStatus().equalsIgnoreCase("failure")||
                                report.getStatus().equalsIgnoreCase("debit")){
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
                }
                else {
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.yellow_dark));
                }
                viewHolder.ll_main_layout.setOnClickListener(view->{
                    viewHideShow(viewHolder, report);

                });
                if( reportList.get(viewHolder.getAdapterPosition()).isHide()){
                    viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
                    viewHolder.ll_hideable.setVisibility(View.GONE);
                }
                else{
                    viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
                    viewHolder.ll_hideable.setVisibility(View.VISIBLE);
                }

                break;
            case LOADING:
//                Do nothing
                break;

        }

    }


    private void viewHideShow(@NonNull PaymentFundReportAdapter.ViewHolder viewHolder, PaymentReport report) {
        if(report.isHide()){
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            reportList.get(viewHolder.getAdapterPosition()).setHide(false);
        }
        else {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
            reportList.get(viewHolder.getAdapterPosition()).setHide(true);
        }
    }

    @Override
    public int getItemCount() {
        return reportList == null ? 0 : reportList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == reportList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(PaymentReport report) {
        reportList.add(report);
        notifyItemInserted(reportList.size() - 1);
    }

    public void addAll(List<PaymentReport> reportList) {
        for (PaymentReport report : reportList) {
            add(report);
        }
    }

    public void remove(PaymentReport report) {
        int position = reportList.indexOf(report);
        if (position > -1) {
            reportList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {

            add(new PaymentReport());
            isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = reportList.size() - 1;
        PaymentReport item = null;
        if(position>0)
        item = getItem(position);

        if (item != null) {
            reportList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PaymentReport getItem(int position) {
        return reportList.get(position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_date;
        private TextView tv_id;
        private TextView tv_mode_closingBal;
        private TextView tv_amount;
        private TextView tv_status;
        private TextView tv_request_to;
        private TextView tv_bank_name;
        private TextView tv_branch_code;
        private TextView tv_deposit_date;
        private TextView tv_deposit_slip;
        private TextView tv_customer_remark;
        private TextView tv_ref_id;
        private TextView tv_updated_remark;
        private TextView tv_remark;
        private TextView tv_bankCharge;
        private TextView tv_closingBalance;
        private TextView tv_openingBalance;
        private TextView tv_description;
        private TextView tv_shopName_requestTo;
        private TextView tv_transferFromTo;
        private TextView tv_user;
        private TextView tv_updateDate;
        private TextView tv_requestDate;



        private TextView tv_itemCount1;
        private TextView tv_itemCount2;
        private LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        private LinearLayout ll_arrow_hideable;
        private LinearLayout ll_fund_transfer;
        private LinearLayout ll_payment_report;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_mode_closingBal = itemView.findViewById(R.id.tv_mode);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_request_to = itemView.findViewById(R.id.tv_request_to);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_branch_code = itemView.findViewById(R.id.tv_branch_code);
            tv_deposit_date = itemView.findViewById(R.id.tv_deposit_date);
            tv_deposit_slip = itemView.findViewById(R.id.tv_deposit_slip);
            tv_customer_remark = itemView.findViewById(R.id.tv_customer_remark);
            tv_updated_remark = itemView.findViewById(R.id.tv_updated_remark);

            tv_remark = itemView.findViewById(R.id.tv_remark);
            tv_ref_id = itemView.findViewById(R.id.tv_ref_id);

            tv_bankCharge = itemView.findViewById(R.id.tv_bankCharge);
            tv_closingBalance = itemView.findViewById(R.id.tv_closingBalance);
            tv_openingBalance = itemView.findViewById(R.id.tv_openingBalance);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_shopName_requestTo = itemView.findViewById(R.id.tv_shopName);
            tv_transferFromTo = itemView.findViewById(R.id.tv_transferFromTo);
            tv_user = itemView.findViewById(R.id.tv_user);
            tv_updateDate = itemView.findViewById(R.id.tv_updateDate);
            tv_requestDate = itemView.findViewById(R.id.tv_requestDate);


            tv_status = itemView.findViewById(R.id.tv_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_itemCount1 = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);
            ll_hideable = itemView.findViewById(R.id.ll_hideable);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            ll_fund_transfer = itemView.findViewById(R.id.ll_fund_transfer);
            ll_payment_report = itemView.findViewById(R.id.ll_payment_report);
            ll_arrow_hideable = itemView.findViewById(R.id.ll_arrow_hideable);



        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
