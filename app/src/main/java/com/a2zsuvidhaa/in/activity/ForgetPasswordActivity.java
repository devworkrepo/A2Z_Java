package com.a2zsuvidhaa.in.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.login.LoginActivity;
import com.a2zsuvidhaa.in.util.Security;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.PasswordValidator;
import com.a2zsuvidhaa.in.util.SoftKeyboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText ed_number;
    private EditText ed_otp;
    private EditText ed_password;
    private EditText ed_conform_password;
    private Button btn_submitNumber;
    private Button btn_resetPassword;
    private ProgressBar progress_number;
    private ProgressBar progress_resetPassword;

    private TextView tv_otpMessage,tv_title;
    private TextView tv_error_hint;

    private LinearLayout ll_mobile;
    private LinearLayout ll_password;
    private RelativeLayout rl_resetPassword;

    private boolean shouldBack = true;


    private String number;
    private String otp;
    private String password;
    private String confirm_password;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        initView();
        if(getIntent().getExtras()!=null)
        {
            ed_number.setText(getIntent().getExtras().getString("mobile"));
            tv_title.setText("Reset Password");
        }
        btn_submitNumber.setOnClickListener(view -> {

            number = ed_number.getText().toString();
            if (!number.isEmpty()) {
                verifyNumber();
            } else MakeToast.show(this, "Number can't be leass than 10 digit or Empty!");
        });
        btn_resetPassword.setOnClickListener(view -> {
            if (isValidInput())
                resetPassword();
        });
        ed_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 10) {
                    SoftKeyboard.hide(ForgetPasswordActivity.this);
                    btn_submitNumber.callOnClick();
                }
            }
        });

    }

    private boolean isValidInput() {
        boolean isValid = false;

        otp = ed_otp.getText().toString();
        password = ed_password.getText().toString();
        confirm_password = ed_conform_password.getText().toString();

        if (!otp.isEmpty()) {
            if (!password.isEmpty()) {
                if(PasswordValidator.validate(password)) {
                    if (!confirm_password.isEmpty()) {
                        if (password.equals(confirm_password)) {
                            isValid = true;
                        } else MakeToast.show(this, "Password not matching!");
                    } else MakeToast.show(this, "Please confirm password!");
                }else MakeToast.show(this,"Password should have number, special character, Capital and Small alphabet and length greater than 8 digit!");
            }
            else MakeToast.show(this, "Password can not be empty!");
        } else MakeToast.show(this, "Otp can not be empty!");
        return isValid;
    }

    private void initView() {
        ImageButton imgBtn_back = findViewById(R.id.imgBtn_back);
        ed_number = findViewById(R.id.ed_number);
        btn_submitNumber = findViewById(R.id.btn_submitNumber);
        progress_number = findViewById(R.id.progress_submitNumber);
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_password = findViewById(R.id.ll_password);
        tv_otpMessage = findViewById(R.id.tv_otpMessage);
        tv_title = findViewById(R.id.tv_title);
        tv_error_hint = findViewById(R.id.tv_error_hint);
        Button btn_resend = findViewById(R.id.btn_resend);

        ed_otp = findViewById(R.id.ed_otp);
        ed_password = findViewById(R.id.ed_password);
        ed_conform_password = findViewById(R.id.ed_conform_password);

        progress_resetPassword = findViewById(R.id.progress_resetPassword);
        rl_resetPassword = findViewById(R.id.rl_resetPassword);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);


        imgBtn_back.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(ForgetPasswordActivity.this, "enable", Toast.LENGTH_SHORT).show();
                btn_resend.setBackgroundColor(getResources().getColor(R.color.black));
                ct.start();
                resendOtp();
                btn_resend.setEnabled(false);
            }
        });

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
        ct.start();

    }
    CountDownTimer ct;
    private void resendOtp() {

        Toast.makeText(ForgetPasswordActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(ForgetPasswordActivity.this,message);
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
                        MakeToast.show(ForgetPasswordActivity.this, e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(ForgetPasswordActivity.this,"error : "+ error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobileNumber",security.encrypt(number));
                param.put("type",security.encrypt("FORGET_PASSWORD"));
                param.put("userId", String.valueOf(AppPreference.getInstance(ForgetPasswordActivity.this).getId()));
                param.put("token", AppPreference.getInstance(ForgetPasswordActivity.this).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void verifyNumber() {

        mobileProgress(true);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.FORGET_PASSWORD,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            token = jsonObject.getString("token");
                            tv_otpMessage.setText(message);
                            ll_password.setVisibility(View.VISIBLE);
                            ll_mobile.setVisibility(View.GONE);
                            shouldBack = false;

                        } else {

                            tv_error_hint.setVisibility(View.VISIBLE);
                            tv_error_hint.setText(message);
                        }


                    } catch (JSONException e) {
                        e.getStackTrace();
                        tv_error_hint.setVisibility(View.VISIBLE);
                        tv_error_hint.setText("Something went wrong\nplease try letter");
                    }

                    mobileProgress(false);
                },
                error -> {

                    tv_error_hint.setVisibility(View.VISIBLE);
                    tv_error_hint.setText("Something went wrong\nplease try letter");
                    if (error instanceof NetworkError) {
                        MakeToast.show(ForgetPasswordActivity.this, "network error");
                    } else if (error instanceof ServerError) {
                        MakeToast.show(ForgetPasswordActivity.this, "Server error");
                    } else if (error instanceof AuthFailureError) {
                        MakeToast.show(ForgetPasswordActivity.this, "AuthFailure error");
                    } else if (error instanceof ParseError) {
                        MakeToast.show(ForgetPasswordActivity.this, "Parse error");
                    } else if (error instanceof TimeoutError) {
                        MakeToast.show(ForgetPasswordActivity.this, "Timeout  error");
                    }
                    mobileProgress(false);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobile", number);
                return param;
            }

        };
        RequestHandler.getInstance(ForgetPasswordActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void mobileProgress(boolean shouldVisible) {

        if (shouldVisible) {
            ed_number.setAlpha(0.4f);
            ed_number.setEnabled(false);
            btn_submitNumber.setVisibility(View.GONE);
            progress_number.setVisibility(View.VISIBLE);
            tv_error_hint.setVisibility(View.GONE);

        } else {
            ed_number.setAlpha(1f);
            ed_number.setEnabled(true);
            btn_submitNumber.setVisibility(View.VISIBLE);
            progress_number.setVisibility(View.GONE);
        }
    }
    private void resetProgress(boolean shouldVisible) {

        if (shouldVisible) {
            ed_otp.setAlpha(0.4f);
            ed_otp.setEnabled(false);
            ed_conform_password.setAlpha(0.4f);
            ed_conform_password.setEnabled(false);
            ed_password.setAlpha(0.4f);
            ed_password.setEnabled(false);
            btn_resetPassword.setVisibility(View.GONE);
            progress_resetPassword.setVisibility(View.VISIBLE);
            rl_resetPassword.setVisibility(View.VISIBLE);
            tv_error_hint.setVisibility(View.GONE);

        } else {
            ed_otp.setAlpha(1f);
            ed_otp.setEnabled(true);
            ed_conform_password.setAlpha(1f);
            ed_conform_password.setEnabled(true);
            ed_password.setAlpha(1f);
            ed_password.setEnabled(true);
            btn_resetPassword.setVisibility(View.VISIBLE);
            progress_resetPassword.setVisibility(View.GONE);
            rl_resetPassword.setVisibility(View.GONE);
            tv_error_hint.setVisibility(View.VISIBLE);
        }
    }

    private void resetPassword() {

        resetProgress(true);
        Log.e("STORE_PASSWORD","="+APIs.STORE_PASSWORD);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.STORE_PASSWORD,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("resp store","="+jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            Dialog dialog = AppDialogs.transactionStatus(ForgetPasswordActivity.this,
                                    message,1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view->{
                                dialog.dismiss();
                                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                            dialog.setOnCancelListener(dialog1 -> {
                                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });


                            dialog.show();
                        } else {

                            tv_error_hint.setVisibility(View.VISIBLE);
                            tv_error_hint.setText(message);
                        }


                    } catch (JSONException e) {
                        e.getStackTrace();
                        tv_error_hint.setVisibility(View.VISIBLE);
                        tv_error_hint.setText("Something went wrong\nplease try letter");
                    }
                    resetProgress(false);
                },
                error -> {

                    tv_error_hint.setVisibility(View.VISIBLE);
                    tv_error_hint.setText("Something went wrong\nplease try letter");
                    if (error instanceof NetworkError) {
                        MakeToast.show(ForgetPasswordActivity.this, "network error");
                    } else if (error instanceof ServerError) {
                        MakeToast.show(ForgetPasswordActivity.this, "Server error");
                    } else if (error instanceof AuthFailureError) {
                        MakeToast.show(ForgetPasswordActivity.this, "AuthFailure error");
                    } else if (error instanceof ParseError) {
                        MakeToast.show(ForgetPasswordActivity.this, "Parse error");
                    } else if (error instanceof TimeoutError) {
                        MakeToast.show(ForgetPasswordActivity.this, "Timeout  error");
                    }
                    resetProgress(false);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("otp", otp);
                param.put("token", token);
                param.put("password", password);
                param.put("password_confirmation", confirm_password);
                Log.d("password_","=="+param.toString());
                return param;
            }

        };
        RequestHandler.getInstance(ForgetPasswordActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    @Override
    public void onBackPressed() {
        if (shouldBack) super.onBackPressed();
        else {
            ll_password.setVisibility(View.GONE);
            ll_mobile.setVisibility(View.VISIBLE);
            shouldBack = true;
        }
    }
}
