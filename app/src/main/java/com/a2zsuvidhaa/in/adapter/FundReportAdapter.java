package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.model.FundReport;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.util.enums.ReportTypes;

import java.util.ArrayList;
import java.util.List;


public class FundReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<FundReport> reportList;
    private Context context;
    private ReportTypes reportTypes;

    private boolean isLoadingAdded = false;

    public FundReportAdapter(Context context, ReportTypes reportType) {
        this.context = context;
        reportList = new ArrayList<>();
        this.reportTypes = reportType;
    }

    public List<FundReport> getReportList() {
        return reportList;
    }

    public void setReportList(List<FundReport> reportList) {
        this.reportList = reportList;
    }

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
        View v1 = inflater.inflate(R.layout.list_fund_report, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FundReport report = reportList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.tv_date.setText(report.getCreated_at());
                viewHolder.tv_user.setText(report.getUsername());
                viewHolder.tv_bank_ref.setText(report.getBank_ref());
                viewHolder.tv_status.setText(report.getStatus());

                if(reportTypes == ReportTypes.DT_FUND_REPORT){
                    viewHolder.tv_id.setText(report.getId() + "\nRs: " + report.getClosing_bal());
                    viewHolder.tv_paymentMode.setText(report.getWallet());
                    viewHolder.tv_amount.setText(report.getNumber()+"\n"+report.getCredit_amount());
                    viewHolder.ll_fundReport.setVisibility(View.GONE);
                    viewHolder.ll_dtReport.setVisibility(View.VISIBLE);

                    viewHolder.tv_transferFromTo.setText(report.getTransferFromTo());
                    viewHolder.tv_shopName.setText(report.getShopName());
                    viewHolder.tv_description.setText(report.getDescription());
                    viewHolder.tv_openingBalance.setText(report.getOpening_bal());
                    viewHolder.tv_bankCharge.setText(report.getBank_charge());
                    viewHolder.tv_remark.setText(report.getRemark());
                }
                else if(reportTypes == ReportTypes.FUND_REPORT){
                    viewHolder.tv_id.setText(String.valueOf(report.getId()));
                    viewHolder.tv_paymentMode.setText(report.getPayment_mode());
                    viewHolder.tv_amount.setText(report.getWallet_amount());
                    viewHolder.tv_deposite.setText(report.getDeposit_date());
                    viewHolder.tv_bank_name.setText(report.getBank_name());
                    viewHolder.tv_requestFor.setText(report.getRequest_for());
                    viewHolder.tv_branch.setText(report.getBranch_name());
                    viewHolder.tv_requestRemark.setText(report.getRequest_remark());
                    viewHolder.tv_approvalRemark.setText(report.getApproval_remark());
                    viewHolder.tv_updateRemark.setText(report.getUpdate_remark());

                    viewHolder.ll_fundReport.setVisibility(View.VISIBLE);
                    viewHolder.ll_dtReport.setVisibility(View.GONE);
                }








                viewHolder.tv_itemCount1.setText(String.valueOf((position+1)));
                viewHolder.tv_itemCount2.setText(String.valueOf((position+1)));

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


    private void viewHideShow(@NonNull FundReportAdapter.ViewHolder viewHolder, FundReport report) {
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

    public void add(FundReport report) {
        reportList.add(report);
        notifyItemInserted(reportList.size() - 1);
    }

    public void addAll(List<FundReport> reportList) {
        for (FundReport report : reportList) {
            add(report);
        }
    }

    public void remove(FundReport report) {
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

            add(new FundReport());
            isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = reportList.size() - 1;
        FundReport item = null;
        if(position>0)
        item = getItem(position);

        if (item != null) {
            reportList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public FundReport getItem(int position) {
        return reportList.get(position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_date;
        private TextView tv_paymentMode;
        private TextView tv_id;
        private TextView tv_amount;
        private TextView tv_status;
        private TextView tv_user;
        private TextView tv_bank_ref;


        //view for fund report
        private TextView tv_deposite;
        private TextView tv_bank_name;
        private TextView tv_requestFor;
        private TextView tv_branch;
        private TextView tv_requestRemark;
        private TextView tv_approvalRemark;
        private TextView tv_updateRemark;
        private LinearLayout ll_fundReport;

        //view for dt report
        private TextView tv_transferFromTo;
        private TextView tv_shopName;
        private TextView tv_description;
        private TextView tv_openingBalance;
        private TextView tv_bankCharge;
        private TextView tv_remark;
        private LinearLayout ll_dtReport;

        private TextView tv_itemCount1;
        private TextView tv_itemCount2;
        private LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        private LinearLayout ll_arrow_hideable;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_paymentMode = itemView.findViewById(R.id.tv_paymentMode);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_user = itemView.findViewById(R.id.tv_user);
            tv_bank_ref = itemView.findViewById(R.id.tv_bank_ref);

            tv_deposite = itemView.findViewById(R.id.tv_deposite);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_requestFor = itemView.findViewById(R.id.tv_requestFor);
            tv_branch = itemView.findViewById(R.id.tv_branch);
            tv_requestRemark = itemView.findViewById(R.id.tv_requestRemark);
            tv_approvalRemark = itemView.findViewById(R.id.tv_approvalRemark);
            tv_updateRemark = itemView.findViewById(R.id.tv_updateRemark);
            ll_fundReport = itemView.findViewById(R.id.ll_fundReport);

            tv_transferFromTo = itemView.findViewById(R.id.tv_transferFromTo);
            tv_shopName = itemView.findViewById(R.id.tv_shopName);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_openingBalance = itemView.findViewById(R.id.tv_openingBalance);
            tv_bankCharge = itemView.findViewById(R.id.tv_bankCharge);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            ll_dtReport = itemView.findViewById(R.id.ll_dtReport);

            tv_itemCount1 = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);
            ll_hideable = itemView.findViewById(R.id.ll_hideable);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            ll_arrow_hideable = itemView.findViewById(R.id.ll_arrow_hideable);



        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
