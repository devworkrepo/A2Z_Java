package com.a2zsuvidhaa.in.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.CreateRetailerActivity;
import com.a2zsuvidhaa.in.activity.kyc.Kyc;
import com.a2zsuvidhaa.in.activity.login.LoginActivity;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.Security;
import com.a2zsuvidhaa.in.util.dialogs.StatusDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements TextWatcher {

    ImageView mobile_verified_image,email_verified_image,close_new_email;
    ProgressBar progressState;
    AppPreference appPreference;
    EditText editText_one;
    EditText editText_two;
    private String otp;
    Button btn_submit;
    Button btn_resend;
    EditText editText_three;
    EditText editText_four,new_email_edit_txt;
    EditText editText_five;
    EditText editText_six;
    ImageView close;
    ImageView check;
    Dialog dialog_email_change;
    LinearLayout ll_options,ll_kycNotify,layout_otp;
    RelativeLayout rr;
    TextView tv_email,upgradePanCard,notupgradePanCard,upgradAadharCard;
    Button button_verify;

    View view;
    TextView extra,tv_email_change,message,tv_mobile_verify,tv_email_verify,tv_address,textKycDesc,textKycHeading,tvb_upgradeNow,tv_shopAddress,tv_shopName;
    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView iv_user_profile = view.findViewById(R.id.iv_user_profile);
        extra = view.findViewById(R.id.extra);
        tv_email_change = view.findViewById(R.id.tv_email_change);
        progressState = view.findViewById(R.id.progressState);
        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_balance = view.findViewById(R.id.tv_balance);
        tv_email = view.findViewById(R.id.tv_email);
        TextView tv_mobile = view.findViewById(R.id.tv_mobile);
        TextView tv_roll = view.findViewById(R.id.tv_roll);
        rr = view.findViewById(R.id.rr);
        layout_otp = view.findViewById(R.id.layout_otp);
        ll_options =view.findViewById(R.id.ll_options);
        upgradePanCard = view.findViewById(R.id.upgradePanCard);
        notupgradePanCard = view.findViewById(R.id.notupgradePanCard);
        upgradAadharCard = view.findViewById(R.id.upgradAadharCard);

        tv_mobile_verify = view.findViewById(R.id.tv_mobile_verify);
        tv_email_verify = view.findViewById(R.id.tv_email_verify);
         tv_address = view.findViewById(R.id.tv_address);
         tv_shopName = view.findViewById(R.id.tv_shopName);
        email_verified_image = view.findViewById(R.id.email_verified_image);
        mobile_verified_image = view.findViewById(R.id.mobile_verified_image);
        tvb_upgradeNow = view.findViewById(R.id.tvb_upgradeNow);
         tv_shopAddress = view.findViewById(R.id.tv_shopAddress);
        check = view.findViewById(R.id.check);
        textKycDesc = view.findViewById(R.id.textKycDesc);
        textKycHeading = view.findViewById(R.id.textKycHeading);
        TextView tv_joiningDate = view.findViewById(R.id.tv_joiningDate);
        TextView tv_lastUpdate = view.findViewById(R.id.tv_lastUpdate);
        TextView tv_outletName = view.findViewById(R.id.tv_outletName);
        ll_kycNotify =view.findViewById(R.id.ll_kycNotify);
        appPreference = new AppPreference(view.getContext());
        if (appPreference.getEmailVerified()==1){
            extra.setVisibility(View.GONE);
            email_verified_image.setVisibility(View.VISIBLE);
            ll_options.setVisibility(View.GONE);
        }else {
            extra.setVisibility(View.VISIBLE);
            email_verified_image.setVisibility(View.GONE);
            ll_options.setVisibility(View.VISIBLE);
        }
        if (appPreference.getMobileVerified()==1){
            mobile_verified_image.setVisibility(View.VISIBLE);
            tv_mobile_verify.setVisibility(View.GONE);
        }else {
            mobile_verified_image.setVisibility(View.GONE);
            tv_mobile_verify.setVisibility(View.VISIBLE);
        }


        dialog_email_change = new Dialog(view.getContext());
        dialog_email_change.setContentView(R.layout.new_email_dialogue);
        Window window = dialog_email_change.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button_verify = dialog_email_change.findViewById(R.id.button_verify);
        new_email_edit_txt = dialog_email_change.findViewById(R.id.new_email_edit_txt);
        close_new_email = dialog_email_change.findViewById(R.id.close_new_email);
        close_new_email.setOnClickListener(v -> {
            dialog_email_change.cancel();
        });


        tv_email_change.setOnClickListener(v -> {
            dialog_email_change.show();
        });
        button_verify.setOnClickListener(v -> {
            if (!new_email_edit_txt.getText().toString().equals("")){
                dialog_email_change.cancel();
                changeEmail(new_email_edit_txt.getText().toString());
            }
        });

        tv_email_verify.setOnClickListener(v -> {
            sendOtpEmail();
            Log.d("TAG", "onCreate: click");
            Dialog dialog;
            dialog = new Dialog(view.getContext());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.otp_verify_dialog);
            btn_resend = dialog.findViewById(R.id.btn_resend);
            btn_submit = dialog.findViewById(R.id.btn_submit);
            editText_one = dialog.findViewById(R.id.editTextone);
            editText_two = dialog.findViewById(R.id.editTexttwo);
            editText_three = dialog.findViewById(R.id.editTextthree);
            editText_four = dialog.findViewById(R.id.editTextfour);
            editText_five = dialog.findViewById(R.id.editTextFive);
            editText_six = dialog.findViewById(R.id.editTextSix);
            message = dialog.findViewById(R.id.message);
            editText_one.addTextChangedListener(this);
            editText_two.addTextChangedListener(this);
            editText_three.addTextChangedListener(this);
            editText_four.addTextChangedListener(this);
            editText_five.addTextChangedListener(this);
            editText_six.addTextChangedListener(this);
            dialog.show();
            Window window2 = dialog.getWindow();
            window2.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            CountDownTimer ct;
            ct=new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    btn_resend.setText("00:" + millisUntilFinished / 1000);

                }

                public void onFinish() {

                    btn_resend.setText("Resend");
                    btn_resend.setEnabled(true);
                    btn_resend.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_0));
                }
            }.start();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ct.cancel();
                }
            });
            close = dialog.findViewById(R.id.close);
            close.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            btn_submit.setOnClickListener(v1 -> {
                String one = editText_one.getText().toString();
                String two = editText_two.getText().toString();
                String three = editText_three.getText().toString();
                String four = editText_four.getText().toString();
                String five = editText_five.getText().toString();
                String six = editText_six.getText().toString();

                if(!one.isEmpty()
                        && !two.isEmpty() && !three.isEmpty()
                        && !four.isEmpty() &&!five.isEmpty()
                        &&!six.isEmpty()){

                    String otp = one + two + three + four + five + six;
                    validateOtpEmail(otp);
                }else MakeToast.show(view.getContext(),"Otp can't be empty!");
            });
           /* layout_otp.setVisibility(View.VISIBLE);
            rr.setBackground(new ColorDrawable(getResources().getColor(R.color.grey)));*/
        });
        tv_mobile_verify.setOnClickListener(v -> {
            sendOtpMobile();
            Log.d("TAG", "onCreate: click");
            Dialog dialog;
            dialog = new Dialog(view.getContext());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.otp_verify_dialog);
            btn_resend = dialog.findViewById(R.id.btn_resend);
            btn_submit = dialog.findViewById(R.id.btn_submit);
            editText_one = dialog.findViewById(R.id.editTextone);
            editText_two = dialog.findViewById(R.id.editTexttwo);
            editText_three = dialog.findViewById(R.id.editTextthree);
            editText_four = dialog.findViewById(R.id.editTextfour);
            editText_five = dialog.findViewById(R.id.editTextFive);
            editText_six = dialog.findViewById(R.id.editTextSix);
            message = dialog.findViewById(R.id.message);
            editText_one.addTextChangedListener(this);
            editText_two.addTextChangedListener(this);
            editText_three.addTextChangedListener(this);
            editText_four.addTextChangedListener(this);
            editText_five.addTextChangedListener(this);
            editText_six.addTextChangedListener(this);
            dialog.show();
            Window window1 = dialog.getWindow();
            window1.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            CountDownTimer ct;
            ct=new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    btn_resend.setText("00:" + millisUntilFinished / 1000);

                }

                public void onFinish() {

                    btn_resend.setText("Resend");
                    btn_resend.setEnabled(true);
                    btn_resend.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_0));
                }
            }.start();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ct.cancel();
                }
            });
            close = dialog.findViewById(R.id.close);
            close.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            btn_submit.setOnClickListener(v1 -> {
                String one = editText_one.getText().toString();
                String two = editText_two.getText().toString();
                String three = editText_three.getText().toString();
                String four = editText_four.getText().toString();
                String five = editText_five.getText().toString();
                String six = editText_six.getText().toString();

                if(!one.isEmpty()
                        && !two.isEmpty() && !three.isEmpty()
                        && !four.isEmpty() &&!five.isEmpty()
                        &&!six.isEmpty()){

                    String otp = one + two + three + four + five + six;
                    validateOtpMobile(otp);
                }else MakeToast.show(view.getContext(),"Otp can't be empty!");
            });
           /* layout_otp.setVisibility(View.VISIBLE);
            rr.setBackground(new ColorDrawable(getResources().getColor(R.color.grey)));*/
        });

        TextView edit_tv = view.findViewById(R.id.edit_tv);
        tvb_upgradeNow.setOnClickListener(v -> {
            if (appPreference.getUserKYC()==0){
                Intent intent = new Intent(getActivity(), Kyc.class);
                startActivity(intent);
            }else if(appPreference.getUserKYC()==1){
                panVerify();
            }

        });
        edit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateRetailerActivity.class);
                startActivity(intent);
            }
        });


         appPreference = AppPreference.getInstance(getActivity());
         if (appPreference.getUserKYC()==1){
             upgradAadharCard.setVisibility(View.VISIBLE);
             notupgradePanCard.setVisibility(View.VISIBLE);
             upgradePanCard.setVisibility(View.GONE);
         }
         else if(appPreference.getUserKYC()==2){
            textKycHeading.setText("KYC Completed");
            textKycDesc.setText("Your wallet limit is upgraded.");
            check.setVisibility(View.VISIBLE);
            tvb_upgradeNow.setVisibility(View.GONE);
             upgradAadharCard.setVisibility(View.VISIBLE);
             upgradePanCard.setVisibility(View.VISIBLE);
             notupgradePanCard.setVisibility(View.GONE);
        }
        else{
             upgradAadharCard.setVisibility(View.GONE);
             upgradePanCard.setVisibility(View.GONE);
             notupgradePanCard.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            tvb_upgradeNow.setVisibility(View.VISIBLE);
        }
        Security security = new Security(APIs.ENCRYPTED_KEY);
