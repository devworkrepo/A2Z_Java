package com.a2zsuvidhaa.in.activity;

import android.app.Dialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.NumberToWordsConverter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.adapter.FundTransferAdapter;
import com.a2zsuvidhaa.in.fragment.FundTransferFragment;
import com.a2zsuvidhaa.in.model.FundTransfer;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FundTransferActivity extends AppCompatActivity {

    private static final String TAG = "FundTransferActivity";

    private EditText ed_amount;
    private EditText ed_remark;
    private TextView tv_incorrect;
    private Button btn_transferNow;
    private FundTransfer fundTransfer;
    private String strRemark;
    private String strAmount;
    private RelativeLayout rl_progress;
    private ProgressBar progressBar;
    private ImageButton imgBtn_back;
    private int transfer_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_transfer);

        String title = "Distributor";
        fundTransfer = getIntent().getParcelableExtra("fund_transfer");
        transfer_type = getIntent().getIntExtra("transfer_type", 1);
        int AGENT_TYPE = getIntent().getIntExtra("AGENT_TYPE", 1);



        if (AGENT_TYPE == FundTransferFragment.RETAILER)
            title = "Retailer";

        if (transfer_type == FundTransferAdapter.RETURN)
            title = title + " Fund Return";
        else title = title + " Fund Transfer";

        TextView tv_shopName = findViewById(R.id.tv_shopName);
        TextView tv_availBalance = findViewById(R.id.tv_availBalance);
        TextView tv_amountInWord = findViewById(R.id.tv_amountInWord);
        TextView tv_idName = findViewById(R.id.tv_idName);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        btn_transferNow = findViewById(R.id.btn_transferNow);

        ed_amount = findViewById(R.id.ed_amount);
        ed_remark = findViewById(R.id.ed_remark);
        tv_incorrect = findViewById(R.id.tv_incorrect);
        progressBar = findViewById(R.id.progressBar);
        rl_progress = findViewById(R.id.rl_progress);


        tv_shopName.setText(fundTransfer.getShopName());
        tv_availBalance.setText(fundTransfer.getMoneyBalance());
        tv_idName.setText(fundTransfer.getId() + " - " + fundTransfer.getName());

        btn_transferNow.setOnClickListener(view1 -> {
            if (isValidInput()) {

                Dialog dialog = AppDialogs.fundTransferConfirmation(this);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(view -> dialog.dismiss());

                EditText ed_confirmAmount = dialog.findViewById(R.id.ed_confirmAmount);

                TextView tv_shopNameD = dialog.findViewById(R.id.tv_shopName);
                TextView tv_usernameD = dialog.findViewById(R.id.tv_username);
                TextView tv_availBalanceD = dialog.findViewById(R.id.tv_availBalance);
                TextView tv_transferAmountD = dialog.findViewById(R.id.tv_transferAmount);

                tv_shopNameD.setText(fundTransfer.getShopName());
                tv_usernameD.setText(fundTransfer.getId() + " - " + fundTransfer.getName());
                tv_availBalanceD.setText(fundTransfer.getMoneyBalance());
                tv_transferAmountD.setText(strAmount);

                Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
                btn_confirm.setOnClickListener(view -> {
                    if(ed_confirmAmount.getText().toString().equals(strAmount)){
                        dialog.dismiss();
                        fundTransfer();
                    }else MakeToast.show(this,"Amount does not matched");
                });
                dialog.show();

            }
        });
        imgBtn_back = findViewById(R.id.imgBtn_back);
        imgBtn_back.setOnClickListener(view -> onBackPressed());

        ed_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!ed_amount.getText().toString().isEmpty()){
                    String amountInWord = NumberToWordsConverter.convert(Integer.parseInt(ed_amount.getText().toString()));
                    tv_amountInWord.setText(amountInWord+" Rs only/-");
                }
                else {
                    tv_amountInWord.setText("Enter amount");
                }


            }
        });
    }


    private boolean isValidInput() {
        boolean isValid = false;
        strAmount = ed_amount.getText().toString();
        strRemark = ed_remark.getText().toString();

        if (!strAmount.isEmpty()) {
            if (Double.parseDouble(strAmount) >= 100) {
                if (!strRemark.isEmpty()) {
                    isValid = true;
                } else MakeToast.show(this, "Enter remark");
            } else MakeToast.show(this, "Enter minimum amount 100 rs!");

        } else MakeToast.show(this, "Enter minimum amount 100 rs!");
        return isValid;
    }


    private void fundTransfer() {

        Dialog dialog = AppDialogs.processing(this);
        dialog.show();
        showErrorMessage("", false);
        btn_transferNow.setVisibility(View.GONE);
        rl_progress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String url = APIs.FUND_TRANSFER;
        if (transfer_type == FundTransferAdapter.RETURN)
            url = APIs.FURN_RETURN;

        final StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            ed_amount.setText("");
                            ed_remark.setText("");

                            Dialog dialog1 = AppDialogs.transactionStatus(this, message, 1);
                            Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog1.dismiss();
                            });
                            dialog1.show();

                        } else if (status.equalsIgnoreCase("200")) {

                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            showErrorMessage(message, true);
                        }

                    } catch (JSONException e) {
                        dialog.dismiss();
                        btn_transferNow.setVisibility(View.VISIBLE);
                        rl_progress.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        showErrorMessage("Something went wrong! please check report and try again.", true);

                    }
                    dialog.dismiss();
                    btn_transferNow.setVisibility(View.VISIBLE);
                    rl_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                },
                error -> {
                    dialog.dismiss();
                    btn_transferNow.setVisibility(View.VISIBLE);
                    rl_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    showErrorMessage("Something went wrong! please check report and try again.", true);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(FundTransferActivity.this).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(FundTransferActivity.this).getToken()));
                param.put("agentId", fundTransfer.getId());
                param.put("amount", strAmount);
                param.put("remark", strRemark);
                return param;
            }

        };
        RequestHandler.getInstance(FundTransferActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void showErrorMessage(String message, boolean b) {
        if (b) {
            tv_incorrect.setText(message);
            tv_incorrect.setVisibility(View.VISIBLE);
        } else {
            tv_incorrect.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        AutoLogoutManager.cancelTimer();
        if (AutoLogoutManager.isSessionTimeout) {
            AutoLogoutManager.logout(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AutoLogoutManager.isAppInBackground(this)) {
            AutoLogoutManager.startUserSession();
        }
    }

}
