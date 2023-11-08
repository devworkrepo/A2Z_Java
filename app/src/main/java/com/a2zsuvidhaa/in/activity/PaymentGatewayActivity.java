package com.a2zsuvidhaa.in.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.model.PGModel;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AppUitls;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.Security;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PaymentGatewayActivity extends AppCompatActivity {

    ArrayList<PGModel> list;
    TextView tv_error_hint;
    TextView tv_charges;

    EditText et_amount;
    EditText et_charge;
    EditText et_total,et_otp;

    Button submit;
    Spinner spn_pgmode;
    Button btn_resend;
    ProgressBar progressBar;

    String date_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);


        initview();
        getPaymentDetails();
    }

    private void initview()
    {
        progressBar=findViewById(R.id.progress);
        tv_error_hint=findViewById(R.id.tv_error_hint);
        tv_charges=findViewById(R.id.charges);

        et_amount=findViewById(R.id.ed_amount);
        et_charge=findViewById(R.id.ed_charge);
        et_total=findViewById(R.id.ed_total);
        et_otp=findViewById(R.id.ed_otp);
         btn_resend= findViewById(R.id.btn_resend);
        submit=findViewById(R.id.btn_submit);
        spn_pgmode=findViewById(R.id.spn_pgmode);

        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!et_amount.getText().toString().equalsIgnoreCase("") ) {

                    double charge = Double.parseDouble(list.get(pos).getTxn_charge());
                    double gst = Double.parseDouble(list.get(pos).getGst_rate());
                    double amount = Double.parseDouble(et_amount.getText().toString());
                    if(amount>=100) {
                        Log.e("amount", "=" + ((amount * charge) / 100) + " " + ((((amount * charge) / 100) * gst) / 100));
                        double tax = 0;
                        if (list.get(pos).getCharge_type().equalsIgnoreCase("1")) {
                            tax = ((amount * charge) / 100) + ((((amount * charge) / 100) * gst) / 100);
                            txn_gst=(double)(((amount * charge) / 100) * gst) / 100;
                        }
                        else {
                            tax = charge + ((charge * gst) / 100);
                            txn_gst=((charge * gst) / 100);
                        }
                        et_charge.setText("" +String.format("%.2f",tax));
                       // et_total.setText("" + (amount + tax));
                        submit.setEnabled(true);
                        submit.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_5));
                    }
                    else{
                        submit.setBackground(getResources().getDrawable(R.drawable.bg_grey_5));
                        et_charge.setText("");
                        submit.setEnabled(false);
                       // et_total.setText("");
                    }
                }
                else{
                    et_charge.setText("");
                    //et_total.setText("");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ct.cancel();
                if(submit.getText().toString().equalsIgnoreCase("Verify"))
                {

                    showMessageDialog();
                }
                else {
                    //initiatePGTransaction();
                    if(et_total.getText().toString().length()==10)
                    verifyNumberToServer(et_total.getText().toString());
                    else
                        showConnectionError("Please Enter Valid Mobile Number");
                }
               // makeTransaction();
            }
        });

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(PaymentGatewayActivity.this, "enable", Toast.LENGTH_SHORT).show();
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

    }
    CountDownTimer ct;
    int pos=0;
    double txn_gst;
    long  txnid;

    private void resendOtp() {

        Toast.makeText(PaymentGatewayActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(PaymentGatewayActivity.this,message);
                        ct.start();
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
                        MakeToast.show(PaymentGatewayActivity.this, e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(PaymentGatewayActivity.this,"error : "+ error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobileNumber",security.encrypt(et_total.getText().toString()));
                param.put("type",security.encrypt("VERIFY_PG"));
                param.put("userId", String.valueOf(AppPreference.getInstance(PaymentGatewayActivity.this).getId()));
                param.put("token", AppPreference.getInstance(PaymentGatewayActivity.this).getToken());
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

    
    private void verifyNumberToServer(String strMobileNumber) {

        String url = APIs.VERIFY_PG_TRANSACTION+ "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(this).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(this).getToken() + "&mobile_number=" + strMobileNumber;

        Log.e("verifyNumberToServer",""+url);
        progressBar.setVisibility(View.VISIBLE);

        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {

                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            submit.setText("Verify");
                            et_otp.setVisibility(View.VISIBLE);
                            btn_resend.setVisibility(View.VISIBLE);
                            ct.start();
                            }



                        else if(status.equalsIgnoreCase("200")){
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){

                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }
                        else showConnectionError("message : " + message);
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);

                        showConnectionError("Something went wrong!\nTry again");

                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);

                    showConnectionError("Something went wrong!\nTry again");

                }) {

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

  /*  private void initiatePGTransaction()
    {

        Intent newPayIntent=new Intent (PaymentGatewayActivity.this , PayActivity.class);
        if(list.get(pos).getMdd().equalsIgnoreCase("NB") || list.get(pos).getMdd().equalsIgnoreCase("MW")) {
            newPayIntent.putExtra("merchantId", "20246");
            newPayIntent.putExtra("loginid", "20246");
            newPayIntent.putExtra("password", "0dd04ef0");
            newPayIntent.putExtra("prodid", "B2B");
            newPayIntent.putExtra("signature_request","3f63df51fe25168570" );
            newPayIntent.putExtra("signature_response","46333356e293f7f268" );
        }
        else {
            newPayIntent.putExtra("merchantId", "21875");
            newPayIntent.putExtra("loginid", "21875");
            newPayIntent.putExtra("password", "27fe0de2");
            newPayIntent.putExtra("prodid", "B2B-II");
            newPayIntent.putExtra("signature_request","f4b555c85ef75fec48" );
            newPayIntent.putExtra("signature_response","e016b4c97d94fdd3fa" );
        }
        Security security = new Security(APIs.ENCRYPTED_KEY);
            // txnscamt Fixed. Must be 0
            newPayIntent.putExtra("txnscamt", "0");

            // txncurr Fixed. Must be �INR�
            newPayIntent.putExtra("txncurr", "INR");
            newPayIntent.putExtra("clientcode", AppUitls.encodeBase64 ("007") );
            newPayIntent.putExtra("custacc","100000036600" );
            //Only for Name
            newPayIntent.putExtra("customerName", ""+ AppPreference.getInstance(PaymentGatewayActivity.this).getName());
            //Only for Email ID
            newPayIntent.putExtra("customerEmailID", ""+security.decrypt(AppPreference.getInstance(PaymentGatewayActivity.this).getEmail()));
            //Only for Mobile Number
            newPayIntent.putExtra("customerMobileNo",""+security.decrypt(AppPreference.getInstance(PaymentGatewayActivity.this).getMobile()) );
            //Only for Address
            newPayIntent.putExtra("billingAddress", ""+ AppPreference.getInstance(PaymentGatewayActivity.this).getAddress());

            //newPayIntent.putExtra("channelid", "INT");
            // amt  Should be 2 decimal number i.e 1.00
            newPayIntent.putExtra("amt",""+Double.parseDouble(et_amount.getText().toString()));
            newPayIntent.putExtra("txnid", ""+txnid);
            //Date Should be in same format
            date_time= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            newPayIntent.putExtra("date", ""+date_time);

            newPayIntent.putExtra("discriminator",""+list.get(pos).getMdd());
            newPayIntent.putExtra("isLive",true);

            Bundle bundle = newPayIntent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    //Log.e("bundle", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                    requestPG.put(key,""+bundle.get(key));
                }
                Log.e("requestPG",requestPG.toString());
            }

            startActivityForResult(newPayIntent,1);


    }*/
    HashMap<String,String> requestPG=new HashMap<>();
  /*  private void makeTransaction() {
        progressBar.setVisibility(View.VISIBLE);

        submit.setVisibility(View.GONE);

        final StringRequest request = new StringRequest(Request.Method.POST, APIs.MAKE_PG_TRANSACTION,
                response -> {
                    try {


                        Log.e("response transfer","sd "+response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if(status.equalsIgnoreCase("1"))
                        {
                            txnid=Long.parseLong(jsonObject.getString("transaction_id"));
                            initiatePGTransaction();

                        }
                        else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(PaymentGatewayActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(PaymentGatewayActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            submit.setVisibility(View.VISIBLE);
                            ct.onFinish();
                           showConnectionError(message);
                        }


                    } catch (JSONException e) {
                        submit.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        showConnectionError("Something went wrong!\nTry again");
                        AppDialogs.volleyErrorDialog(PaymentGatewayActivity.this, 1);

                    }
                    progressBar.setVisibility(View.GONE);

                },
                error -> {
                    submit.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    showConnectionError("Something went wrong!\nTry again");
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", AppPreference.getInstance(PaymentGatewayActivity.this).getToken());
                params.put("userId", String.valueOf(AppPreference.getInstance(PaymentGatewayActivity.this).getId()));
                params.put("txn_amount", et_amount.getText().toString());
                params.put("txn_charge",""+String.format("%.2f", Double.parseDouble(et_charge.getText().toString())));
                params.put("gst", ""+String.format("%.2f",txn_gst));
                params.put("mdd", list.get(pos).getMdd());
                params.put("otp",et_otp.getText().toString());
                params.put("mobile",et_total.getText().toString());

                Log.e("params",params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(PaymentGatewayActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }*/

    private void updateTransaction(HashMap<String,String> responsePG) {
        progressBar.setVisibility(View.VISIBLE);


        final StringRequest request = new StringRequest(Request.Method.POST, APIs.UPDATE_PG_TRANSACTION,
                response -> {
                    try {


                        Log.e("UPDATE_PG_TRANSACTION","= "+response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if(status.equalsIgnoreCase("1"))
                        {
                            Dialog dialog = AppDialogs.pgStatusDialog(PaymentGatewayActivity.this);
                            Button btn_close = dialog.findViewById(R.id.btn_close);
                            TextView tv_date=dialog.findViewById(R.id.tv_date);
                            TextView tv_txnid=dialog.findViewById(R.id.tv_txnid);
                            TextView tv_pg_id=dialog.findViewById(R.id.tv_pgid);
                            TextView tv_ref=dialog.findViewById(R.id.tv_ref);
                            TextView tv_balance=dialog.findViewById(R.id.tv_balance);
                            TextView tv_msg=dialog.findViewById(R.id.msg);

                            tv_msg.setText(""+message);
                            tv_date.setText(""+date_time);
                            tv_txnid.setText(""+txnid);
                            tv_pg_id.setText(""+responsePG.get("mmp_txn"));
                            tv_ref.setText(""+responsePG.get("bank_txn"));
                            tv_balance.setText(""+jsonObject.getString("balance"));

                            btn_close.setOnClickListener(view3-> {

                                dialog.dismiss();
                                this.finish();
                            });

                            dialog.show();
                        }
                        else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(PaymentGatewayActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(PaymentGatewayActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {

                            showConnectionError(message);
                        }


                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        showConnectionError("Something went wrong!\nTry again");
                        AppDialogs.volleyErrorDialog(PaymentGatewayActivity.this, 1);

                    }
                    progressBar.setVisibility(View.GONE);

                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    showConnectionError("Something went wrong!\nTry again");
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params=responsePG;

                responsePG.put("resp",""+params.toString());
                responsePG.put("req",""+requestPG.toString());
                responsePG.put("token", AppPreference.getInstance(PaymentGatewayActivity.this).getToken());
                responsePG.put("userId", String.valueOf(AppPreference.getInstance(PaymentGatewayActivity.this).getId()));
                responsePG.put("report_id", ""+txnid);
                //responsePG.put("response_pg",responsePG.toString());

                Log.e("params",responsePG.toString());
                return responsePG;
            }
        };
        RequestHandler.getInstance(PaymentGatewayActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void getPaymentDetails() {

        progressBar.setVisibility(View.VISIBLE);
        String url = APIs.GET_PAYMENT_DETAILS + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(PaymentGatewayActivity.this).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(PaymentGatewayActivity.this).getToken() ;
        Log.e("getPaymentDetails",""+url);
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {

                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {


                            JSONArray jsonArray = jsonObject.getJSONArray("details");
                            listDetails(jsonArray);

                        }
                        else if(status.equalsIgnoreCase("200")){
                            Intent intent = new Intent(PaymentGatewayActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){
                            Intent intent = new Intent(PaymentGatewayActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }
                        else showConnectionError(message);
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        showConnectionError("Something went wrong!\nTry again");

                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    showConnectionError("Something went wrong!\nTry again");

                }) {

        };
        RequestHandler.getInstance(PaymentGatewayActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void listDetails(JSONArray jsonArray) {


        list = new ArrayList<>();
        list.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                    PGModel pgModel = new PGModel();
                    pgModel.setId(jsonObject.getString("id"));
                    pgModel.setPayment_mode(jsonObject.getString("payment_mode"));
                    pgModel.setMdd(jsonObject.getString("mdd"));
                    pgModel.setTxn_charge(jsonObject.getString("txn_charge"));
                    pgModel.setGst_rate(jsonObject.getString("gst_rate"));
                    pgModel.setCharge_type(jsonObject.getString("charge_type"));
                    pgModel.setStatus(jsonObject.getString("status"));

                    list.add(pgModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        String[] prepaidStrings = new String[list.size()];
        for(int i=0;i<list.size();i++){
            prepaidStrings[i]=list.get(i).getPayment_mode();
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, prepaidStrings);

        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_pgmode.setAdapter(dataAdapter);

        spn_pgmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
                if(list.get(position).getCharge_type().equalsIgnoreCase("0")) {
                    et_amount.setText("");
                    et_charge.setText("");
                    tv_charges.setText("Rs. " + list.get(position).getTxn_charge() + " + GST");
                }
                else{
                    et_amount.setText("");
                    et_charge.setText("");
                    tv_charges.setText(""+list.get(position).getTxn_charge()+"% + GST");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("fragment response pg","="+requestCode);
        if (requestCode == 1)
        {
            System.out.println("---------INSIDE-------");
            if (data != null)
            {
                String message = data.getStringExtra("status");
                String[] resKey = data.getStringArrayExtra("responseKeyArray");
                String[] resValue = data.getStringArrayExtra("responseValueArray");
                HashMap<String,String> responsePG=new HashMap<>();
                if(resKey!=null && resValue!=null)
                {
                    for(int i=0; i<resKey.length; i++) {
                        System.out.println("  " + i + " resKey : " + resKey[i] + " resValue : " + resValue[i]);
                        try {
                            responsePG.put(resKey[i], resValue[i]);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                updateTransaction(responsePG);
                //  Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                System.out.println("RECEIVED BACK--->" + message);
            }

        }
    }

    private   void showMessageDialog( ){
       Dialog dialog=new Dialog(PaymentGatewayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // dialog.setCancelable(false);
       // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.message_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        CheckBox cb=((CheckBox) dialog.findViewById(R.id.cb_tc));
        ((CheckBox) dialog.findViewById(R.id.cb_tc)).setText(AppPreference.getInstance(this).getName()+" am a KYC registered A of Excel One Stop Solution Pvt ltd. (a2zsuvidhaa).\nI undertake that all Mobile Wallet ,Credit and Debit Card transactions made by me are genuine. If a Third Party's Credit/Debit Card is used to take balance on my Wallet Account, I hereby declare that such a transaction has been made by me only after informing and taking full consent of that Third Party. I also declare that in case I fail to do so, Excel One Stop Solution Pvt ltd. (a2zsuvidhaa) shall not be held liable and that the entire liability rests on me.In case of any chargeback or fraud transaction claim, I acknowledge the right of Excel One stop solution Pvt Ltd. (a2zsuvidhaa) to recover that amount from my Wallet Account.\");\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");

        dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked()) {
                    dialog.dismiss();
                   // makeTransaction();
                    Toast.makeText(PaymentGatewayActivity.this, "work in progress", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
    private void showConnectionError(String message) {
        tv_error_hint.setText(message);
        tv_error_hint.setVisibility(View.VISIBLE);

       Thread th_error_hint = new Thread(() -> {
            try {
                Thread.sleep(4000);
                if(PaymentGatewayActivity.this!=null){
                    Objects.requireNonNull(PaymentGatewayActivity.this)
                            .runOnUiThread(() -> tv_error_hint.setVisibility(View.GONE));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        th_error_hint.start();

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
