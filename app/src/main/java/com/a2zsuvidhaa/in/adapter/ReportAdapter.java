package com.a2zsuvidhaa.in.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.InvoiceSlipActivity;
import com.a2zsuvidhaa.in.listener.ReportReceiptClickListener;
import com.a2zsuvidhaa.in.util.enums.ReportTypes;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.model.Report;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class ReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isChecking = false;
    private boolean isChecking2 = false;
    private ReportTypes reportType;

    private List<Report> reportList;
    private Context context;

    private boolean isLoadingAdded = false;
    private String statusValue;
    public static String REPORT_ID = "report_id";
    public static String REPORT_API_ID = "report_api_id";

    public ReportAdapter(Context context, ReportTypes reportTypes) {
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
        View v1 = inflater.inflate(R.layout.list_ledger_report, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Report report = reportList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;
                if (reportType == ReportTypes.LEDGER_REPORT)
                    viewHolder.ll_ledger_report.setVisibility(View.VISIBLE);
                else viewHolder.ll_ledger_report.setVisibility(View.GONE);

                viewHolder.tv_date.setText(report.getDate());
                viewHolder.tv_id.setText(report.getId());
                viewHolder.tv_serviceName.setText(report.getOperator());
                viewHolder.tv_closingBalance.setText(report.getBalance());
                viewHolder.tv_amount.setText(report.getAmount());
                viewHolder.tv_number.setText(report.getNumber());
                if (reportType == ReportTypes.LEDGER_REPORT) {
                    viewHolder.ll_op_id.setVisibility(View.GONE);
                } else if (reportType == ReportTypes.USAGE_REPORT) {
                    viewHolder.tv_mobile.setText(report.getRmnNumber());
                    viewHolder.ll_op_id.setVisibility(View.VISIBLE);
                }
                viewHolder.tv_status.setText(report.getStatus());
                viewHolder.tv_txnId.setText(report.getOperatorId());
                viewHolder.tv_operator.setText(report.getOperator());


                viewHolder.tv_remitterNumber.setText(report.getMobileNumber());
                viewHolder.tv_accountNo.setText(report.getBeneAccount());
                viewHolder.tv_beneName.setText(report.getBeneName());
                viewHolder.tv_ifse_code.setText(report.getIfscCode());
                viewHolder.tv_bank_name.setText(report.getBankName());
                //viewHolder.tv_txnId.setText(report.getTxnId());
                viewHolder.tv_txnType.setText(report.getTxnType());
                viewHolder.tv_remark.setText(report.getRemark());
                viewHolder.tv_description.setText(report.getDescription());
                viewHolder.tv_creditDebit.setText(report.getCreditDebit());
                viewHolder.tv_creditAmount.setText(report.getCredtAmount());
                viewHolder.tv_debitAmount.setText(report.getDebitAmount());
                viewHolder.tv_tds.setText(report.getTds());
                viewHolder.tv_serviceTax.setText(report.getServiceTax());
                viewHolder.tv_openingBalance.setText(report.getOpeningBalance());
                viewHolder.tv_r2rTransfer.setText(report.getR2rTransfer());
                viewHolder.tv_bankRRNumber.setText(report.getrRNo());


                viewHolder.tv_itemCount.setText(String.valueOf((position + 1)));
                viewHolder.tv_itemCount2.setText(String.valueOf((position + 1)));


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
                viewHolder.btn_check.setOnClickListener(view -> {
                    if (!isChecking) {
                        isChecking = true;
                        checkImpsStatus(viewHolder, viewHolder.getAdapterPosition());
                    } else
                        MakeToast.show(context, "One item is already processing\nplease wait...");
                });

                if (reportList.get(viewHolder.getAdapterPosition()).isHide()) {
                    viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
                    viewHolder.ll_hideable.setVisibility(View.GONE);
                } else {
                    viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
                    viewHolder.ll_hideable.setVisibility(View.VISIBLE);
                }
                if (report.getApiId().equalsIgnoreCase("25")) {
                    viewHolder.btn_check.setVisibility(View.GONE);
                } else {
                    if (report.getStatusId().equalsIgnoreCase("1")
                            || report.getStatusId().equalsIgnoreCase("3")
                            || report.getStatusId().equalsIgnoreCase("18")
                            || report.getStatusId().equalsIgnoreCase("34")
                    ) {
                        viewHolder.btn_check.setAlpha(1f);
                        viewHolder.btn_check.setEnabled(true);
                        viewHolder.btn_check.setVisibility(View.GONE);
                    } else {
                        viewHolder.btn_check.setAlpha(0.5f);
                        viewHolder.btn_check.setEnabled(false);
                        viewHolder.btn_check.setVisibility(View.GONE);
                    }
                }
                viewHolder.btn_view_complain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = AppDialogs.complain(context);
                        Spinner spinner = dialog.findViewById(R.id.spn_searchType);
                        EditText remark = dialog.findViewById(R.id.ed_remark);
                        Button submit = dialog.findViewById(R.id.btn_submit);
                        ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                        btn_cancel.setOnClickListener(view3 -> dialog.dismiss());

                        String[] prepaidStrings = {"Amount Not Credit", "Recharge Not Credit", "Pending Txn", "Others"};
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(context),
                                R.layout.spinner_layout, prepaidStrings);
                        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
                        spinner.setAdapter(dataAdapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                rsn = prepaidStrings[position];

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        submit.setOnClickListener(view1 -> {
                            submit.setText("Loading...");
                            submitComplain(rsn, remark.getText().toString(), report.getId(), dialog);
                        });

                        dialog.show();
                    }
                });

                if (report.getApiId() != null) {
                    if (report.getApiId().equalsIgnoreCase("5")//a2z_wallet
                            || report.getApiId().equalsIgnoreCase("25")//a2z_wallet_plus
                            || report.getApiId().equalsIgnoreCase("4")//dmt1
                            || report.getApiId().equalsIgnoreCase("1")//electricity pay
                            || report.getApiId().equalsIgnoreCase("27")//BBPS1 Bill pay
                            || report.getApiId().equalsIgnoreCase("30")//BBPS1 Bill pay
                            || report.getApiId().equalsIgnoreCase("40")//BBPS2 Bill pay
                            || report.getApiId().equalsIgnoreCase("10")//AEPS
                            || report.getApiId().equalsIgnoreCase("28")//Aadhar pay
                            // ||report.getApiId().equalsIgnoreCase("13")//prepaid recharge
                            || report.getApiId().equalsIgnoreCase("16")//dmt2
                            || report.getApiId().equalsIgnoreCase("58")//aeps 3
                            || report.getApiId().equalsIgnoreCase("15")
                            || report.getApiId().equalsIgnoreCase("46")
                    ) {

                        viewHolder.btn_view_slip.setVisibility(View.VISIBLE);


                        //25-> a2z wallet
                        //16->dmt 2
                        //50->dmt 1
                        //52->a2z plus 2
                        //53 a2z plus 3
                        //58 -> aeps 3
                        //47 -> aadhaar Pay 2 (Instant Pay)
                        //46 -> Aeps 2 (Instant Pay)
                        //28 -> Aadhar pay 1 (FingPay)
                        //10 -> Aeps 1 (fingpay)

                        if(report.getApiId().equals("2"))
                            viewHolder.btn_view_slip.setVisibility(View.GONE);

                        viewHolder.btn_view_slip.setOnClickListener(v -> {
                            if (report.getApiId().equals("25")
                                    || report.getApiId().equals("16")
                                    || report.getApiId().equals("46")
                                    || report.getApiId().equals("58")
                                    || report.getApiId().equals("28")
                                    || report.getApiId().equals("10")
                            ) {
                                if (listener != null) listener.onReceiptClick(report);
                            }
                            else {

                                Intent intent = new Intent(context, InvoiceSlipActivity.class);
                                intent.putExtra(REPORT_ID, report.getId());
                                intent.putExtra(REPORT_API_ID, Integer.parseInt(report.getApiId()));
                                context.startActivity(intent);
                            }
                        });




                    } else viewHolder.btn_view_slip.setVisibility(View.GONE);
                } else viewHolder.btn_view_slip.setVisibility(View.GONE);

                viewHolder.btn_action.setOnClickListener(view -> {
                    if (!isChecking2) {
                        isChecking2 = true;
                        updateTransactionStatus(viewHolder, viewHolder.getAdapterPosition());
                    } else
                        MakeToast.show(context, "One item is already processing\nplease wait...");
                });

                if (report.getStatus().equalsIgnoreCase("Success")
                        || report.getStatus().equalsIgnoreCase("Pending")
                        || report.getStatusId().equalsIgnoreCase("Inprocess")) {
                    viewHolder.btn_action.setAlpha(1f);
                    viewHolder.btn_action.setEnabled(true);
                } else {
                    viewHolder.btn_action.setAlpha(0.5f);
                    viewHolder.btn_action.setEnabled(false);
                }


                break;
            case LOADING:
