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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.model.Remark;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.listener.OnCallbackListener;
import com.a2zsuvidhaa.in.model.AgentRequestView;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AgentRequestViewAdapter extends RecyclerView.Adapter<AgentRequestViewAdapter.ViewHolder> {

    private Spinner spn_remark;
    private Spinner spn_bankCharge;
    private Spinner spn_status;
    private Button btn_sendNow;
    private Dialog mainDialogApprove;
    private RelativeLayout rl_action_button;
    private LinearLayout ll_processing;
    private HashMap<String, String> remarkHashMap = new HashMap<>();
    private HashMap<String, String> statusHashMap = new HashMap<>();
    private HashMap<String, String> bankchargeHashMap = new HashMap<>();
    private static final String TAG = "AgentRequestAdapter";
    private Context context;
    private ArrayList<AgentRequestView> agentRequestViewList;
    private String strStatusId;
    private String strRemarkId;
    private String loadCashId;

    public AgentRequestViewAdapter(Context context, ArrayList<AgentRequestView> agentRequestViewList) {
        this.context = context;
        this.agentRequestViewList = agentRequestViewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_agent_request_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        AgentRequestView agentRequestView = agentRequestViewList.get(i);

        viewHolder.tv_created_at.setText(agentRequestView.getCreated_at());
        viewHolder.tv_useridName.setText(agentRequestView.getUser_id() + "\n" + agentRequestView.getUser_name());
        viewHolder.tv_idAmount.setText(agentRequestView.getId() + "\nRs: " + agentRequestView.getAmount());
        viewHolder.tv_status.setText(agentRequestView.getStatus());
        viewHolder.tv_firm_name.setText(agentRequestView.getFirm_name());
        viewHolder.tv_role.setText(agentRequestView.getRole());
        viewHolder.tv_mobile.setText(agentRequestView.getMobile());
        viewHolder.tv_mode.setText(agentRequestView.getMode());
        viewHolder.tv_branch_code.setText(agentRequestView.getBranch_code());
        viewHolder.tv_online_payment_mode.setText(agentRequestView.getOnline_payment_mode());
        viewHolder.tv_deposit_date.setText(agentRequestView.getDeposit_date());
        viewHolder.tv_bank_name.setText(agentRequestView.getBank_name());
        viewHolder.tv_remark.setText(agentRequestView.getRemark());
        viewHolder.tv_slip.setText(agentRequestView.getSlip());
        viewHolder.tv_ref_id.setText(agentRequestView.getRef_id());
        viewHolder.tv_itemCount.setText(String.valueOf((i + 1)));
        viewHolder.tv_itemCount2.setText(String.valueOf((i + 1)));

        if (agentRequestView.getStatus().equalsIgnoreCase("success") ||
                agentRequestView.getStatus().equalsIgnoreCase("complete") ||
                agentRequestView.getStatus().equalsIgnoreCase("active") ||
                agentRequestView.getStatus().equalsIgnoreCase("successfully submitted") ||
                agentRequestView.getStatus().equalsIgnoreCase("credit")) {
            viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));
        } else if (
                agentRequestView.getStatus().equalsIgnoreCase("failure") ||
                        agentRequestView.getStatus().equalsIgnoreCase("debit")) {
            viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.yellow_dark));
        }
        viewHolder.ll_main_layout.setOnClickListener(view -> {
            viewHideShow(viewHolder, agentRequestView);

        });
        if (agentRequestViewList.get(viewHolder.getAdapterPosition()).isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
        }
        viewHolder.btn_action.setOnClickListener(view -> {
            loadCashId = agentRequestView.getId();
            getCashLoad(viewHolder.getAdapterPosition());
        });


    }

    private void viewHideShow(@NonNull ViewHolder viewHolder, AgentRequestView report) {
        if (report.isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            agentRequestViewList.get(viewHolder.getAdapterPosition()).setHide(false);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
            agentRequestViewList.get(viewHolder.getAdapterPosition()).setHide(true);
        }
    }


    @Override
    public int getItemCount() {
        return agentRequestViewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_created_at;
        TextView tv_useridName;
        TextView tv_idAmount;
        TextView tv_status;
        TextView tv_firm_name;
        TextView tv_role;
        TextView tv_mobile;
        TextView tv_mode;
        TextView tv_branch_code;
        TextView tv_online_payment_mode;
        TextView tv_deposit_date;
        TextView tv_bank_name;
        TextView tv_remark;
        TextView tv_slip;
        TextView tv_ref_id;

        TextView tv_itemCount;
        TextView tv_itemCount2;


        LinearLayout ll_arrow_hideable;
        LinearLayout ll_hideable;
        LinearLayout ll_main_layout;
        Button btn_action;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_created_at = itemView.findViewById(R.id.tv_created_at);
            tv_useridName = itemView.findViewById(R.id.tv_useridName);
            tv_idAmount = itemView.findViewById(R.id.tv_idAmount);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_firm_name = itemView.findViewById(R.id.tv_firm_name);
            tv_role = itemView.findViewById(R.id.tv_role);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_mode = itemView.findViewById(R.id.tv_mode);
            tv_branch_code = itemView.findViewById(R.id.tv_branch_code);
            tv_online_payment_mode = itemView.findViewById(R.id.tv_online_payment_mode);
            tv_deposit_date = itemView.findViewById(R.id.tv_deposit_date);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            tv_slip = itemView.findViewById(R.id.tv_slip);
            tv_ref_id = itemView.findViewById(R.id.tv_ref_id);
            tv_itemCount = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);


            ll_arrow_hideable = itemView.findViewById(R.id.ll_arrow_hideable);
            ll_hideable = itemView.findViewById(R.id.ll_hideable);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            btn_action = itemView.findViewById(R.id.btn_action);
        }
    }

    private void getCashLoad(int position) {

        mainDialogApprove = AppDialogs.requestApprove(context);

        TextView tv_amount = mainDialogApprove.findViewById(R.id.tv_amount);
        TextView tv_agentName = mainDialogApprove.findViewById(R.id.tv_agentName);
        TextView tv_refId = mainDialogApprove.findViewById(R.id.tv_refId);
        EditText ed_remark = mainDialogApprove.findViewById(R.id.ed_remark);

        AgentRequestView agentRequestView = agentRequestViewList.get(position);
        tv_amount.setText(agentRequestView.getAmount());
        tv_agentName.setText(agentRequestView.getUser_name());
        tv_refId.setText(agentRequestView.getRef_id());

        rl_action_button = mainDialogApprove.findViewById(R.id.rl_action_button);
        ll_processing = mainDialogApprove.findViewById(R.id.ll_processing);

        spn_remark = mainDialogApprove.findViewById(R.id.spn_remark);
        spn_bankCharge = mainDialogApprove.findViewById(R.id.spn_bankCharge);
        spn_status = mainDialogApprove.findViewById(R.id.spn_status);
        Button btn_cancel = mainDialogApprove.findViewById(R.id.btn_cancel);
        btn_sendNow = mainDialogApprove.findViewById(R.id.btn_sendNow);
        mainDialogApprove.show();
        btn_cancel.setOnClickListener(view -> mainDialogApprove.dismiss());
        btn_sendNow.setOnClickListener(view -> approveAgentRequest(ed_remark.getText().toString()));

        parseBankChargeObject();
        parseRemarkObject(position);
        parseStatusObject();

    }

    private void parseBankChargeObject() {

        bankchargeHashMap.clear();
        bankchargeHashMap.put("0.00", "0.00");

        String[] prepaidStrings = bankchargeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_bankCharge.setAdapter(dataAdapter);

        spn_bankCharge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void parseStatusObject() {

        statusHashMap.clear();
        statusHashMap.put("Approve", "1");
        statusHashMap.put("Reject", "2");
        statusHashMap.put("Pending", "3");


        String[] prepaidStrings = statusHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_status.setAdapter(dataAdapter);
        spn_status.setSelection(2);
        spn_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strStatusId = statusHashMap.get(spn_status.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void parseRemarkObject(int position) {

        remarkHashMap.clear();
        ArrayList<Remark> remarks = agentRequestViewList.get(position).getRemarks();
        for (int i = 0; i < remarks.size(); i++) {
            remarkHashMap.put(remarks.get(i).getRemark(), remarks.get(i).getKey());
        }


        String[] prepaidStrings = remarkHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_remark.setAdapter(dataAdapter);

        spn_remark.setSelection(1);
        spn_remark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strRemarkId = remarkHashMap.get(spn_remark.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setVisibility(boolean show) {
        if (show) {
            btn_sendNow.setEnabled(false);
            btn_sendNow.setAlpha(0.5f);
        } else {
            btn_sendNow.setEnabled(true);
            btn_sendNow.setAlpha(1f);
        }
    }

    private void approveAgentRequest(String remark) {

        setVisibility(true);
        rl_action_button.setVisibility(View.GONE);
        ll_processing.setVisibility(View.VISIBLE);
        String url = APIs.AGENT_REQUEST_APPROVE
                + "?" + APIs.USER_TAG + "=" + AppPreference.getInstance(context).getId()
                + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(context).getToken()
                + "&id=" + loadCashId
                + "&status_id=" + strStatusId
                + "&remark=" + strRemarkId
                + "&adminRemark=" + strRemarkId
                + "&approve_remark=" + remark;
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject object = new JSONObject(response);

                        int status = object.getInt("status");
                        String message = object.getString("message");
                        if (status == 1 || status == 2) {
                            mainDialogApprove.dismiss();
                            Dialog dialog = AppDialogs.transactionStatus(context, message, status);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {

                                if (status == 1)
                                    listener.onCallback();
                                dialog.dismiss();
                            });
                            dialog.show();
                            setVisibility(false);
                        } else if (status == 200) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status == 300) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else {
                            MakeToast.show(context, message);
                            setVisibility(false);
                        }
                        rl_action_button.setVisibility(View.VISIBLE);
                        ll_processing.setVisibility(View.GONE);
                        setVisibility(false);

                    } catch (JSONException e) {
                        rl_action_button.setVisibility(View.VISIBLE);
                        ll_processing.setVisibility(View.GONE);
                        MakeToast.show(context, "Something went wrong! try again");
                        setVisibility(false);
                    }


                },
                error -> {

                    rl_action_button.setVisibility(View.VISIBLE);
                    ll_processing.setVisibility(View.GONE);
                    MakeToast.show(context, "Something went wrong! try again");
                    setVisibility(false);
                }) {

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private OnCallbackListener listener;

    public void setupOnClickListener(OnCallbackListener listener) {
        this.listener = listener;
    }

}
