package com.a2zsuvidhaa.in.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.ForgetPasswordActivity;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.InternetConnection;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.Security;
import com.a2zsuvidhaa.in.util.dialogs.StatusDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    CheckBox cb_tc;
    TextView tv_tc;

    private EditText ed_mobile,ed_name,ed_email,ed_password;
    private Spinner spn_state;
    private TextView tv_incorrect;
    Button btn_signup;
    private HashMap<String, String> stateHashmap = new HashMap<>();
    private String sState;
    private RelativeLayout rl_progress;
    private ProgressBar progressBar;
    String otp_txt="";

    AppPreference appPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        appPreference =  AppPreference.getInstance(SignupActivity.this);

        initView();
        getStates();


    }
    private boolean isPasswordHide = true;
    private void initView() {
        ed_mobile = findViewById(R.id.ed_mobile);
        ed_password = findViewById(R.id.ed_password);
        ed_name = findViewById(R.id.ed_name);
        ed_email = findViewById(R.id.ed_email);
        spn_state = findViewById(R.id.spn_state);
        btn_signup = findViewById(R.id.btn_signup);
        tv_incorrect = findViewById(R.id.tv_incorrect);
        rl_progress = findViewById(R.id.rl_progress);
        progressBar = findViewById(R.id.progressBar);
        cb_tc=findViewById(R.id.cb_tc);
        tv_tc=findViewById(R.id.tv_tc);
        ImageButton btn_showHidePassword = findViewById(R.id.btn_showHidePassword);

        btn_showHidePassword.setOnClickListener(view -> {
            if (isPasswordHide) {
                ed_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btn_showHidePassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_hide_password));
                isPasswordHide = false;
            } else {
                ed_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btn_showHidePassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_password));
                isPasswordHide = true;
            }
        });

        tv_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDialogs.showMessageDialogtc(SignupActivity.this);
            }
        });


        btn_signup.setOnClickListener(view -> {

        if(cb_tc.isChecked()) {

            if (isValid()) {
                if (InternetConnection.isConnected(SignupActivity.this)) {
                    resendOtp();

                }
                else MakeToast.show(SignupActivity.this, "No Internet Connection Available!");
            }
            else MakeToast.show(SignupActivity.this, "Not valid");

        }
        else
            MakeToast.show(SignupActivity.this, "Please Agree with Terms and Conditions.");
        });
    }

    private boolean isValid() {
        Log.e("valid","enter"+ed_password.getText().toString());
        boolean isValid = false;

        if (!ed_name.getText().toString().isEmpty()) {
            if (!ed_mobile.getText().toString().isEmpty()) {
                if (ed_mobile.getText().toString().length() >= 10) {
                    if (!ed_email.getText().toString().isEmpty()) {
                        if(isValidPassword(ed_password.getText().toString()))
                        {
                            isValid = true;
                        }
                        else MakeToast.show(this, "Please Enter Valid Password .");
                    } else MakeToast.show(this, "Email can't be empty");

                } else MakeToast.show(this, "Mobile Number can't be less than 10 digit");
            } else MakeToast.show(this, "Mobile Number can't be empty");

        } else MakeToast.show(this, "Name can't be empty");
        MakeToast.show(SignupActivity.this, "valid");
        return isValid;
    }
    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
    private void signup() {

        rl_progress.setVisibility(View.VISIBLE);
        btn_signup.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tv_incorrect.setVisibility(View.GONE);
        //   editTextUnFoucus();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.SIGNIN_URL,
                response -> {
                    try {


                        JSONObject object = new JSONObject(response);
                        Log.e("login resp","="+object.toString());

                        int status = object.getInt("status");
                        if (status == 1) {

                           // AppDialogs.dialogMessage(SignupActivity.this,""+object.getString("message"),1);
                            showDialogResponse(""+object.getString("message"));
                        } else if (status == 0) {
                            rl_progress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            btn_signup.setVisibility(View.VISIBLE);
                            tv_incorrect.setText(object.toString());
                            tv_incorrect.setVisibility(View.VISIBLE);

                        } else if (status == 3) {
                            MakeToast.show(SignupActivity.this, object.getString("message"));
                            Intent intent = new Intent(SignupActivity.this, DeviceVerificationActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (status == 4) {
                            MakeToast.show(this, "4");
                        }
                        else if (status == 18) {
                            rl_progress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            btn_signup.setVisibility(View.VISIBLE);
                            tv_incorrect.setText(object.getString("message"));
                            tv_incorrect.setVisibility(View.VISIBLE);

                        }
                        else if (status == 108) {
                            rl_progress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            btn_signup.setVisibility(View.VISIBLE);
                            String msg=object.getString("message");
                            String strMobile=object.getString("mobile");
                            Dialog dialog1 = AppDialogs.transactionStatus(SignupActivity.this, msg, 1);
                            Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {

                                dialog1.dismiss();
                                Intent intent = new Intent(this, ForgetPasswordActivity.class);
                                intent.putExtra("mobile",strMobile);
                                startActivity(intent);
                            });
                            dialog1.show();
                        }




                        rl_progress.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        btn_signup.setVisibility(View.VISIBLE);
                        //editTextFoucus();

                    } catch (JSONException e) {
                        //editTextFoucus();
                        rl_progress.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        btn_signup.setVisibility(View.VISIBLE);
                        tv_incorrect.setVisibility(View.VISIBLE);
                        tv_incorrect.setText(e.getMessage());
                        MakeToast.show(SignupActivity.this, e.getMessage());

                    }
                },
                error -> {
                    rl_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    btn_signup.setVisibility(View.VISIBLE);
                    tv_incorrect.setText("Login failed -> Something went wrong");
                    tv_incorrect.setVisibility(View.VISIBLE);
                    //editTextFoucus();
                    MakeToast.show(SignupActivity.this, "on error");
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        MakeToast.show(this,"no internet connection");
                    } else if (error instanceof AuthFailureError) {
                        MakeToast.show(this,"AuthFailureError");
                    } else if (error instanceof ServerError) {
                        MakeToast.show(this,"ServerError");
                    } else if (error instanceof NetworkError) {
                        MakeToast.show(this,"NetworkError");
                    } else if (error instanceof ParseError) {
                        MakeToast.show(this,"ParseError");
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobile", ed_mobile.getText().toString());
                param.put("password", ed_password.getText().toString());
                param.put("name", ed_name.getText().toString());
                param.put("email", ed_email.getText().toString());
                param.put("state_id",sState);
                param.put("role_id","5");
                param.put("parent_id",""+ appPreference.getId());


                Log.d("sign up","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



    private void resendOtp() {
        Log.e("otp","=response");
        rl_progress.setVisibility(View.VISIBLE);
        btn_signup.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tv_incorrect.setVisibility(View.GONE);
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP,
                response -> {
                    rl_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    btn_signup.setVisibility(View.VISIBLE);
                    try {
                        Log.e("otp","="+response);
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if(status==1) {

                            otp_txt=  jsonObject.getString("otp");
                            showDialog();
                        }
                        else
                            MakeToast.show(SignupActivity.this, message);

                    } catch (JSONException e) {
                        MakeToast.show(SignupActivity.this, e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(SignupActivity.this,"error : "+ error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobileNumber",security.encrypt(ed_mobile.getText().toString()));
                param.put("type",security.encrypt("VERIFY_REGISTER"));
               // param.put("userId", String.valueOf(SharedPref.getInstance(SignupActivity.this).getId()));
             //   param.put("token", SharedPref.getInstance(SignupActivity.this).getToken());
                Log.d("send","=="+param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    void getStates() {

        String url = APIs.GET_STATES + "?" + APIs.USER_TAG + "=" + AppPreference.getInstance(this).getId()
                + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(this).getToken();
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {


                            JSONObject states = jsonObject.getJSONObject("states");
                            setStates(states);


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
                        } else MakeToast.show(this, message);


                    } catch (JSONException e) {
                        MakeToast.show(this, "Something went wrong! try again(c)");
                    }
                },
                error -> {
                    MakeToast.show(this, "Something went wrong! try again(e)");
                    finish();
                }) {

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setStates(JSONObject states) {
        try {
            stateHashmap.clear();
            Iterator iterator = states.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                stateHashmap.put(states.getString(key), key);
            }
            String[] prepaidStrings = stateHashmap.keySet().toArray(new String[0]);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                    R.layout.spinner_layout2, prepaidStrings);
            dataAdapter.setDropDownViewResource(R.layout.spinner_layout2);
            spn_state.setAdapter(dataAdapter);

            spn_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sState = stateHashmap.get(spn_state.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Dialog dialog;
    private void showDialog()
    {
        dialog = new Dialog(SignupActivity.this);//, R.style.FullHeightDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.otp_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText otp=(EditText)dialog.findViewById(R.id.otp);

        ((TextView) dialog.findViewById(R.id.msg_diag_message)).setText("Please Enter OTP Sent To Your Mobile Number");
        ((Button) dialog.findViewById(R.id.msg_diag_yes_button)).setText("SUBMIT");
//        ((Button) dialog.findViewById(R.id.msg_diag_no_button)).setText("Cancel");

        dialog.findViewById(R.id.msg_diag_yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(otp.getText().toString().equalsIgnoreCase("") || otp.getText().toString().isEmpty() )
                {

                    return;
                }
                else if(otp.getText().toString().equalsIgnoreCase(otp_txt) )
                {
                    signup();
                    //showDialogResponse("Registeration Success.");
                }
                else
                {
                    otp.setError("Invalid OTP.");
                    return;
                }
                dialog.dismiss();

            }
        });



        dialog.show();
    }

    private void showDialogResponse(String heading)
    {

        Dialog dialog = StatusDialog.INSTANCE.success(this,"Success");

        dialog.findViewById(R.id.btn_ok).setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
            intent.putExtra("mobile",ed_mobile.getText().toString());
            intent.putExtra("password",ed_password.getText().toString());
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }
}
