package com.a2zsuvidhaa.in.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.model.Report;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.util.enums.ReportTypes;

import java.util.ArrayList;
import java.util.List;


public class ReportAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ReportTypes reportType;

    private List<Report> reportList;
    private Context context;

    private boolean isLoadingAdded = false;

    public ReportAdapter2(Context context, ReportTypes reportTypes) {
        this.context = context;
        reportList = new ArrayList<>();
        this.reportType = reportTypes;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
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
        View v1 = inflater.inflate(R.layout.list_ledger_report2, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Report report = reportList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;

                if (reportType == ReportTypes.ACCOUNT_STATEMENT) {
                    viewHolder.ll_accountStatement.setVisibility(View.VISIBLE);
                    viewHolder.tv_product.setText(report.getProduct());
                    viewHolder.tv_name.setText(report.getName());
                } else if (reportType == ReportTypes.NETWORK_RECHARGE) {
                    viewHolder.ll_networkRecharge.setVisibility(View.VISIBLE);
                    viewHolder.tv_product.setText(report.getTxnType() + "\n" + report.getProviderName());
                    viewHolder.tv_name.setText(report.getUserName());
                }

                viewHolder.tv_date.setText(report.getDate());


                if (report.getBalance() != null) {
                    viewHolder.tv_idBalance.setText(report.getId() + "\nRs: " + report.getBalance());
                } else {
                    viewHolder.tv_idBalance.setText(report.getId());
                }


                viewHolder.tv_status.setText(report.getStatus());

                viewHolder.tv_amountNumber.setText(report.getNumber() + "\nRs: " + report.getAmount());
                viewHolder.tv_itemCount.setText(String.valueOf((position + 1)));
                viewHolder.tv_itemCount2.setText(String.valueOf((position + 1)));

                viewHolder.tv_tds.setText(report.getTds());
                viewHolder.tv_txnId.setText(report.getTxnId());
                viewHolder.tv_opId.setText(report.getOpId());


                viewHolder.tv_mobile.setText(report.getMobileNumber());

                viewHolder.tv_description.setText(report.getDescription());
                viewHolder.tv_openingBalance.setText(report.getOpeningBalance());
                viewHolder.tv_creditAmount.setText(report.getCredtAmount());
                viewHolder.tv_debitAmount.setText(report.getDebitAmount());
                viewHolder.tv_remark.setText(report.getRemark());
                viewHolder.tv_bank_name.setText(report.getBankName());
                viewHolder.tv_mode.setText(report.getMode());


                if (report.getStatus().equalsIgnoreCase("success") ||
                        report.getStatus().equalsIgnoreCase("complete") ||
                        report.getStatus().equalsIgnoreCase("active") ||
                        report.getStatus().equalsIgnoreCase("successfully submitted") ||
                        report.getStatus().equalsIgnoreCase("credit")) {
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));
                } else if (
                        report.getStatus().equalsIgnoreCase("failure") ||
                                report.getStatus().equalsIgnoreCase("debit")) {
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.yellow_dark));
                }
                viewHolder.ll_main_layout.setOnClickListener(view -> {
                    viewHideShow(viewHolder, report);

                });

                if (reportList.get(viewHolder.getAdapterPosition()).isHide()) {
                    viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
                    viewHolder.ll_hideable.setVisibility(View.GONE);
                } else {
                    viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
                    viewHolder.ll_hideable.setVisibility(View.VISIBLE);
                }

                break;
            case LOADING:

                break;

        }

    }


    private void viewHideShow(@NonNull ReportAdapter2.ViewHolder viewHolder, Report report) {
        if (report.isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            reportList.get(viewHolder.getAdapterPosition()).setHide(false);
        } else {
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

    public void add(Report report) {
        reportList.add(report);
        notifyItemInserted(reportList.size() - 1);
    }

    public void addAll(List<Report> reportList) {
        for (Report report : reportList) {
            add(report);
        }
    }

    public void remove(Report report) {
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

        add(new Report());
        isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;


        int position = reportList.size() - 1;
        Report item = null;
        if (position > 0)
            item = getItem(position);

        if (item != null) {
            reportList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Report getItem(int position) {
        return reportList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_date;
        private TextView tv_product;
        private TextView tv_idBalance;
        private TextView tv_amountNumber;
        private TextView tv_status;

        private TextView tv_mobile;
        private TextView tv_name;
        private TextView tv_description;
        private TextView tv_openingBalance;
        private TextView tv_gst;
        private TextView tv_tds;
        private TextView tv_txnId;
        private TextView tv_opId;
        private TextView tv_creditAmount;
        private TextView tv_debitAmount;
        private TextView tv_remark;
        private TextView tv_bank_name;
        private TextView tv_mode;


        private TextView tv_itemCount;
        private TextView tv_itemCount2;

        private LinearLayout ll_accountStatement;
        private LinearLayout ll_networkRecharge;
        private LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        private LinearLayout ll_arrow_hideable;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            //ll_account statement
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_openingBalance = itemView.findViewById(R.id.tv_openingBalance);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            //ll_network recharge
            tv_gst = itemView.findViewById(R.id.tv_gst);
            tv_tds = itemView.findViewById(R.id.tv_tds);
            tv_txnId = itemView.findViewById(R.id.tv_txnId);
            tv_opId = itemView.findViewById(R.id.tv_opId);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_product = itemView.findViewById(R.id.tv_product);
            tv_idBalance = itemView.findViewById(R.id.tv_idBalance);
            tv_amountNumber = itemView.findViewById(R.id.tv_amountNumber);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_creditAmount = itemView.findViewById(R.id.tv_creditAmount);
            tv_debitAmount = itemView.findViewById(R.id.tv_debitAmount);
            tv_mode = itemView.findViewById(R.id.tv_mode);

            tv_itemCount = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);


            ll_accountStatement = itemView.findViewById(R.id.ll_accountStatement);
            ll_networkRecharge = itemView.findViewById(R.id.ll_networkRecharge);
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

