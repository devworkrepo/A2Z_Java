package com.a2zsuvidhaa.in.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a2zsuvidhaa.in.fragment.PaymentFundReportFragment;
import com.a2zsuvidhaa.in.util.enums.PaymentReportType;
import com.a2zsuvidhaa.in.util.ReportJsonParser;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.adapter.PaymentFundReportAdapter;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.PaymentReport;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PaymentSearchActivity extends AppCompatActivity {


    private static final String TAG = "PaymentFundSearch";
    private String date_to = "";
    private String date_from = "";
    private String search_url;
    private String search_url2;
    private TextView tv_noResult;

    private int year, month, day;
    Calendar myCalendar;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private PaymentFundReportAdapter adapter;


    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private String page = "";
    private PaymentReportType  paymentReportType;
    private HashMap<String,String> userHashmap = new HashMap<>();

    private String user="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_funt_report_search);


        paymentReportType =    (PaymentReportType) getIntent()
                .getSerializableExtra(PaymentFundReportFragment.PAYMENT_REPORT_TYPE);
        date_from = getIntent().getStringExtra("date_from");
        date_to = getIntent().getStringExtra("date_to");
        search_url = getIntent().getStringExtra("search_url");
        if(paymentReportType == PaymentReportType.FUND_TRANSFER_REPORT)
            user = getIntent().getStringExtra("user");
        tv_noResult = findViewById(R.id.tv_noResult);


        if (savedInstanceState != null) {
            date_from = savedInstanceState.getString("date_from");
            date_to = savedInstanceState.getString("date_to");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Payment Search");
        if(paymentReportType == PaymentReportType.FUND_TRANSFER_REPORT)
            toolbar.setTitle("Fund Transfer Search");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);


        main_progressbar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        setRecyclerViewAdapter();

        getReportSearchData(1);

    }

    private void setRecyclerViewAdapter() {

        adapter = new PaymentFundReportAdapter(this,paymentReportType);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getReportSearchData(2);


            }

            @Override
            public int getTotalPageCount() {
                return total_page;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {


            Dialog dialog = AppDialogs.searchReport(this);
            TextView tv_start_date = dialog.findViewById(R.id.tv_start_date);
            TextView tv_end_date = dialog.findViewById(R.id.tv_end_date);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);

            tv_start_date.setOnClickListener(view -> {
                datePicker(tv_start_date, 1);
            });

            tv_end_date.setOnClickListener(view -> {
                datePicker(tv_end_date, 2);
            });

            btn_cancel.setOnClickListener(view -> {
                dialog.dismiss();
            });
            btn_search.setOnClickListener(view -> {

                if (!date_from.isEmpty() && !date_to.isEmpty()) {
                    recreate();
                    dialog.dismiss();
                } else MakeToast.show(this, "Select start and end date");
            });

            dialog.show();
        }
        return true;
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

    private void datePicker(TextView tv_date, int type) {

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    monthOfYear = monthOfYear + 1;

                    String m = String.valueOf(monthOfYear);
                    String d = String.valueOf(dayOfMonth);

                    if (monthOfYear < 10) {

                        m = ("0" + (monthOfYear));
                    }
                    if (dayOfMonth < 10) {

                        d = ("0" + (dayOfMonth));
                    }

                    tv_date.setText(d + "-" + (m) + "-" + year);
                    if (type == 1) {
                        date_from = year + "-" + (m) + "-" + d;
                    } else {
                        date_to = year + "-" + (m) + "-" + d;
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void getReportSearchData(int type) {


        if (type == 1)
            search_url2 = search_url + "&fromdate=" + date_from + "&todate=" + date_to+"&user="+user;
        if (type == 2) {
            search_url2 = search_url + "&fromdate=" + date_from + "&todate=" + date_to +"&user="+user+ "&" + page;
        } else main_progressbar.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.GET, search_url2,


                response -> {
                    try {
                        main_progressbar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            int count = jsonObject.getInt("count");

                            if (count > 0) {
                                total_page += 1;
                                page = jsonObject.getString("page");
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                parseArray(jsonArray, type);
                                if(paymentReportType == PaymentReportType.FUND_TRANSFER_REPORT){
                                    try{
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("users");
                                        Iterator iterator = jsonObject1.keys();
                                        while (iterator.hasNext()) {
                                            String key = (String) iterator.next();
                                            userHashmap.put(jsonObject1.getString(key), key);

                                        }
                                    }catch (Exception e){
                                    }
                                }
                            } else {
                                page = "";
                                adapter.removeLoadingFooter();
                                isLoading = false;
                                isLastPage = true;
                                if (adapter.getItemCount() > 0) {
                                    tv_noResult.setVisibility(View.GONE);
                                } else tv_noResult.setVisibility(View.VISIBLE);

                            }
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
                        main_progressbar.setVisibility(View.GONE);

                    }
                },
                error -> {
                    main_progressbar.setVisibility(View.GONE);
                }) {

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }


    private void parseArray(JSONArray jsonArray, int type) {

        ArrayList<PaymentReport> paymentReportList = new ArrayList<>();

        try {
            ReportJsonParser parser = new ReportJsonParser(jsonArray);
            paymentReportList = parser.parsePaymentReport(paymentReportType);

        } catch (JSONException e) {
            main_progressbar.setVisibility(View.GONE);
            e.printStackTrace();
        }


        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            adapter.addAll(paymentReportList);

            if (currentPage <= total_page) adapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(paymentReportList);

            if (currentPage != total_page)
                adapter.addLoadingFooter();
            else isLastPage = true;


        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("date_from", date_from);
        bundle.putString("date_to", date_to);
    }

}
