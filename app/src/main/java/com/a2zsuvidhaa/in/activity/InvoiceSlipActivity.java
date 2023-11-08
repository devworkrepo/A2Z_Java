package com.a2zsuvidhaa.in.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.print.PrintHelper;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.adapter.ReportAdapter;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.NumberToWordsConverter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class InvoiceSlipActivity extends AppCompatActivity {


    //static textView for showing
    private TextView tvs_ipmsUtr;
    private TextView tvs_txnid;
    private TextView tvs_senderName;
    private TextView tvs_accountNo;
    private TextView tvs_ifscCode;
    private TextView tvs_senderNumber;
    private TextView tvs_beneficiaryName;

    private LinearLayout ll_commission;
    private LinearLayout bene_lin;
    private LinearLayout sender_lin;
    private LinearLayout ll_retry;
    private LinearLayout ll_slip;
    private Button btn_commissioin;
    private EditText ed_commission;
    private TextView tv_totalAmount;
    private TextView tv_amountInWord;
    private TextView tv_shopName;
    private TextView tv_contactNumber;
    private TextView tv_receiptNo;
    private TextView tv_date;
    private TextView tv_senderName;
    private TextView tv_senderNumber;
    private TextView tv_accountNo;
    private TextView tv_beneName;
    private TextView tv_ifse_code;
    private TextView tv_transactionType;
    private TextView tv_serviceProvider;
    private TextView tv_transaction_id;
    private TextView tv_impUtrNo;
    private TextView tv_amount;
    private TextView tv_status;
    private ProgressBar progressBar;

    private Button printSlip;
    private Button btn_retry;
    private ScrollView scrollView;

    private String report_id = "0";
    private int report_api_id = 0;
    private float amount = 0;
    private String invoiceType = "DMT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_slip);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
        getSlipReport();
        btn_commissioin.setOnClickListener(view -> setCommission());
        printSlip.setOnClickListener(view -> print());
        btn_retry.setOnClickListener(view -> getSlipReport());

    }


    private void init() {

        report_id = getIntent().getStringExtra(ReportAdapter.REPORT_ID);
        report_api_id = getIntent().getIntExtra(ReportAdapter.REPORT_API_ID, 0);

        bene_lin = findViewById(R.id.bene_lin);
        sender_lin = findViewById(R.id.sender_lin);
        ll_commission = findViewById(R.id.ll_add_commission);
        ll_retry = findViewById(R.id.ll_retry);
        ll_slip = findViewById(R.id.ll_slip);
        btn_commissioin = findViewById(R.id.btn_set_commission);
        ed_commission = findViewById(R.id.ed_commission);
        tv_totalAmount = findViewById(R.id.tv_totalAmount);
        tv_amountInWord = findViewById(R.id.tv_amountInWord);
        tv_shopName = findViewById(R.id.tv_shopName);
        tv_contactNumber = findViewById(R.id.tv_contactNumber);
        tv_receiptNo = findViewById(R.id.tv_receiptNo);
        tv_date = findViewById(R.id.tv_date);
        tv_senderName = findViewById(R.id.tv_senderName);
        tv_senderNumber = findViewById(R.id.tv_senderNumber);
        tv_accountNo = findViewById(R.id.tv_accountNo);
        tv_beneName = findViewById(R.id.tv_beneName);
        tv_ifse_code = findViewById(R.id.tv_ifse_code);
        tv_transactionType = findViewById(R.id.tv_transactionType);
        tv_serviceProvider = findViewById(R.id.tv_serviceProvider);
        tv_transaction_id = findViewById(R.id.tv_transaction_id);
        tv_impUtrNo = findViewById(R.id.tv_impUtrNo);
        tv_amount = findViewById(R.id.tv_amount);
        tv_status = findViewById(R.id.tv_status);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);

        printSlip = findViewById(R.id.printSlip);
        btn_retry = findViewById(R.id.btn_retry);

        if (report_api_id == 25
                || report_api_id == 4
                || report_api_id == 16 || report_api_id == 10 || report_api_id == 28)
        {
            if (report_api_id == 25) {
                ll_commission.setVisibility(View.VISIBLE);
            }
            else if (report_api_id == 10 || report_api_id == 28 ) {
                ll_commission.setVisibility(View.GONE);
                bene_lin.setVisibility(View.INVISIBLE);
                sender_lin.setVisibility(View.INVISIBLE);
            }
            else {
                printSlip.setVisibility(View.VISIBLE);
                ll_commission.setVisibility(View.GONE);
            }

            invoiceType = "DMT";
        } else if(report_api_id == 1 || report_api_id == 27 || report_api_id == 30 || report_api_id == 40 ) {
            ll_commission.setVisibility(View.GONE);
            invoiceType = "BBPS";
        }


        tvs_ipmsUtr = findViewById(R.id.tvs_ipmsUtr);
        tvs_txnid = findViewById(R.id.tvs_txnid);
        tvs_senderName = findViewById(R.id.tvs_senderName);
        tvs_accountNo = findViewById(R.id.tvs_accountNo);
        tvs_ifscCode = findViewById(R.id.tvs_ifscCode);
        tvs_senderNumber = findViewById(R.id.tvs_senderNumber);
        tvs_beneficiaryName = findViewById(R.id.tvs_beneficiaryName);


    }

    private void getSlipReport() {
        ll_retry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String url = APIs.SLIP_REPORT
                + "?" + APIs.USER_TAG + "=" + AppPreference.getInstance(this).getId()
                + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(this).getToken()
                + "&type=" + invoiceType
                + "&id=" + report_id;
        Log.e("SLIP_REPORT","="+url);
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            ll_slip.setVisibility(View.VISIBLE);
                            ll_retry.setVisibility(View.GONE);
                            JSONObject slipObject = jsonObject.getJSONObject("data");
                            setData(slipObject);

                        } else if (status.equalsIgnoreCase("200")) {
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        }


                    } catch (JSONException e) {
                        MakeToast.show(InvoiceSlipActivity.this,
                                "Something went wrong\nContact to your superior(Distributor)");
                        finish();
                    }
                    progressBar.setVisibility(View.GONE);

                },
                error -> {

                    progressBar.setVisibility(View.GONE);
                    ll_commission.setVisibility(View.GONE);
                    ll_retry.setVisibility(View.VISIBLE);
                }) {

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(60),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setData(JSONObject slipObject) {

        try {
            amount = Float.parseFloat(slipObject.getString("amount"));

            if (invoiceType.equalsIgnoreCase("DMT")) {
                tv_shopName.setText(slipObject.getString("shopName"));
                Log.e("tv_contactNumber","="+slipObject.getString("shopContact"));
                tv_contactNumber.setText(slipObject.getString("shopContact"));
                tv_receiptNo.setText(slipObject.getString("recieptNo"));
                tv_date.setText(slipObject.getString("createdAt"));
                tv_senderName.setText(slipObject.getString("billerName"));
                tv_senderNumber.setText(slipObject.getString("senderMobile"));
                tv_accountNo.setText(slipObject.getString("accountNumber"));
                tv_beneName.setText(slipObject.getString("beneficiaryName"));
                tv_ifse_code.setText(slipObject.getString("ifscCode"));
                tv_transactionType.setText(slipObject.getString("transactionType"));
                tv_serviceProvider.setText(slipObject.getString("serviceProvider"));
                tv_transaction_id.setText(slipObject.getString("txnId"));
                tv_impUtrNo.setText(slipObject.getString("impsUtrNo"));
                tv_status.setText(slipObject.getString("status"));
                tv_amount.setText(slipObject.getString("amount"));

                tvs_ifscCode.setText("Bank Name");
            } else {

                tv_shopName.setText(slipObject.getString("shopName"));
                tv_contactNumber.setText(slipObject.getString("shopContact"));
                tv_receiptNo.setText(slipObject.getString("recieptNo"));
                tv_date.setText(slipObject.getString("createdAt"));
                tv_senderName.setText(slipObject.getString("billerName"));
                tv_senderNumber.setText(slipObject.getString("consumerIdNo"));//consumer number or id
                tv_accountNo.setText(slipObject.getString("customerMobNO"));//customer mobile number
                tv_beneName.setText(slipObject.getString("paymentMode"));// payment mode
                tv_ifse_code.setText(slipObject.getString("paymentChannel"));//payment channel
                tv_transactionType.setText(slipObject.getString("transactionType"));
                tv_serviceProvider.setText(slipObject.getString("serviceProvider"));
               // tv_transaction_id.setText(slipObject.getString("txnId"));
                tv_transaction_id.setVisibility(View.GONE);
                Log.e("txnId","="+slipObject.getString("txnId"));
                tv_impUtrNo.setText(slipObject.getString("txnId")); //pay id (transaction id)
                tv_status.setText(slipObject.getString("status"));
                tv_amount.setText(slipObject.getString("amount"));


                tvs_ipmsUtr.setText("Transaction ID");
                //tvs_txnid.setText("BBPS Transaction ID");
                tvs_txnid.setVisibility(View.GONE);
                tvs_senderName.setText("Customer Name");
                tvs_accountNo.setText("Customer Mobile No.");
                tvs_ifscCode.setText("Payment Channel");
                tvs_senderNumber.setText("Consumer Id/No.");
                tvs_beneficiaryName.setText("Payment Mode");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        tv_totalAmount.setText(String.valueOf(amount));
        tv_amountInWord.setText(NumberToWordsConverter.convert(((int)amount)) + " Rupees Only/-");


    }

    private void setCommission() {
        if (ed_commission.getText().toString().equals(""))
            ed_commission.setText("0");

        String comAmount = ed_commission.getText().toString();
        float mainAmount = (amount + Integer.parseInt(comAmount));
        tv_totalAmount.setText(String.valueOf(mainAmount));
        tv_amountInWord.setText(NumberToWordsConverter.convert(((int)mainAmount)) + " Rupees Only/-");
        ll_commission.setVisibility(View.GONE);
        printSlip.setVisibility(View.VISIBLE);
    }
    private void print() {


       /* Bitmap viewBitmap = Bitmap.createBitmap(scrollView.getWidth(),scrollView.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewBitmap);
        scrollView.draw(canvas);*/

        Bitmap viewBitmap = getBitmapFromView(scrollView, scrollView.getHeight(), scrollView.getWidth());

        // Get the print manager.
        PrintHelper printHelper = new PrintHelper(this);
        // Set the desired scale mode.
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        // Get the bitmap for the ImageView's drawable.
        //Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        // Print the bitmap.
        printHelper.printBitmap("Print Bitmap", viewBitmap);
    }

    public static Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        int height = Math.min(1000, totalHeight);
        float percent = height / (float) totalHeight;

        Bitmap canvasBitmap = Bitmap.createBitmap((int) (totalWidth * percent), (int) (totalHeight * percent), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);

        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);

        canvas.save();
        canvas.scale(percent, percent);
        view.draw(canvas);
        canvas.restore();

        return canvasBitmap;
    }
}