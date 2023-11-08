package com.a2zsuvidhaa.in.activity;


import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.a2zsuvidhaa.in.PermissionHandler;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.database.DBHelper;
import com.a2zsuvidhaa.in.listener.OnDatePicker;

import com.a2zsuvidhaa.in.listener.WebApiCallListener;
import com.a2zsuvidhaa.in.model.BankDetail;

import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AppUitls;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;
import com.a2zsuvidhaa.in.util.DatePicker;
import com.a2zsuvidhaa.in.util.file.StorageHelper;
import com.a2zsuvidhaa.in.util.FileUtils;
import com.a2zsuvidhaa.in.util.InternetConnection;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.SoftKeyboard;
import com.a2zsuvidhaa.in.util.WebApiCall;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class FunRequestActivityBKP extends AppCompatActivity implements OnDatePicker {

    private static final String TAG = "FunRequestActivityBKP";
    private final static int IMAGE_RESULT = 200;

    private Button btn_image_upload;
    private Button btn_requestPayment;

    private Spinner spn_requestTo;
    private Spinner spn_toAccount;
    private Spinner spn_paymentMode;
    private Spinner spn_online_mode;
    private EditText ed_amount;
    private EditText ed_refNumber;
    private EditText ed_remark;
    private EditText ed_bankName;

    private ProgressBar progressBar;
    private ProgressBar progressBar_toAccount;

    private RelativeLayout rl_progress;
    private LinearLayout ll_toAccount;
    private LinearLayout ll_onlineMode;
    private LinearLayout ll_refNumber;
    private LinearLayout ll_bankName;

    private TextView tv_approvalDetails;
    private TextView tv_paymentDate;
    private TextView tv_incorrect;
    private TextView tv_file_name;

    private HashMap<String, String> bankListHashMap = new HashMap<>();
    private LinkedHashMap<String, String> paymentModeHashMap = new LinkedHashMap<>();
    private LinkedHashMap<String, String> onlineModeHashMap = new LinkedHashMap<>();
    private String[] approvals = new String[2];

    private Uri picUri;
    private String strBankId = "";
    private String strBankName = "";
    private String strPaymentMode = "";
    private String strPaymentDate = "";
    private String strRequestTo = "";
    private String strAmount = "";
    private String strRemark = "";
    private String strRefNumber = "";
    private String strOnlineMode = "";
    private String get_from = "0";
    private String get_acc = "";
    private String get_ifsc = "";
    private String get_reqTo = "";

    private String selectedFilePath;

    private DBHelper dbHelper;

    String type = "1";
    String mode = "";
    BankDetail bankDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_request);

        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getString("type");
            mode =  getIntent().getExtras().getString("mode");
            bankDetail = getIntent().getExtras().getParcelable("bank");
        }
        if (getIntent().getExtras() != null) {
            get_from = getIntent().getExtras().getString("");
            get_from = getIntent().getExtras().getString("");
            get_from = getIntent().getExtras().getString("");
            get_from = getIntent().getExtras().getString("");

        }

        init();
        requestTo();
        setupPaymentMode();
        dbHelper = new DBHelper(this);
        btn_requestPayment.setOnClickListener(view -> {
            if (isFieldValid()) {

                Dialog confirmDialog = AppDialogs.confirmFundRequestDialog(this);

                TextView tv_requestTo = confirmDialog.findViewById(R.id.tv_requestTo);
                TextView tv_paymentMode = confirmDialog.findViewById(R.id.tv_paymentMode);
                TextView tv_paymentDate = confirmDialog.findViewById(R.id.tv_paymentDate);
                TextView tv_toAccount = confirmDialog.findViewById(R.id.tv_toAccount);
                TextView tv_refNumber = confirmDialog.findViewById(R.id.tv_refNumber);
                TextView tv_approveDetail = confirmDialog.findViewById(R.id.tv_approveDetail);
                TextView tv_amount = confirmDialog.findViewById(R.id.tv_amount);
                EditText ed_confirmAmount = confirmDialog.findViewById(R.id.ed_confirmAmount);
                Button btn_close = confirmDialog.findViewById(R.id.btn_close);
                Button btn_confirmTransfer = confirmDialog.findViewById(R.id.btn_confirmTransfer);
                LinearLayout ll_companyFields = confirmDialog.findViewById(R.id.ll_companyFields);

                if (strRequestTo.equalsIgnoreCase("2"))
                    ll_companyFields.setVisibility(View.VISIBLE);
                else ll_companyFields.setVisibility(View.GONE);

                tv_requestTo.setText(spn_requestTo.getSelectedItem().toString());
                tv_paymentMode.setText(spn_paymentMode.getSelectedItem().toString());
                tv_paymentDate.setText(strPaymentDate);
                if (type.equalsIgnoreCase("2"))
                    tv_toAccount.setText(spn_toAccount.getSelectedItem().toString());

                tv_refNumber.setText(strRefNumber);
                tv_approveDetail.setText(tv_approvalDetails.getText().toString());
                tv_amount.setText(strAmount);

                btn_close.setOnClickListener(view12 -> confirmDialog.dismiss());
                btn_confirmTransfer.setOnClickListener(view2 -> {
                    if (strAmount.equalsIgnoreCase(ed_confirmAmount.getText().toString())) {
                        SoftKeyboard.hide(this);
                        confirmDialog.dismiss();
                        if (dbHelper.isNumberExist(strRefNumber, strAmount)) {
                            if (dbHelper.canTransaction(strRefNumber, AppUitls.getTime())) {
                                requestPayment("update");
                            } else {
                                Dialog dialog = AppDialogs.transactionStatus(this,
                                        "Request of same amount with same Reference Id has " +
                                                "been send successfully\nPlease wait for 2 minutes", 3);
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                                btn_ok.setOnClickListener(view1 -> {
                                    dialog.dismiss();
                                });
                                dialog.show();
                            }
                        } else {
                            requestPayment("insert");
                        }

                    } else MakeToast.show(this, "Confirm amount does not matched!");
                });

                confirmDialog.show();
            }
        });
        tv_paymentDate.setOnClickListener(view -> DatePicker.datePicker(this));
        btn_image_upload.setOnClickListener(view -> PermissionHandler.checkStorageAndCameraPermission(this));
        PermissionHandler.setPermissionGrantedListener(isGranted -> {
            if (isGranted) {
                //Dialogs.INSTANCE.chooserDialog(this, Callalbel)
              //  pickFile();
                // startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });

        DatePicker.setupOnDatePicker(this);
        if (type.equalsIgnoreCase("2"))
            getBankList();

        setupRefPlaceHolder();

    }

    private void setupRefPlaceHolder(){
        if(mode.equalsIgnoreCase("BT"))
            ed_refNumber.setHint(bankDetail.getBank_transfer_place_holder());
        if(mode.equalsIgnoreCase("CASH_CDM"))
            ed_refNumber.setHint(bankDetail.getCash_cdm_place_holder());
        if(mode.equalsIgnoreCase("CASH_DEPOSIT"))
            ed_refNumber.setHint(bankDetail.getCash_deposit_place_holder());
    }

    private void requestTo() {
        String agentType = "Distributor";
        if (AppPreference.getInstance(this).getRollId() == 4 || AppPreference.getInstance(this).getRollId() == 3)
            agentType = "Master Distributor";


        String[] requestToList = new String[1];
        if (type.equalsIgnoreCase("1")) {
            ll_toAccount.setVisibility(View.GONE);
            spn_requestTo.setVisibility(View.VISIBLE);
            requestToList[0] = agentType;
        } else {
            spn_toAccount.setVisibility(View.VISIBLE);
            spn_requestTo.setVisibility(View.GONE);
            requestToList[0] = "Company";
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, requestToList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_requestTo.setAdapter(dataAdapter);


        spn_requestTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_requestTo.getSelectedItem().toString().equalsIgnoreCase(requestToList[0])) {
                    tv_approvalDetails.setText(approvals[0]);
                    strRequestTo = "1";
                    ed_refNumber.setText("");
                    //setupPaymentMode(1);
                    ll_toAccount.setVisibility(View.GONE);
                    Log.e("strRequestTo 1", "=");
                } else {
                    tv_approvalDetails.setText(approvals[1]);
                    strRequestTo = "2";
                    // setupPaymentMode(2);
                    ll_toAccount.setVisibility(View.VISIBLE);
                    Log.e("strRequestTo 2", "=");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupPaymentMode() {
        paymentModeHashMap.clear();
        /*if (requestTo == 1) {
            paymentModeHashMap.put("Cash", "Cash");
            paymentModeHashMap.put("Cheque", "Cheque");
            paymentModeHashMap.put("OnLine", "OnLine");

        } else {
            //paymentModeHashMap.put("Cheque", "Cheque");
            paymentModeHashMap.put("OnLine", "OnLine");
            paymentModeHashMap.put("cash@Counte", "cash@Counte");
            paymentModeHashMap.put("Cash@CDM", "Cash@CDM");



        }*/
        if (type.equalsIgnoreCase("2")) {
            strRequestTo = "2";
            if (MainActivity.mode_pos == 1)
            {

                paymentModeHashMap.put("OnLine", "OnLine");
            }
            else if (MainActivity.mode_pos == 2)
            {

                paymentModeHashMap.put("Cash@CDM", "Cash@CDM");
            }
            else if (MainActivity.mode_pos == 3)
            {

                paymentModeHashMap.put("cash@Counter", "cash@Counter");
            }
            else
            {
                paymentModeHashMap.put("Cash@Collect", "Cash@Collect");
            }
        } else {
            strRequestTo = "1";
            paymentModeHashMap.put("Cash", "Cash");
            paymentModeHashMap.put("Cheque", "Cheque");
            paymentModeHashMap.put("OnLine", "OnLine");
        }


        String[] paymentModeList = paymentModeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> paymentModeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, paymentModeList);
        paymentModeAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_paymentMode.setAdapter(paymentModeAdapter);

        spn_paymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strPaymentMode = paymentModeHashMap.get(spn_paymentMode.getSelectedItem().toString());
                if (strRequestTo.equals("1")) {
                    ll_onlineMode.setVisibility(View.GONE);
                    if (strPaymentMode != null) {
                        if (strPaymentMode.equalsIgnoreCase("Online")
                                || strPaymentMode.equalsIgnoreCase("Cheque")) {
                            ll_bankName.setVisibility(View.VISIBLE);
                            ll_refNumber.setVisibility(View.VISIBLE);
                        } else {
                            ll_bankName.setVisibility(View.GONE);
                            ll_refNumber.setVisibility(View.GONE);
                        }
                    }
                } else {
                    ll_refNumber.setVisibility(View.VISIBLE);
                    if (strPaymentMode.equalsIgnoreCase("Online")) {
                        ll_onlineMode.setVisibility(View.VISIBLE);
                        setupOnlineMode();
                    } else {
                        ll_onlineMode.setVisibility(View.GONE);
                    }
                }

                if (ll_bankName.getVisibility() == View.GONE) {
                    strBankName = "";
                    ed_bankName.setText("");

                }
                if (ll_refNumber.getVisibility() == View.GONE) {
                    strRefNumber = "";
                    ed_refNumber.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupOnlineMode() {

        onlineModeHashMap.clear();
        onlineModeHashMap.put("IMPS", "IMPS");
        onlineModeHashMap.put("NEFT", "NEFT");
        onlineModeHashMap.put("RTGS", "RTGS");
        onlineModeHashMap.put("OTHER", "OTHER");


        String[] paymentModeList = onlineModeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> paymentModeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, paymentModeList);
        paymentModeAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_online_mode.setAdapter(paymentModeAdapter);

        spn_online_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strOnlineMode = onlineModeHashMap.get(spn_online_mode.getSelectedItem().toString());

                if (ll_onlineMode.getVisibility() == View.GONE) {
                    strOnlineMode = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void init() {
        tv_file_name = findViewById(R.id.tv_file_name);
        btn_image_upload = findViewById(R.id.btn_image_upload);
        spn_requestTo = findViewById(R.id.spn_requestTo);
        spn_toAccount = findViewById(R.id.spn_toAccount);
        spn_paymentMode = findViewById(R.id.spn_paymentMode);
        spn_online_mode = findViewById(R.id.spn_online_mode);
        ed_amount = findViewById(R.id.ed_amount);
        ed_remark = findViewById(R.id.ed_remark);
        ed_bankName = findViewById(R.id.ed_bankName);
        ed_refNumber = findViewById(R.id.ed_refNumber);
        btn_requestPayment = findViewById(R.id.btn_requestPayment);
        progressBar = findViewById(R.id.progressBar);
        rl_progress = findViewById(R.id.rl_progress);
        progressBar_toAccount = findViewById(R.id.progressBar_toAccount);
        tv_approvalDetails = findViewById(R.id.tv_approvalDetail);
        tv_paymentDate = findViewById(R.id.tv_paymentDate);

        ImageButton imgBtn_back = findViewById(R.id.imgBtn_back);
        imgBtn_back.setOnClickListener(view -> onBackPressed());
        tv_incorrect = findViewById(R.id.tv_incorrect);

        tv_paymentDate.setText(AppUitls.currentDate());
        strPaymentDate = AppUitls.currentDate();


        ll_toAccount = findViewById(R.id.ll_toAccount);
        ll_onlineMode = findViewById(R.id.ll_onlineMode);
        ll_refNumber = findViewById(R.id.ll_refNumber);
        ll_bankName = findViewById(R.id.ll_bankName);

    }


    private void requestPayment(String insertOrUpdate) {
        setProgressVisibility(true, 1);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.FUND_REQUEST_SAVE,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("resp req fund", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            clearInput();
                            Dialog dialog = AppDialogs.transactionStatus(this, message, 1);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });
                            dialog.setOnDismissListener(dialogInterface -> {
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });

                            if (insertOrUpdate.equalsIgnoreCase("update")) {
                                if (!dbHelper.updateTransaction(strRefNumber,
                                        String.valueOf(AppUitls.getTimeFuture2Miuntes()), strAmount)) {
                                    MakeToast.show(this, "failed to insert record into database, but" +
                                            "\nyou don't have to be worry!" +
                                            "\nplease once contact to developer thankyou");
                                }
                            } else if (insertOrUpdate.equalsIgnoreCase("insert")) {
                                if (!dbHelper.saveTransaction(strRefNumber, "fund_requests",
                                        "mobile", String.valueOf(AppUitls.getTimeFuture2Miuntes()), strAmount)) {
                                    MakeToast.show(this, "failed to insert record into database, but" +
                                            "\nyou don't have to be worry!" +
                                            "\nplease once contact to developer thankyou");
                                }
                            }


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
                            Dialog dialog = AppDialogs.transactionStatus(
                                    this, message, 2);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                        }

                    } catch (JSONException e) {
                        tv_incorrect.setVisibility(View.VISIBLE);

                    }
                    setProgressVisibility(false, 1);


                },
                error -> {
                    setProgressVisibility(false, 1);
                    tv_incorrect.setVisibility(View.VISIBLE);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(FunRequestActivityBKP.this).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(FunRequestActivityBKP.this).getToken()));
                param.put("amount", strAmount);
                param.put("requestTo", strRequestTo);
                param.put("paymentDate", strPaymentDate);
                param.put("paymentMode", strPaymentMode);
                param.put("refNumber", strRefNumber);
                param.put("onlineMode", strOnlineMode);
                param.put("bankId", strBankId);
                param.put("bankName", strBankName);
                Log.e("prams fund req", "=" + param.toString());
                if (selectedFilePath != null)
                    param.put("d_picture", StorageHelper.INSTANCE.fileToString(selectedFilePath));
                param.put("remark", strRemark);

                return param;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean isFieldValid() {
        boolean isValid = false;
        strAmount = ed_amount.getText().toString();
        strRefNumber = ed_refNumber.getText().toString();
        strRemark = ed_remark.getText().toString();
        strBankName = ed_bankName.getText().toString();

        if (InternetConnection.isConnected(this)) {
            if (!strBankId.equalsIgnoreCase("") || strRequestTo.equalsIgnoreCase("1")) {
                tv_incorrect.setVisibility(View.GONE);
                if (!strAmount.isEmpty()) {
                    if (!strBankName.isEmpty() || ll_bankName.getVisibility() == View.GONE) {
                        if (!strRefNumber.isEmpty() || ll_refNumber.getVisibility() == View.GONE) {

                            if (type.equalsIgnoreCase("2")) {
                                if (selectedFilePath != null) {
                                    isValid = true;
                                } else MakeToast.show(this, "Please Upload Payment Slip.");
                            } else isValid = true;
                        } else MakeToast.show(this, "Bank Ref can't be empty!");

                    } else MakeToast.show(this, "Bank name can't be empty!");
                } else MakeToast.show(this, "Amuont can't be empty!");
            } else MakeToast.show(this,
                    "Request can't be approve !\nBank Name not found!\ntry after sometime");
        }
        return isValid;
    }


    void getBankList() {

        String url = APIs.FUND_REQUEST_BANK_LIST + "?";
        setProgressVisibility(true, 0);


        WebApiCall.getRequest(this, url);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                setProgressVisibility(false, 0);
                try {

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("1")) {

                        JSONObject bankObject = jsonObject.getJSONObject("banks");
                        setBankList(bankObject);
                        JSONObject approvalObject = jsonObject.getJSONObject("approvalDetail");
                        approvals[0] = approvalObject.getString("message1");
                        approvals[1] = approvalObject.getString("message2");
                        tv_approvalDetails.setText(approvals[0]);


                    } else if (status.equalsIgnoreCase("200")) {
                        Intent intent = new Intent(FunRequestActivityBKP.this, AppInProgressActivity.class);
                        intent.putExtra("message", message);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    } else if (status.equalsIgnoreCase("300")) {
                        Intent intent = new Intent(FunRequestActivityBKP.this, AppInProgressActivity.class);
                        intent.putExtra("message", message);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    } else MakeToast.show(FunRequestActivityBKP.this, message);

                } catch (JSONException e) {
                }

            }
            @Override
            public void onFailure(String message) {
                setProgressVisibility(false, 0);
            }
        });


    }


    private void setBankList(JSONObject pmethodObject) {
        try {
            bankListHashMap.clear();
            Iterator iterator = pmethodObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (bankDetail.getId().equalsIgnoreCase(key))
                    bankListHashMap.put(pmethodObject.getString(key), key);
            }
            String[] prepaidStrings = bankListHashMap.keySet().toArray(new String[0]);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                    R.layout.spinner_layout, prepaidStrings);
            dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
            spn_toAccount.setAdapter(dataAdapter);

            spn_toAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    strBankId = bankListHashMap.get(spn_toAccount.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setProgressVisibility(boolean b, int type) {

        if (type == 0) { // initial progress
            if (b) {
                progressBar_toAccount.setVisibility(View.VISIBLE);
            } else {
                progressBar_toAccount.setVisibility(View.GONE);
            }
        } else {// on button click request fund payment
            if (b) {
                btn_requestPayment.setVisibility(View.GONE);
                rl_progress.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                btn_requestPayment.setVisibility(View.VISIBLE);
                rl_progress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }



    private void pickFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 10);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 10) {
                Uri uri = data.getData();
                selectedFilePath = FileUtils.getPath(this, uri);
                String fileName = FileUtils.getFileName(this,uri);
                tv_file_name.setText(fileName);

                   /* String filePath = getImageFilePath(data);
                    if (filePath != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                        imageBitmap = AppUitls.getResizedBitmap(bitmap);
                        imageBitmap = getRotateImageBitmap(filePath,imageBitmap);
                        iv_upload_slip.setImageBitmap(imageBitmap);

                        iv_upload_slip.setVisibility(View.VISIBLE);
                        btn_image_upload.setText("Retake");
                    }*/
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        picUri = savedInstanceState.getParcelable("pic_uri");
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

    private void clearInput() {
        ed_amount.setText("");
        ed_refNumber.setText("");
        ed_remark.setText("");
    }


    @Override
    public void onDatePick(String date) {
        tv_paymentDate.setText(date);
        strPaymentDate = date;
    }

}
