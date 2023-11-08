/*
package com.a2zsuvidhaa.in.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.PidParser2;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.SharedPref;
import com.a2zsuvidhaa.in.listener.WebApiCallListener;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.WebApiCall;
import com.a2zsuvidhaa.in.util.dialogs.StatusDialog;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class KYCApprovalActivity extends AppCompatActivity {

    TextView message_select_device;
    TextView message;
    TextView device_approve;


    Button btnApprove;

    EditText edName;
    EditText edPanCard;
    EditText edAadharNumber;
    EditText edMerchantId;

    private static final int RD_SERVICE_RESPONSE_DODE = 122;

    Button btn_resend;
    SharedPref sharedPref;

    private Spinner spn_selectDevice;
    private String deviceName = "MANTRA";

    Dialog dialogSelectDevice;


    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_y_c_approval);

        sharedPref = new SharedPref(this);

        edName = findViewById(R.id.ed_name);
        btn_resend = findViewById(R.id.btn_resend);
        message = findViewById(R.id.message);
        edPanCard = findViewById(R.id.ed_pan_card);
        edAadharNumber = findViewById(R.id.ed_aadhar_number);
        edMerchantId = findViewById(R.id.ed_merchant_id);
        btnApprove = findViewById(R.id.btn_next);


        btnApprove.setOnClickListener((View v) -> {
            requestOtp();
        });
        getKycDetails();


    }


    public void getKycDetails() {

        Dialog progressDialog = StatusDialog.INSTANCE.progress(this, "Loading");

        WebApiCall.getRequestWithHeader(this, APIs.USER_KYC_DETAILS);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                try {

                    int status = jsonObject.getInt("status");
                    String message = jsonObject.optString("message");

                    if (status == 1) {

                        JSONObject detailObject = jsonObject.getJSONObject("details");

                        edName.setText(detailObject.getString("agent_name"));
                        edPanCard.setText(detailObject.getString("pan_number"));
                        edAadharNumber.setText(detailObject.getString("aadhaar_number"));
                        edMerchantId.setText(detailObject.getString("merchant_login_id"));
                    } else {
                        StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);
                    }

                } catch (Exception e) {
                    StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, e.getMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);
            }
        });

    }


    public void requestOtp() {


        Dialog progressDialog = StatusDialog.INSTANCE.progress(this, "Requesting for OTP");

        WebApiCall.getRequestWithHeader(this, APIs.SEND_OTP);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                progressDialog.dismiss();


                try {
                    String message = jsonObject.optString("message");
                    int status = jsonObject.getInt("status");

                    if (status == 1) {
                        showVerifyOtpDialog();
                    } else StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);

                    MakeToast.show(KYCApprovalActivity.this, message);

                } catch (Exception e) {
                    StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, e.getMessage().toString());
                }


            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);
            }
        });


    }

    private void showVerifyOtpDialog() {

        verifyOtp("");
    }

    private void setDeviceType() {

        dialogSelectDevice = new Dialog(this);
        dialogSelectDevice.setContentView(R.layout.dialog_select_device);
        dialogSelectDevice.setCanceledOnTouchOutside(false);
        dialogSelectDevice.setCancelable(false);
        ImageView close = dialogSelectDevice.findViewById(R.id.close);
        message_select_device = dialogSelectDevice.findViewById(R.id.message_select_device);
        close.setOnClickListener(v -> {
            dialogSelectDevice.dismiss();
        });
        Window window2 = dialogSelectDevice.getWindow();
        window2.setBackgroundDrawableResource(android.R.color.transparent);
        window2.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        spn_selectDevice = dialogSelectDevice.findViewById(R.id.spn_selectDevice);
        device_approve = dialogSelectDevice.findViewById(R.id.device_approve);


        device_approve.setOnClickListener(v -> {
            captureData();
        });


        dialogSelectDevice.show();
        message_select_device.setText("Please connect biometric device and complete kyc process.");
        message_select_device.setTextColor(getResources().getColor(R.color.white));
        String[] deviceList = {"MANTRA", "MORPHO"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, deviceList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_selectDevice.setAdapter(dataAdapter);
        spn_selectDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_selectDevice.getSelectedItem().toString().equalsIgnoreCase(deviceList[0])) {
                    deviceName = "MANTRA";
                } else {
                    deviceName = "MORPHO";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void captureData() {
        final String MANTRA_PACKAGE_URL = "com.mantra.rdservice";
        final String MORPHO_PACKAGE_URL = "com.scl.rdservice";

        try {

            String pidOption = "<PidOptions ver=\"1.0\"><Opts env=\"P\" fCount=\"1\" fType=\"2\" iCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"15000\" wadh=\"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\" posh=\"UNKNOWN\" /></PidOptions>";
            Log.d("TAG", "captureData:Hello " + pidOption);
            Intent intent = new Intent();
            if (deviceName.equalsIgnoreCase("MANTRA"))
                intent.setPackage(MANTRA_PACKAGE_URL);
            else intent.setPackage(MORPHO_PACKAGE_URL);
            intent.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
            intent.putExtra("PID_OPTIONS", pidOption);
            startActivityForResult(intent, RD_SERVICE_RESPONSE_DODE);


        } catch (Exception e) {
            message_select_device.setText("Biometric device is not connected.");
            message_select_device.setTextColor(getResources().getColor(R.color.failed));
            Dialog dialog = AppDialogs.transactionStatus(this, "No device is connected! please reconnect and try again", 2);
            Button btnOK = dialog.findViewById(R.id.btn_ok);
            btnOK.setOnClickListener(view -> dialog.dismiss());
            dialog.show();
            MakeToast.show(this, "No device is connected! please reconnect and try again");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RD_SERVICE_RESPONSE_DODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data != null) {
                        String result = data.getStringExtra("PID_DATA");
                        if (result != null) {
                            try {
                                String[] respString = PidParser2.parse(result);
                                if (respString[0].equalsIgnoreCase("0")) {
                                    confirmDialog(result);
                                } else {
                                    dialogSelectDevice.show();

                                    message_select_device.setText("Biometric device is not connected.");
                                    message_select_device.setTextColor(getResources().getColor(R.color.failed));
                                    message_select_device.setText(respString[1]);
                                    MakeToast.show(this, respString[1]);
                                }
                            } catch (XmlPullParserException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            dialogSelectDevice.show();
                            message_select_device.setText("Biometric device is not connected.");
                            message_select_device.setTextColor(getResources().getColor(R.color.failed));
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void resendOtp() {

        Dialog progressDialog = StatusDialog.INSTANCE.progress(this, "Resending OTP");

        WebApiCall.getRequestWithHeader(this, APIs.RESEND_OTP_AES);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                progressDialog.dismiss();

                String message = jsonObject.optString("message");
                MakeToast.show(KYCApprovalActivity.this, message);
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
            }
        });
    }

    public void verifyOtp(String otp) {

        HashMap<String, String> params = new HashMap<>();
        params.put("otp", otp);
        WebApiCall.postRequest(this, APIs.VERIFY_OTP, params);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                try {
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.optString("message");

                    if (status == 1) {
                        Dialog dialog = StatusDialog.INSTANCE.success(KYCApprovalActivity.this, message);
                        dialog.setOnDismissListener(dialog1 -> {
                            setDeviceType();
                        });
                    } else StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);

                } catch (Exception e) {
                    StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, e.getMessage().toString());
                }
            }

            @Override
            public void onFailure(String message) {
                StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);
            }
        });


    }

    public void confirmDialog(String otp) {

        Dialog progressDialog = StatusDialog.INSTANCE.progress(this, "Loading");

        HashMap<String, String> params = new HashMap<>();
        params.put("biometricData", otp);

        WebApiCall.postRequest(this, APIs.VERIFY_KYC, params);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                try {

                    int status = jsonObject.getInt("status");
                    String message = jsonObject.optString("message");

                    if (status == 1) {
                        dialogSelectDevice.dismiss();
                        sharedPref.setKYC(1);
                        Dialog dialog = StatusDialog.INSTANCE.success(KYCApprovalActivity.this, "KYC successfully completed.");

                        dialog.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                            KYCApprovalActivity.this.onBackPressed();
                        });
                    } else StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);

                } catch (Exception e) {
                    StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, e.getMessage().toString());
                }
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                StatusDialog.INSTANCE.failure(KYCApprovalActivity.this, message);
            }
        });
    }

}
*/
