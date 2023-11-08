package com.a2zsuvidhaa.in.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.adapter.ReportAdapter;
import com.a2zsuvidhaa.in.adapter.FundReportAdapter;
import com.a2zsuvidhaa.in.adapter.ReportAdapter2;
import com.a2zsuvidhaa.in.util.enums.DatePickerType;
import com.a2zsuvidhaa.in.util.enums.ReportTypes;
import com.a2zsuvidhaa.in.fragment.ReportFragment;
import com.a2zsuvidhaa.in.listener.OnDatePicker;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.Report;
import com.a2zsuvidhaa.in.model.FundReport;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AppUitls;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;
import com.a2zsuvidhaa.in.util.DatePicker;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.ReportJsonParser;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ReportSearchActivity extends AppCompatActivity implements OnDatePicker {


    private static final String TAG = "ReportSearchActivity";
    private String date_to = "";
    private String date_from = "";
    private String search_type = "";
    private String searchInput = "";
    private String search_url;
    private String search_url2;
    private String toolbar_title = "Report Search";
    private TextView tv_noResult;

    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ReportAdapter reportSearchAdapter;
    private ReportAdapter2 reportSearchAdapter2;
    private FundReportAdapter fundReportAdapter;

    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private String page = "";
    private boolean isFundRequest = false;
    private HashMap<String, String> searchTypeHashMap = new HashMap<>();
    private boolean isDate = false;
    private String strStatus = "0";
    private String strProduct = "0";

    private HashMap<String, String> orderStatusHashMap = new HashMap<>();
    private HashMap<String, String> orderProductHashMap = new HashMap<>();

    private TextView tv_start_date;
    private TextView tv_end_date;
    private ReportTypes reportTypes;
    private DatePickerType datePickerType;
    private int reportAdapterType=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_search);


        reportTypes = (ReportTypes) getIntent().getSerializableExtra(ReportFragment.REPORT_TYPE);
        search_type = getIntent().getStringExtra("type");
        search_url = getIntent().getStringExtra("search_url");

        if (reportTypes == ReportTypes.LEDGER_REPORT)
            toolbar_title = "Ledger Search";
        else if (reportTypes == ReportTypes.USAGE_REPORT)
            toolbar_title = "Usage Search";
        else if (reportTypes == ReportTypes.FUND_REPORT)
            toolbar_title = "Fund Search";
        else if (reportTypes == ReportTypes.ACCOUNT_STATEMENT){
            reportAdapterType =2;
            toolbar_title = "Account Stm Search";
        }



        if (reportTypes == ReportTypes.LEDGER_REPORT  || reportTypes == ReportTypes.USAGE_REPORT) {
            if (search_type.equalsIgnoreCase("Date")) {
                date_from = getIntent().getStringExtra("date_from");
                date_to = getIntent().getStringExtra("date_to");
                strStatus = getIntent().getStringExtra("statusOf");
                strProduct = getIntent().getStringExtra("productOf");
                isDate = true;
            } else {
                search_url = getIntent().getStringExtra("search_url");
                searchInput = getIntent().getStringExtra("searchInput");
                isDate = false;
            }
        }else if(reportTypes == ReportTypes.ACCOUNT_STATEMENT){
            date_from = getIntent().getStringExtra("date_from");
            date_to = getIntent().getStringExtra("date_to");
            isDate = true;
        }
        else if (reportTypes == ReportTypes.FUND_REPORT || reportTypes == ReportTypes.DT_FUND_REPORT) {
            isFundRequest = true;
            toolbar_title = "Fund Search";
            if(ReportTypes.DT_FUND_REPORT == reportTypes)
                toolbar_title = "DT Search";

            date_from = getIntent().getStringExtra("date_from");
            date_to = getIntent().getStringExtra("date_to");
        }


        searchTypeHashMap.put("Record ID", "RECORD_ID");
        searchTypeHashMap.put("Txn ID", "TXN_ID");
        if (!toolbar_title.equalsIgnoreCase("Recharge Report Search"))
            searchTypeHashMap.put("Account No", "ACCOUNT_NO");
        searchTypeHashMap.put("Mobile Number", "MOB_NO");
        searchTypeHashMap.put("Date", "DATE");


        HashMap<String, String> statusHashMap = new HashMap<>();
        HashMap<String, String> productHashMap = new HashMap<>();

        //status hashMap
        statusHashMap.put("--Select--", "0");
        statusHashMap.put("Success", "1");
        statusHashMap.put("Failed", "2");
        statusHashMap.put("Pending", "3");
        statusHashMap.put("Refunded", "4");
        statusHashMap.put("Successfully Submitted", "24");
        statusHashMap.put("Refund Success", "21");
        statusHashMap.put("Commission", "22");
        statusHashMap.put("In-Process", "18");

        //product hashmap
        productHashMap.put("--Select--", "0");
        productHashMap.put("DMT1", "4");
        productHashMap.put("A2Z wallet", "5");
        productHashMap.put("AEPS", "10");
        productHashMap.put("Verify", "2");
        productHashMap.put("Recharge/Bill Payment", "1");

        orderProductHashMap = (HashMap<String, String>) AppUitls.sortByComparator(productHashMap, true);
        orderStatusHashMap = (HashMap<String, String>) AppUitls.sortByComparator(statusHashMap, true);


        if (savedInstanceState != null) {
            date_from = savedInstanceState.getString("date_from");
            date_to = savedInstanceState.getString("date_to");
            search_type = savedInstanceState.getString("search_type");
            isDate = savedInstanceState.getBoolean("isDate");
            isFundRequest = savedInstanceState.getBoolean("isFundRequest");
            searchInput = savedInstanceState.getString("searchInput");
            strStatus = savedInstanceState.getString("strStatus");
            strProduct = savedInstanceState.getString("strProduct");
        }
        tv_noResult = findViewById(R.id.tv_noResult);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(toolbar_title);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        main_progressbar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        setRecyclerViewAdapter();

        getReportSearchData(1);

        DatePicker.setupOnDatePicker(this);

    }

    private void setRecyclerViewAdapter() {

        if (isFundRequest) {
            fundReportAdapter = new FundReportAdapter(this, reportTypes);
        } else {
            if(reportAdapterType ==1)
            reportSearchAdapter = new ReportAdapter(this, reportTypes);
            else  reportSearchAdapter2 = new ReportAdapter2(this, reportTypes);
        }

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (isFundRequest) {
            recyclerView.setAdapter(fundReportAdapter);
        } else {
            if(reportAdapterType ==1)
            recyclerView.setAdapter(reportSearchAdapter);
            else recyclerView.setAdapter(reportSearchAdapter2);
        }
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


            Dialog dialog = AppDialogs.searchReport1(this);

            tv_start_date = dialog.findViewById(R.id.tv_start_date);
            tv_end_date = dialog.findViewById(R.id.tv_end_date);

            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            EditText ed_searchInput = dialog.findViewById(R.id.ed_searchType);
            LinearLayout ll_date = dialog.findViewById(R.id.ll_date);
            Spinner spn_search = dialog.findViewById(R.id.spn_searchType);

            LinearLayout ll_product = dialog.findViewById(R.id.ll_product);
            LinearLayout ll_status = dialog.findViewById(R.id.ll_status);
            Spinner spn_status = dialog.findViewById(R.id.spn_status);
            Spinner spn_product = dialog.findViewById(R.id.spn_product);

            setupStatus(spn_status);
            setupProduct(spn_product);


            setupSearchTypeSpn(spn_search, ed_searchInput, ll_date, ll_product, ll_status);


            tv_start_date.setOnClickListener(view -> {
                datePickerType = DatePickerType.START_DATE;
                DatePicker.datePicker(this);
            });

            tv_end_date.setOnClickListener(view -> {
                datePickerType = DatePickerType.END_DATE;
                DatePicker.datePicker(this);
            });

            btn_cancel.setOnClickListener(view -> {
                dialog.dismiss();
            });
            btn_search.setOnClickListener(view -> {

                if (isDate) {
                    if (!date_from.isEmpty() && !date_to.isEmpty()) {
                        recreate();
                        dialog.dismiss();
                    } else MakeToast.show(this, "Select start and end date");
                } else {
                    if (!ed_searchInput.getText().toString().isEmpty()) {
                        searchInput = ed_searchInput.getText().toString();
                        recreate();
                        dialog.dismiss();
                    } else MakeToast.show(this, "Enter search input");
                }
            });


            dialog.show();
        }
        return true;
    }

    private void setupStatus(Spinner spinner) {
        String[] prepaidStrings = orderStatusHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(this),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strStatus = orderStatusHashMap.get(spinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupProduct(Spinner spinner) {
        String[] prepaidStrings = orderProductHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(this),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strProduct = orderProductHashMap.get(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSearchTypeSpn(Spinner spinner, EditText ed_searchInput, LinearLayout
            ll_date, LinearLayout ll_product, LinearLayout ll_status) {
        String[] prepaidStrings = searchTypeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(this),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);


        if (reportTypes == ReportTypes.ACCOUNT_STATEMENT) {
            int position = dataAdapter.getPosition("Date");
            spinner.setSelection(position);
            spinner.setEnabled(false);
        }
        else spinner.setEnabled(true);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search_type = searchTypeHashMap.get(spinner.getSelectedItem().toString());

                if (spinner.getSelectedItem().toString().equalsIgnoreCase("Date")) {
                    ll_date.setVisibility(View.VISIBLE);
                    ed_searchInput.setVisibility(View.GONE);
                    if (reportTypes == ReportTypes.LEDGER_REPORT) {
                        ll_status.setVisibility(View.VISIBLE);
                        ll_product.setVisibility(View.VISIBLE);
                    } else if (ReportTypes.USAGE_REPORT == reportTypes) {
                        ll_status.setVisibility(View.VISIBLE);
                        ll_product.setVisibility(View.GONE);
                    }

                    isDate = true;
                }else {
                    isDate = false;
                    ll_date.setVisibility(View.GONE);
                    ed_searchInput.setVisibility(View.VISIBLE);
                    ll_product.setVisibility(View.GONE);
                    ll_status.setVisibility(View.GONE);
                }

                if (spinner.getSelectedItem().toString().equalsIgnoreCase("Name")) {
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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


    private void getReportSearchData(int type) {

        if (strProduct.equalsIgnoreCase("0"))
            strProduct = "";
        if (strStatus.equalsIgnoreCase("0"))
            strStatus = "";

        if (isFundRequest) {
            if (type == 1)
                search_url2 = search_url + "&start_date=" + date_from + "&end_date=" + date_to;
            if (type == 2)
                search_url2 = search_url + "&start_date=" + date_from + "&end_date=" + date_to + "&" + page;
            else main_progressbar.setVisibility(View.VISIBLE);
        } else {
            if (type == 1) {
                if (isDate)
                    search_url2 = search_url + "&start_date=" + date_from + "&end_date=" + date_to + "&productOf=" + strProduct
                            + "&statusOf=" + strStatus;
                else
                    search_url2 = search_url + "&SEARCH_TYPE=" + search_type + "&SEARCH_INPUT=" + searchInput;
            } if (type == 2) {
                if (isDate)
                    search_url2 = search_url + "&start_date=" + date_from + "&end_date=" + date_to + "&" + "&productOf="
                            + strProduct + "&statusOf=" + strStatus + "&" + page;
                else
                    search_url2 = search_url + "&SEARCH_TYPE=" + search_type + "&SEARCH_INPUT=" + searchInput + "&" + page;
            } else main_progressbar.setVisibility(View.VISIBLE);
        }
        Log.e("report search","="+search_url2);
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
                                JSONArray jsonArray = jsonObject.getJSONArray("reports");
                                if (isFundRequest)
                                    parseFundReportData(jsonArray, type);
                                else parseReportData(jsonArray, type);
                            } else {
                                if (isFundRequest) {
                                    if (!fundReportAdapter.isEmpty()) {
                                        page = "";
                                        fundReportAdapter.removeLoadingFooter();
                                        isLoading = false;
                                        isLastPage = true;
                                        tv_noResult.setVisibility(View.GONE);

                                    } else tv_noResult.setVisibility(View.VISIBLE);
                                } else {
                                    if(reportAdapterType ==1){
                                        if (!reportSearchAdapter.isEmpty()) {
                                            page = "";
                                            reportSearchAdapter.removeLoadingFooter();
                                            isLoading = false;
                                            isLastPage = true;
                                            tv_noResult.setVisibility(View.GONE);
                                        } else tv_noResult.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        if (!reportSearchAdapter2.isEmpty()) {
                                            page = "";
                                            reportSearchAdapter2.removeLoadingFooter();
                                            isLoading = false;
                                            isLastPage = true;
                                            tv_noResult.setVisibility(View.GONE);
                                        } else tv_noResult.setVisibility(View.VISIBLE);
                                    }
                                }

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

    private void parseFundReportData(JSONArray jsonArray, int type) {


        ArrayList<FundReport> reportList = new ArrayList<>();
        ReportJsonParser jsonParser = new ReportJsonParser(jsonArray);
        try {
            reportList = jsonParser.parseFundRequest(reportTypes);
        } catch (JSONException e) {
            e.printStackTrace();
            main_progressbar.setVisibility(View.GONE);
        }

        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            fundReportAdapter.addAll(reportList);

            if (currentPage <= total_page) fundReportAdapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            fundReportAdapter.removeLoadingFooter();
            isLoading = false;
            fundReportAdapter.addAll(reportList);

            if (currentPage != total_page)
                fundReportAdapter.addLoadingFooter();
            else isLastPage = true;


        }


    }

    private void parseReportData(JSONArray jsonArray, int type) {

        ArrayList<Report> reportList = new ArrayList<>();

        ReportJsonParser parser = new ReportJsonParser(jsonArray);
        try {
            reportList = parser.parseReportData(reportTypes);
        } catch (JSONException e) {
            e.printStackTrace();
            main_progressbar.setVisibility(View.GONE);
        }

        if(reportAdapterType ==1){
            if (type == 1) {
                main_progressbar.setVisibility(View.GONE);
                reportSearchAdapter.addAll(reportList);

                if (currentPage <= total_page) reportSearchAdapter.addLoadingFooter();
                else isLastPage = true;
            } else if (type == 2) {
                reportSearchAdapter.removeLoadingFooter();
                isLoading = false;
                reportSearchAdapter.addAll(reportList);

                if (currentPage != total_page)
                    reportSearchAdapter.addLoadingFooter();
                else isLastPage = true;


            }
        }
        else{
            if (type == 1) {
                main_progressbar.setVisibility(View.GONE);
                reportSearchAdapter2.addAll(reportList);

                if (currentPage <= total_page) reportSearchAdapter2.addLoadingFooter();
                else isLastPage = true;
            } else if (type == 2) {
                reportSearchAdapter2.removeLoadingFooter();
                isLoading = false;
                reportSearchAdapter2.addAll(reportList);

                if (currentPage != total_page)
                    reportSearchAdapter2.addLoadingFooter();
                else isLastPage = true;


            }
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
        bundle.putString("search_type", search_type);
        bundle.putBoolean("isDate", isDate);
        bundle.putBoolean("isFundRequest", isFundRequest);
        bundle.putString("searchInput", searchInput);
        bundle.putString("strStatus", strStatus);
        bundle.putString("strProduct", strProduct);
    }


    @Override
    public void onDatePick(String date) {
        if (datePickerType == DatePickerType.START_DATE) {
            date_from = date;
            tv_start_date.setText(date);
        } else {
            date_to = date;
            tv_end_date.setText(date);
        }

    }
}
