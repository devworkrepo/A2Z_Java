package com.a2zsuvidhaa.in.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.Security;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChangePinFragment extends Fragment {


    private static final String TAG = "ChangePin";
    public ChangePinFragment() {
        // Required empty public constructor
    }

    public static ChangePinFragment newInstance() {
        return new ChangePinFragment();
    }


    private EditText ed_pin;
    private EditText ed_confirmPin;
    private EditText ed_otp;
    private Button btn_submit;
    private TextView tv_incorrect;
    private TextView tv_success;
    private ProgressBar progressBar;
    private RelativeLayout rl_progress;
    private LinearLayout ll_otp;

    private String strPin;
    private String strConfirmPin;
    private String strOtp;

    private int myInt;
    double myDouble;
    long myLong;
    byte myByte;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_pin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        btn_submit.setOnClickListener(view1 -> {


            if(btn_submit.getText().toString().equalsIgnoreCase("Request for Otp")){
                if (validInput()) {

                    optRequestProcess();
                }
            }
            else if(btn_submit.getText().toString().equalsIgnoreCase("go back")){
                if(getActivity()!=null)
                    getActivity().onBackPressed();
            }
            else{

                strOtp = ed_otp.getText().toString();
                if(!strOtp.isEmpty()){
                    generateNewPin();
                }else MakeToast.show(getActivity(),"Please enter otp");
            }
        });
    }

    private void optRequestProcess() {
        setProgressVisibility(true,1);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GENERATE_NEW_PIN,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if(status.equalsIgnoreCase("1")){
                            btn_submit.setText("Generate Pin");
                            ll_otp.setVisibility(View.VISIBLE);
                            ct.start();

                            ed_pin.setEnabled(false);
                            ed_confirmPin.setEnabled(false);
                            ed_pin.setAlpha(0.5f);
                            ed_confirmPin.setAlpha(0.5f);

                            tv_success.setText(message);
                            tv_success.setVisibility(View.VISIBLE);

                        }
                        else if(status.equalsIgnoreCase("200")){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }


                    } catch (JSONException e) {
                        showErrorMessage("Something wend wrong! try again",true);

                    }
                    progressBar.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_submit.setVisibility(View.VISIBLE);

                },
                error -> {
                    setProgressVisibility(false,1);
                    showErrorMessage("Something wend wrong! try again",true);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(getActivity()).getToken()));
                return param;
            }

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void generateNewPin() {
        setProgressVisibility(false,1);
        tv_success.setVisibility(View.GONE);
        setProgressVisibility(true,1);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_GENERATED_PIN,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if(status.equalsIgnoreCase("1")){
                            btn_submit.setText("Go Back");
                            ll_otp.setVisibility(View.GONE);
                            tv_success.setText(message);
                            tv_success.setVisibility(View.VISIBLE);
                            AppPreference.getInstance(getActivity()).setChangePin(2);
                        }
                        else if(status.equalsIgnoreCase("200")){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else{
                            showErrorMessage(message,true);
                        }


                    } catch (JSONException e) {
                        showErrorMessage("Something wend wrong! try again",true);

                    }
                    progressBar.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_submit.setVisibility(View.VISIBLE);

                },
                error -> {
                    setProgressVisibility(false,1);
                    showErrorMessage("Something wend wrong! try again",true);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(getActivity()).getToken()));
                param.put("txn_pin",strPin);
                param.put("confirm_txn_pin",strConfirmPin);
                param.put("otp",strOtp);
                return param;
            }

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setProgressVisibility(boolean b, int type) {

        if(b){
            progressBar.setVisibility(View.VISIBLE);
            rl_progress.setVisibility(View.VISIBLE);
            btn_submit.setVisibility(View.GONE);
            ed_pin.setEnabled(false);
            ed_confirmPin.setEnabled(false);
            ed_pin.setAlpha(0.5f);
            ed_confirmPin.setAlpha(0.5f);
        }else{
            progressBar.setVisibility(View.GONE);
            rl_progress.setVisibility(View.GONE);
            btn_submit.setVisibility(View.VISIBLE);
            ed_pin.setEnabled(true);
            ed_confirmPin.setEnabled(true);
            ed_pin.setAlpha(1f);
            ed_confirmPin.setAlpha(1f);
        }

    }

    private boolean validInput() {
        boolean isValid = false;
        strPin = ed_pin.getText().toString();
        strConfirmPin = ed_confirmPin.getText().toString();

        if (!strPin.isEmpty()) {
            if (!strConfirmPin.isEmpty()) {
                isValid = true;
            } else MakeToast.show(getActivity(), "Please Confirm Pin!");
        } else MakeToast.show(getActivity(), "Please Enter Pin!");
        return isValid;
    }

    private void initView(View view) {
        ed_pin = view.findViewById(R.id.ed_pin);
        ed_confirmPin = view.findViewById(R.id.ed_confirmPin);
        ed_otp = view.findViewById(R.id.ed_otp);
        tv_incorrect = view.findViewById(R.id.tv_incorrect);
        tv_success = view.findViewById(R.id.tv_success);
        btn_submit = view.findViewById(R.id.btn_submit);
        progressBar = view.findViewById(R.id.progressBar);
        rl_progress = view.findViewById(R.id.rl_progress);
        ll_otp = view.findViewById(R.id.ll_otp);
        Button btn_resend=view.findViewById(R.id.btn_resend);

        ct=new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                btn_resend.setText("00:" + millisUntilFinished / 1000);

            }

            public void onFinish() {

                btn_resend.setText("Resend");
                btn_resend.setEnabled(true);
                btn_resend.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_0));
            }
        };

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_resend.setEnabled(false);
                btn_resend.setBackgroundColor(getResources().getColor(R.color.black));
                ct.start();
               // Toast.makeText(getActivity(), "enable", Toast.LENGTH_SHORT).show();
                resendOtp();

            }
        });

    }
    CountDownTimer ct;
    private void resendOtp() {

        Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(getActivity(),message);
                        /*if(status==1){
                            MakeToast.show(OTPLoginActivity.this,message);
                        }else{
                            Dialog dialog = AppDialogs.transactionStatus(OTPLoginActivity.this,message,1);
                            dialog.show();
                            Button btnOk = dialog.findViewById(R.id.btn_ok);
                            btnOk.setOnClickListener(view->dialog.dismiss());
                            dialog.setOnDismissListener(dialog1 -> {
                                Intent intent = new Intent(OTPLoginActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }*/

                    } catch (JSONException e) {
                        MakeToast.show(getActivity(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(getActivity(),"error : "+ error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobileNumber",security.encrypt(String.valueOf(AppPreference.getInstance(getActivity()).getMobile())));
                param.put("type",security.encrypt("CHANGE_PIN"));
                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", AppPreference.getInstance(getActivity()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void showErrorMessage(String message,boolean shouldShow){
        if(shouldShow){
            tv_incorrect.setText(message);
            tv_incorrect.setVisibility(View.VISIBLE);
        }
        else {
            tv_incorrect.setVisibility(View.GONE);
        }
    }

}