//        Log.e("state id","="+sharedPref.getSTATEID()+"--");
        tv_balance.setText(appPreference.getUserBalance());
        tv_email.setText(security.decrypt(appPreference.getEmail()));
        tv_mobile.setText(security.decrypt(appPreference.getMobile()));
        if(appPreference.getRollId()==5)
            tv_roll.setText("Retailer");
        else  if(appPreference.getRollId()==4)
            tv_roll.setText("Distributor");
        else  if(appPreference.getRollId()==3)
            tv_roll.setText("MD");
        else  if(appPreference.getRollId()==22)
            tv_roll.setText("RM");
        else  if(appPreference.getRollId()==23)
            tv_roll.setText("ASM");
        else  if(appPreference.getRollId()==24)
            tv_roll.setText("FOS");
        tv_address.setText(appPreference.getAddress());
        tv_shopName.setText(appPreference.getShopName());
        tv_shopAddress.setText(appPreference.getShopAdress());
        tv_joiningDate.setText(appPreference.getJoiningDate());
        tv_lastUpdate.setText(appPreference.getLastUpdate());
        tv_outletName.setText("( "+ appPreference.getShopName()+" )");

        Picasso.get()
                .load(AppPreference.getInstance(getActivity()).getProfilePic())
                .resize(50, 50)
                .centerCrop()
                .placeholder(R.drawable.icon_no_image)
                .error(R.drawable.icon_no_image)
                .into(iv_user_profile);
        tv_name.setText(AppPreference.getInstance(getActivity()).getName());

    }


    private void resendOtp() {

        Toast.makeText(view.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(view.getContext(),message);
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
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("type",security.encrypt("NEW_DEVICE_OTP"));
                param.put("userId", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                param.put("token", AppPreference.getInstance(view.getContext()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void sendOtpMobile() {

        Toast.makeText(view.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.VERIFY_MOBILE_EMAIL,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(view.getContext(),message);
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
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user-id", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                param.put("token", AppPreference.getInstance(view.getContext()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("type","MOBILE");
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void sendOtpEmail() {

        Toast.makeText(view.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.VERIFY_MOBILE_EMAIL,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(view.getContext(),message);
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
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user-id", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                param.put("token", AppPreference.getInstance(view.getContext()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("type","EMAIL");
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void validateOtpMobile(String otp) {

        Toast.makeText(view.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.VALIDATE_MOBILE_EMAIL,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(view.getContext(),message);
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
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user-id", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                param.put("token", AppPreference.getInstance(view.getContext()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("type","MOBILE");
                param.put("otp",otp);
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void changeEmail(String email) {

        Toast.makeText(view.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.VERIFY_MOBILE_EMAIL,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(view.getContext(),message);
                        if (status ==1){
                            Dialog dialog;
                            dialog = new Dialog(view.getContext());
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setContentView(R.layout.otp_verify_dialog);
                            btn_resend = dialog.findViewById(R.id.btn_resend);
                            btn_submit = dialog.findViewById(R.id.btn_submit);
                            editText_one = dialog.findViewById(R.id.editTextone);
                            editText_two = dialog.findViewById(R.id.editTexttwo);
                            editText_three = dialog.findViewById(R.id.editTextthree);
                            editText_four = dialog.findViewById(R.id.editTextfour);
                            editText_five = dialog.findViewById(R.id.editTextFive);
                            editText_six = dialog.findViewById(R.id.editTextSix);
                            editText_one.addTextChangedListener(this);
                            editText_two.addTextChangedListener(this);
                            editText_three.addTextChangedListener(this);
                            editText_four.addTextChangedListener(this);
                            editText_five.addTextChangedListener(this);
                            editText_six.addTextChangedListener(this);
                            dialog.show();
                            Window window2 = dialog.getWindow();
                            window2.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            CountDownTimer ct;
                            ct=new CountDownTimer(60000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    btn_resend.setText("00:" + millisUntilFinished / 1000);

                                }

                                public void onFinish() {

                                    btn_resend.setText("Resend");
                                    btn_resend.setEnabled(true);
                                    btn_resend.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_0));
                                }
                            }.start();
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ct.cancel();
                                }
                            });
                            close = dialog.findViewById(R.id.close);
                            close.setOnClickListener(v1 -> {
                                dialog.dismiss();
                            });
                            btn_submit.setOnClickListener(v1 -> {
                                String one = editText_one.getText().toString();
                                String two = editText_two.getText().toString();
                                String three = editText_three.getText().toString();
                                String four = editText_four.getText().toString();
                                String five = editText_five.getText().toString();
                                String six = editText_six.getText().toString();

                                if(!one.isEmpty()
                                        && !two.isEmpty() && !three.isEmpty()
                                        && !four.isEmpty() &&!five.isEmpty()
                                        &&!six.isEmpty()){

                                    String otp = one + two + three + four + five + six;
                                    dialog.cancel();
                                    validateNewEmail(email,otp);
                                }else MakeToast.show(view.getContext(),"Otp can't be empty!");
                            });
                        }
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
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user-id", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                param.put("token", AppPreference.getInstance(view.getContext()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("type","CHANGE");
                param.put("new_email",email);
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void validateNewEmail(String email,String otp) {
        Toast.makeText(view.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.VALIDATE_MOBILE_EMAIL,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(view.getContext(),message);
                        if (status ==1){
                            tv_email.setText(email);
                            appPreference.setEmail(email);

                        }
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
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user-id", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                param.put("token", AppPreference.getInstance(view.getContext()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("type","CHANGE");
                param.put("new_email",email);
                param.put("otp",otp);
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void validateOtpEmail(String otp) {

        Toast.makeText(view.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.VALIDATE_MOBILE_EMAIL,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(view.getContext(),message);
                        if (status ==1){
                            appPreference.setEmailVerified(1);
                            email_verified_image.setVisibility(View.VISIBLE);
                            ll_options.setVisibility(View.GONE);
                        }
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
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user-id", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                param.put("token", AppPreference.getInstance(view.getContext()).getToken());
                Log.d("resend","=="+param.toString());
                return param;
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("type","EMAIL");
                param.put("otp",otp);
                Log.d("resend","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (appPreference.getUserKYC()==1){
            upgradAadharCard.setVisibility(View.VISIBLE);
            notupgradePanCard.setVisibility(View.VISIBLE);
            upgradePanCard.setVisibility(View.GONE);
        }
        else if(appPreference.getUserKYC()==2){
            textKycHeading.setText("KYC Completed");
            textKycDesc.setText("Your wallet limit is upgraded.");
            check.setVisibility(View.VISIBLE);
            tvb_upgradeNow.setVisibility(View.GONE);
            upgradAadharCard.setVisibility(View.VISIBLE);
            upgradePanCard.setVisibility(View.VISIBLE);
            notupgradePanCard.setVisibility(View.GONE);
        }
        else{
            upgradAadharCard.setVisibility(View.GONE);
            upgradePanCard.setVisibility(View.GONE);
            notupgradePanCard.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            tvb_upgradeNow.setVisibility(View.VISIBLE);
        }
            tv_address.setText(appPreference.getAddress());
            tv_shopName.setText(appPreference.getShopName());
            tv_shopAddress.setText(appPreference.getShopAdress());


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 1) {
            if (editText_one.length() == 1) {
                editText_two.requestFocus();
            }

            if (editText_two.length() == 1) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 1) {
                editText_four.requestFocus();
            }
            if (editText_four.length() == 1) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 1) {
                editText_six.requestFocus();
            }
        } else if (editable.length() == 0) {
            if (editText_six.length() == 0) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 0) {
                editText_four.requestFocus();
            }
            if (editText_four.length() == 0) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 0) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 0) {
                editText_one.requestFocus();
            }
        }

    }

    private void isFieldValid() {

        String one = editText_one.getText().toString();
        String two = editText_two.getText().toString();
        String three = editText_three.getText().toString();
        String four = editText_four.getText().toString();
        String five = editText_five.getText().toString();
        String six = editText_six.getText().toString();

        if(!one.isEmpty()
                || !two.isEmpty() || !three.isEmpty()
                || !four.isEmpty() ||!five.isEmpty()
                ||!six.isEmpty()){

            otp = one + two + three + four + five + six;
            verifyOtpToServer();
        }else MakeToast.show(view.getContext(),"Otp can't be empty!");
    }
    private void verifyOtpToServer() {

        Dialog progressDialog = StatusDialog.INSTANCE.progress(view.getContext(),"Verifying...");
        progressDialog.show();
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.VERIFY_DEVICE,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if(status!=701){
                            MakeToast.show(view.getContext(),message);
                        }else{
                            Dialog dialog = AppDialogs.transactionStatus(view.getContext(),message,1);
                            dialog.show();
                            Button btnOk = dialog.findViewById(R.id.btn_ok);
                            btnOk.setOnClickListener(view->dialog.dismiss());
                            dialog.setOnDismissListener(dialog1 -> {
                                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                                startActivity(intent);
                            });
                        }

                    } catch (JSONException e) {
                        MakeToast.show(view.getContext(), e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    MakeToast.show(view.getContext(),"error : "+ error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("otp",security.encrypt(otp));

                Log.d("Verify","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void panVerify()
    {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.pancard_dialgoue);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        EditText edit_PanNumber = dialog.findViewById(R.id.edit_PanNumber);
        TextView editName_approve = dialog.findViewById(R.id.editName_approve);
        dialog.show();
        editName_approve.setOnClickListener(v -> {
            if (!edit_PanNumber.getText().toString().equals("")){
                progressState.setVisibility(View.VISIBLE);
                final StringRequest request = new StringRequest(Request.Method.POST, APIs.PAN_VERIFY,
                        response -> {
                            try {
                                progressState.setVisibility(View.GONE);
                                Log.e("pan verify","="+response);
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                if (status.equalsIgnoreCase("1")) {

                                    String panName = jsonObject.getString("name");
                                    appPreference.setUserKYC(2);
                                    if (appPreference.getUserKYC()==1){
                                        upgradAadharCard.setVisibility(View.VISIBLE);
                                        notupgradePanCard.setVisibility(View.VISIBLE);
                                        upgradePanCard.setVisibility(View.GONE);
                                    }
                                    else if(appPreference.getUserKYC()==2){
                                        textKycHeading.setText("KYC Completed");
                                        textKycDesc.setText("Your wallet limit is upgraded.");
                                        check.setVisibility(View.VISIBLE);
                                        tvb_upgradeNow.setVisibility(View.GONE);
                                        upgradAadharCard.setVisibility(View.VISIBLE);
                                        upgradePanCard.setVisibility(View.VISIBLE);
                                        notupgradePanCard.setVisibility(View.GONE);
                                    }
                                    else{
                                        upgradAadharCard.setVisibility(View.GONE);
                                        upgradePanCard.setVisibility(View.GONE);
                                        notupgradePanCard.setVisibility(View.GONE);
                                        check.setVisibility(View.GONE);
                                        tvb_upgradeNow.setVisibility(View.VISIBLE);
                                    }
                                    Dialog dialog1 = AppDialogs.transactionStatus(
                                            view.getContext(), "Verification Successfull.\n"+panName, 1);
                                    dialog1.show();
                                    Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                                    btn_ok.setOnClickListener(view -> dialog1.dismiss());

                                } else if (status.equalsIgnoreCase("200")) {
                                    Intent intent = new Intent(view.getContext(), AppInProgressActivity.class);
                                    intent.putExtra("message", message);
                                    intent.putExtra("type", 0);
                                    startActivity(intent);
                                } else if (status.equalsIgnoreCase("300")) {

                                    Intent intent = new Intent(view.getContext(), AppInProgressActivity.class);
                                    intent.putExtra("message", message);
                                    intent.putExtra("type", 1);
                                    startActivity(intent);
                                } else {
                                    Dialog dialog1 = AppDialogs.transactionStatus(
                                            view.getContext(), message, 2);
                                    dialog1.show();
                                    Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                                    btn_ok.setOnClickListener(view -> dialog1.dismiss());
                                }
                                dialog.cancel();
                            } catch (Exception e) {
                                progressState.setVisibility(View.GONE);
                                dialog.cancel();
                            }
                        },
                        error -> {
                            dialog.cancel();
                            progressState.setVisibility(View.GONE);

                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> param = new HashMap<>();

                        param.put("user-id", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                        param.put("token", String.valueOf(AppPreference.getInstance(view.getContext()).getToken()));



                        return param;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> param = new HashMap<>();

                        param.put("userId", String.valueOf(AppPreference.getInstance(view.getContext()).getId()));
                        param.put("token", String.valueOf(AppPreference.getInstance(view.getContext()).getToken()));

                        param.put("pan_number", edit_PanNumber.getText().toString());


                        return param;
                    }

                };
                RequestHandler.getInstance(view.getContext()).addToRequestQueue(request);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(20),
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }

        });

    }
}
