package com.a2zsuvidhaa.in.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.util.dialogs.Dialogs;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PurchaseBalanceFragment extends Fragment {


    private static final String TAG = "ChangePin";

    public PurchaseBalanceFragment() {
    }

    public static PurchaseBalanceFragment newInstance() {
        return new PurchaseBalanceFragment();
    }


    private EditText ed_amount;
    private Button btn_submit;
    private TextView tv_incorrect;
    private RelativeLayout rl_progress;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        btn_submit.setOnClickListener(view1 -> {
            String amount = ed_amount.getText().toString();
            if (!amount.isEmpty()) {

               Dialog confirmDialog =  Dialogs.INSTANCE.commonConfirmDialog(requireActivity(),
                       "Are you sure to purchase balance of Rs." + amount);

               Button btnConfirm = confirmDialog.findViewById(R.id.btn_confirm);
               btnConfirm.setOnClickListener(v -> {
                    confirmDialog.dismiss();
                    purchaseAmount(amount);

                });

            } else MakeToast.show(getActivity(), "Please enter amount!");


        });
    }

    private void purchaseAmount(String amount) {
        setProgressVisibility(true);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.PURCHASE_BALANCE,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            ed_amount.setText("");

                            Dialog dialog = AppDialogs.transactionStatus(getActivity(), message, 1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());

                            dialog.show();


                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        }

                        MakeToast.show(getActivity(), message);


                    } catch (JSONException e) {
                        showErrorMessage();

                    }
                    progressBar.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_submit.setVisibility(View.VISIBLE);

                },
                error -> {
                    setProgressVisibility(false);
                    showErrorMessage();
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(getActivity()).getToken()));
                param.put("balance", amount);

                return param;
            }

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setProgressVisibility(boolean b) {

        if (b) {
            progressBar.setVisibility(View.VISIBLE);
            rl_progress.setVisibility(View.VISIBLE);
            btn_submit.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rl_progress.setVisibility(View.GONE);
            btn_submit.setVisibility(View.VISIBLE);
        }

    }


    private void initView(View view) {

        ed_amount = view.findViewById(R.id.ed_amount);
        btn_submit = view.findViewById(R.id.btn_submit);
        tv_incorrect = view.findViewById(R.id.tv_incorrect);
        rl_progress = view.findViewById(R.id.rl_progress);
        progressBar = view.findViewById(R.id.progressBar);

    }

    private void showErrorMessage() {
        tv_incorrect.setText("Something wend wrong! try again");
        tv_incorrect.setVisibility(View.VISIBLE);
    }

}