//                Do nothing
                break;

        }

    }

    String rsn = "";

    private void viewHideShow(@NonNull ReportAdapter.ViewHolder viewHolder, Report report) {
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
        private TextView tv_closingBalance;
        private TextView tv_serviceName;
        private TextView tv_amount;
        private TextView tv_number;
        private TextView tv_status;
        private TextView tv_id;

        private TextView tv_remitterNumber;
        private TextView tv_accountNo;
        private TextView tv_mobile;
        private TextView tv_beneName;
        private TextView tv_bank_name;
        private TextView tv_ifse_code;
        private TextView tv_txnId;
        private TextView tv_remark;
        private TextView tv_bankRRNumber;
        private TextView tv_description;
        private TextView tv_creditDebit;
        private TextView tv_openingBalance;
        private TextView tv_creditAmount;
        private TextView tv_debitAmount;
        private TextView tv_tds;
        private TextView tv_serviceTax;
        private TextView tv_txnType;
        private TextView tv_r2rTransfer;
        private TextView tv_operator;

        private TextView tv_itemCount;
        private TextView tv_itemCount2;


        private Button btn_check;
        private Button btn_action;
        private Button btn_view_slip, btn_view_complain;

        private LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        private LinearLayout ll_arrow_hideable;
        private LinearLayout ll_op_id;
        private LinearLayout ll_ledger_report;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_id = itemView.findViewById(R.id.tv_id);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_closingBalance = itemView.findViewById(R.id.tv_closingBalance);
            tv_serviceName = itemView.findViewById(R.id.tv_serviceName);
            tv_status = itemView.findViewById(R.id.tv_status);
            btn_view_slip = itemView.findViewById(R.id.btn_view_slip);
            btn_view_complain = itemView.findViewById(R.id.btn_view_complain);
            tv_remitterNumber = itemView.findViewById(R.id.tv_remitterNumber);
            tv_accountNo = itemView.findViewById(R.id.tv_accountNo);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_beneName = itemView.findViewById(R.id.tv_beneName);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_ifse_code = itemView.findViewById(R.id.tv_ifse_code);
            tv_txnId = itemView.findViewById(R.id.tv_txnId);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            tv_bankRRNumber = itemView.findViewById(R.id.tv_bankRRNumber);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_creditDebit = itemView.findViewById(R.id.tv_creditDebit);
            tv_openingBalance = itemView.findViewById(R.id.tv_openingBalance);
            tv_creditAmount = itemView.findViewById(R.id.tv_creditAmount);
            tv_debitAmount = itemView.findViewById(R.id.tv_debitAmount);
            tv_tds = itemView.findViewById(R.id.tv_tds);
            tv_serviceTax = itemView.findViewById(R.id.tv_serviceTax);
            tv_txnType = itemView.findViewById(R.id.tv_txnType);
            tv_r2rTransfer = itemView.findViewById(R.id.tv_r2rTransfer);
            tv_operator = itemView.findViewById(R.id.tv_operator);


            tv_itemCount = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);


            btn_check = itemView.findViewById(R.id.btn_action_response);
            btn_action = itemView.findViewById(R.id.btn_action);

            ll_hideable = itemView.findViewById(R.id.ll_hideable);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            ll_arrow_hideable = itemView.findViewById(R.id.ll_arrow_hideable);
            ll_op_id = itemView.findViewById(R.id.ll_op_id);
            ll_ledger_report = itemView.findViewById(R.id.ll_ledger_report);


        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    private void submitComplain(String rsn, String remark, String txnid, Dialog dialog) {

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.COMPLAIN,
                response -> {
                    try {
                        Button button = dialog.findViewById(R.id.btn_submit);
                        button.setText("Submit");
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");


                        if (status.equalsIgnoreCase("1")) {

                            //{"status":1,"message":"Amount Refunded Successful"}
                            dialog.dismiss();
                            Dialog dialog1 = AppDialogs.transactionStatus(context, message, 1);
                            Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog1.dismiss());
                            dialog1.show();

                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else {
                            MakeToast.showCenter(context, message);
                        }


                    } catch (JSONException e) {

                        MakeToast.showCenter(context, e.getMessage());
                    }
                },
                error -> {

                    MakeToast.showCenter(context, "onError");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", String.valueOf(AppPreference.getInstance(context).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(context).getToken()));
                param.put("complainTxnId", "" + txnid);
                param.put("issueType", rsn);
                param.put("complainRemark", remark);
                return param;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void checkImpsStatus(ViewHolder viewHolder, int position) {

        String url = APIs.CHECK_IMPS_STATUS + "?" + APIs.USER_TAG + "=" +
                AppPreference.getInstance(context).getId()
                + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(context).getToken()
                + "&id=" + reportList.get(position).getId();

        // Log.d("CheckStatusTesting","check status URL : "+url);

        viewHolder.btn_check.setText("checking..");
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    //      Log.d("CheckStatusTesting","onResponse : "+ response);
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("msg");
                        if (status.equalsIgnoreCase("1")) {

                            Dialog dialog = AppDialogs.checkStatus(context);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            TextView tv_message = dialog.findViewById(R.id.tv_message);
                            tv_message.setText(message);
                            if (message.equalsIgnoreCase("SUCCESS")) {
                                tv_message.setTextColor(context.getResources().getColor(R.color.success));
                            } else
                                tv_message.setTextColor(context.getResources().getColor(R.color.yellow_dark));
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                            dialog.show();
                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {

                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else {
                            Dialog dialog = AppDialogs.checkStatus(context);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            TextView tv_message = dialog.findViewById(R.id.tv_message);
                            tv_message.setTextColor(context.getResources().getColor(R.color.text_color_3));
                            tv_message.setText(message);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                            dialog.show();
                        }


                    } catch (JSONException e) {
                        // Log.d("CheckStatusTesting","onException : "+e.getMessage());
                    }
                    viewHolder.btn_check.setText("CHECK");
                    isChecking = false;
                },
                error -> {
                    // Log.d("CheckStatusTesting","onError : "+error.getMessage());
                    isChecking = false;
                    viewHolder.btn_check.setText("CHECK");
                }) {

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void updateTransactionStatus(ViewHolder viewHolder, int position) {

        viewHolder.btn_action.setText("Loading...");
        final StringRequest request = new StringRequest(Request.Method.GET,
                APIs.GET_TRANSACTION_DETAILS + "?" + APIs.USER_TAG + "=" +
                        AppPreference.getInstance(context).getId()
                        + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(context).getToken()
                        + "&id=" + reportList.get(position).getId(),
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            JSONObject results = jsonObject.getJSONObject("results");


                            Dialog dialog = AppDialogs.updateTransaction(context);

                            RelativeLayout rl_actionButton = dialog.findViewById(R.id.rl_action_button);
                            LinearLayout ll_processing = dialog.findViewById(R.id.ll_processing);

                            TextView tv_recordNumber = dialog.findViewById(R.id.tv_recordNumber);
                            TextView tv_number = dialog.findViewById(R.id.tv_number);
                            TextView tv_customerMobNumber = dialog.findViewById(R.id.tv_customerMobNumber);
                            TextView tv_txnAmount = dialog.findViewById(R.id.tv_txnAmount);
                            TextView tv_transaction_id = dialog.findViewById(R.id.tv_transaction_id);
                            TextView tv_providerName = dialog.findViewById(R.id.tv_providerName);
                            TextView tv_serviceName = dialog.findViewById(R.id.tv_serviceName);
                            TextView tv_userName = dialog.findViewById(R.id.tv_userName);
                            EditText ed_rrNumber = dialog.findViewById(R.id.ed_rrNumber);
                            Spinner spn_status = dialog.findViewById(R.id.spn_status);
                            Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                            Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
                            getStatusFromSpinner(spn_status);

                            btn_cancel.setOnClickListener(view -> dialog.dismiss());

                            String number = results.getString("number");
                            String id = results.getString("id");
                            String txnId = results.getString("txnId");

                            tv_number.setText(number);
                            tv_txnAmount.setText(results.getString("amount"));
                            tv_transaction_id.setText(txnId);
                            tv_customerMobNumber.setText(results.getString("customerNumber"));
                            tv_recordNumber.setText(id);
                            tv_providerName.setText(results.getString("providerName"));
                            tv_serviceName.setText(results.getString("serviceName"));
                            tv_userName.setText(results.getString("userName"));

                            btn_confirm.setOnClickListener(view -> {

                                String rrNumber = ed_rrNumber.getText().toString();
                                update(id, number, txnId, rrNumber, dialog, rl_actionButton, ll_processing, viewHolder);

                            });

                            dialog.show();


                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {

                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else {
                            Dialog dialog = AppDialogs.checkStatus(context);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            TextView tv_message = dialog.findViewById(R.id.tv_message);
                            tv_message.setTextColor(context.getResources().getColor(R.color.text_color_3));
                            tv_message.setText(message);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                            dialog.show();
                        }


                    } catch (JSONException e) {
                    }
                    viewHolder.btn_action.setText("action");
                    isChecking2 = false;
                },
                error -> {

                    isChecking2 = false;
                    viewHolder.btn_action.setText("action");
                }) {

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void update(String id, String number, String txnId, String rrNumber, Dialog dialog,
                        RelativeLayout rl_actionButton, LinearLayout ll_processing, ViewHolder viewHolder) {

        rl_actionButton.setVisibility(View.GONE);
        ll_processing.setVisibility(View.VISIBLE);

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.UPDATE_TRANSACTION,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            //{"status":1,"message":"Amount Refunded Successful"}
                            dialog.dismiss();
                            Dialog dialog1 = AppDialogs.transactionStatus(context, message, 1);
                            Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog1.dismiss());
                            dialog1.show();

                            if (statusValue.equalsIgnoreCase("1")) {

                                viewHolder.tv_status.setText("Success");
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));

                            } else if (statusValue.equalsIgnoreCase("2")) {
                                viewHolder.tv_status.setText("Refund-Suc");
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.yellow_dark));


                                viewHolder.btn_check.setAlpha(0.5f);
                                viewHolder.btn_check.setEnabled(false);
                                viewHolder.btn_action.setAlpha(0.5f);
                                viewHolder.btn_action.setEnabled(false);
                                reportList.get(viewHolder.getAdapterPosition()).setStatusId("2");
                            } else if (statusValue.equalsIgnoreCase("3")) {
                                viewHolder.tv_status.setText("Pending");
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.yellow_dark));
                            }


                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else {
                            MakeToast.show(context, message);
                        }

                        rl_actionButton.setVisibility(View.VISIBLE);
                        ll_processing.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        rl_actionButton.setVisibility(View.VISIBLE);
                        ll_processing.setVisibility(View.GONE);
                        MakeToast.show(context, e.getMessage());
                    }
                },
                error -> {
                    rl_actionButton.setVisibility(View.VISIBLE);
                    ll_processing.setVisibility(View.GONE);
                    MakeToast.show(context, "onError");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", String.valueOf(AppPreference.getInstance(context).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(context).getToken()));
                param.put("id", id);
                param.put("number", number);
                param.put("txnid", txnId);
                param.put("status", statusValue);
                return param;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getStatusFromSpinner(Spinner spn_status) {

        HashMap<String, String> values = new HashMap<>();
        values.put("Success", "1");
        values.put("Failure", "2");
        values.put("Pending", "3");


        String[] prepaidStrings = values.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_status.setAdapter(dataAdapter);

        spn_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusValue = values.get(spn_status.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private ReportReceiptClickListener listener;

    public void setupListener(ReportReceiptClickListener listener) {
        this.listener = listener;
    }
}

