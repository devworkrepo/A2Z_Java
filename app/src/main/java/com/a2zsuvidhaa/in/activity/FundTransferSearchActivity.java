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
import com.a2zsuvidhaa.in.fragment.FundTransferFragment;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.adapter.FundTransferAdapter;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.FundTransfer;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FundTransferSearchActivity extends AppCompatActivity {


    private static final String TAG = "FundTransferSearch";
    private String search_url;
    private String search_url2;
    private String title = "Fund Transfer Search";
    public int AGENT_TYPE = 1;

    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private FundTransferAdapter adapter;


    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private String page = "";

    private TextView tv_noResult;

    private String search_type;
    private String search_input;
    private HashMap<String, String> searchTypeHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_search);


        if (!getIntent().getStringExtra("launch_origin").isEmpty()) {
            search_type = getIntent().getStringExtra("search_type");
            search_input = getIntent().getStringExtra("search_input");
            title = getIntent().getStringExtra("launch_origin");
            search_url = getIntent().getStringExtra("search_url");
            AGENT_TYPE = getIntent().getIntExtra("AGENT_TYPE",1);
        }

        if (savedInstanceState != null) {
            search_type = savedInstanceState.getString("search_type");
            search_input = savedInstanceState.getString("search_input");
        }
        tv_noResult = findViewById(R.id.tv_noResult);

        searchTypeHashMap.put("Name", "NAME");
        searchTypeHashMap.put("ID", "ID");
        searchTypeHashMap.put("Mobile Number", "MOB_NO");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title +" Search");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        main_progressbar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        setRecyclerViewAdapter();

        getReportSearchData(1);

    }

    private void setRecyclerViewAdapter() {

        adapter = new FundTransferAdapter(this);

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

        adapter.setupFundTransferListener(((fundTransfer,transfer_type) -> {


            Intent intent = new Intent(this, FundTransferActivity.class);

            intent.putExtra("AGENT_TYPE", AGENT_TYPE);
            intent.putExtra("transfer_type",transfer_type);
            intent.putExtra("fund_transfer",fundTransfer);
            this.startActivity(intent);
        }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {

            Dialog dialog = AppDialogs.searchFundTranser(this);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            EditText ed_searchInput = dialog.findViewById(R.id.ed_searchType);
            Spinner spn_search = dialog.findViewById(R.id.spn_searchType);
            setupSearchTypeSpn(spn_search, ed_searchInput);


            btn_cancel.setOnClickListener(view3 -> dialog.dismiss());
            btn_search.setOnClickListener(view4 -> {
                search_input = ed_searchInput.getText().toString();
                if (!search_input.isEmpty()) {
                    recreate();
                    dialog.dismiss();
                } else MakeToast.show(this, "Enter Search Input");

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


    private void getReportSearchData(int type) {

        String userType="";
        if(AGENT_TYPE == FundTransferFragment.DISTRIBUTOR){
            userType = "Distributor";
        }

        if (type == 1)
            search_url2 = search_url + "&SEARCH_TYPE=" + search_type + "&number=" + search_input+"&type="+userType;
        if (type == 2) {
            search_url2 = search_url + "&SEARCH_TYPE=" + search_type + "&number=" + search_input + "&" + page+"&type="+userType;
        } else main_progressbar.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.GET, search_url2,


                response -> {
                    try {
                        main_progressbar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if(status.equalsIgnoreCase("1")) {
                            int count = jsonObject.getInt("count");
                            if (count > 0) {
                                total_page += 1;
                                page = jsonObject.getString("page");
                                JSONArray jsonArray = jsonObject.getJSONArray("reports");
                                parseArray(jsonArray, type);
                            } else {
                                if (!adapter.isEmpty()) {
                                    page = "";
                                    adapter.removeLoadingFooter();
                                    isLoading = false;
                                    isLastPage = true;
                                    if (adapter.getItemCount() > 0) {
                                        tv_noResult.setVisibility(View.GONE);
                                    } else tv_noResult.setVisibility(View.VISIBLE);
                                } else tv_noResult.setVisibility(View.VISIBLE);
                            }
                        }else if(status.equalsIgnoreCase("200")){
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
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

        ArrayList<FundTransfer> fundTransferArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String idName = "";

                String id = jsonObject.getString("id");
                if (title.equalsIgnoreCase("Distributor Fund Transfer Search"))
                    idName = jsonObject.getString("prefix");
                String name = jsonObject.getString("name");
                String shopName = jsonObject.getString("shopName");
                String mobile = jsonObject.getString("mobile");
                String moneyBalance = jsonObject.getString("moneyBalance");


                FundTransfer fundTransfer = new FundTransfer(id, idName, name, shopName, mobile, moneyBalance);

                fundTransferArrayList.add(fundTransfer);


            } catch (JSONException e) {
                main_progressbar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }


        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            adapter.addAll(fundTransferArrayList);

            if (currentPage <= total_page) adapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(fundTransferArrayList);

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
        bundle.putString("search_type", search_type);
        bundle.putString("search_input", search_input);
    }

    private void setupSearchTypeSpn(Spinner spinner, EditText ed_searchInput) {
        String[] prepaidStrings = searchTypeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search_type = searchTypeHashMap.get(spinner.getSelectedItem().toString());
                if (spinner.getSelectedItem().toString().equalsIgnoreCase("Name")) {
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
