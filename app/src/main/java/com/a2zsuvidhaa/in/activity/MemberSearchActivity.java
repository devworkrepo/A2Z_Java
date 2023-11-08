package com.a2zsuvidhaa.in.activity;

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
import com.a2zsuvidhaa.in.adapter.MemberAdapter;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.listener.WebApiCallListener;
import com.a2zsuvidhaa.in.model.Member;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.WebApiCall;
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

public class MemberSearchActivity extends AppCompatActivity {

    private String search_url;
    private String search_url2;
    private TextView tv_noResult;

    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private MemberAdapter adapter;


    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private String page = "";
    private String userType = "";
    private String searchType = "Name";
    private String searchInput = "NAME";
    private String searchFor = "";

    private HashMap<String, String> searchTypeHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_search);


        search_url = getIntent().getStringExtra("search_url");
        searchType = getIntent().getStringExtra("searchType");
        searchInput = getIntent().getStringExtra("searchInput");
        userType = getIntent().getStringExtra("user_type");
        searchFor = getIntent().getStringExtra("search_for");


        tv_noResult = findViewById(R.id.tv_noResult);


        if (savedInstanceState != null) {
            searchType = savedInstanceState.getString("searchType");
            searchInput = savedInstanceState.getString("searchInput");
            userType = savedInstanceState.getString("user_type");
            searchFor = savedInstanceState.getString("search_for");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

        if(!searchFor.equals("")){
            toolbar.setTitle(searchFor +" Search");
        }
        else    toolbar.setTitle("Member Search");


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        searchTypeHashMap.put("Name", "NAME");
        searchTypeHashMap.put("Mobile", "MOB");
        searchTypeHashMap.put("ID", "ID");


        main_progressbar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        setRecyclerViewAdapter();

        getReportSearchData(1);

    }

    private void setRecyclerViewAdapter() {


        adapter = new MemberAdapter(this);
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

            Dialog dialog = AppDialogs.searchReport1(this);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            LinearLayout ll_date = dialog.findViewById(R.id.ll_date);
            ll_date.setVisibility(View.GONE);

            EditText ed_searchInput = dialog.findViewById(R.id.ed_searchType);
            Spinner spn_search = dialog.findViewById(R.id.spn_searchType);
            setupSearchTypeSpn(spn_search);

            btn_cancel.setOnClickListener(view3 -> {
                dialog.dismiss();
            });
            btn_search.setOnClickListener(view4 -> {

                if (!ed_searchInput.getText().toString().isEmpty()) {
                    recreate();
                    dialog.dismiss();
                } else MakeToast.show(this, "Enter search input ");


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


    private void setupSearchTypeSpn(Spinner spinner) {
        String[] prepaidStrings = searchTypeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchType = searchTypeHashMap.get(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getReportSearchData(int type) {

        String symbol = "&";
        if(!searchFor.equals("")){
            symbol = "?";
        }

        if (type == 1)
            search_url2 = search_url + symbol+"searchType=" + searchType + "&searchInput=" + searchInput + "&type=" + userType;
        if (type == 2) {
            search_url2 = search_url +symbol+ "searchType=" + searchType + "&searchInput=" + searchInput + "&" + page + "&type=" + userType;
        } else main_progressbar.setVisibility(View.VISIBLE);


        if (!searchFor.equals("")) {
            WebApiCall.getRequestWithHeader(this, search_url2);
        } else WebApiCall.getRequest(this, search_url2);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                try {
                    main_progressbar.setVisibility(View.GONE);
                    String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("1")) {
                        int count = jsonObject.getInt("count");


                        if (count > 0) {
                            total_page += 1;
                            page = jsonObject.optString("page");
                            JSONArray jsonArray = jsonObject.getJSONArray("members");
                            parseArray(jsonArray, type);
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
                        String message = jsonObject.optString("message");
                        Intent intent = new Intent(MemberSearchActivity.this, AppInProgressActivity.class);
                        intent.putExtra("message", message);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    } else if (status.equalsIgnoreCase("300")) {
                        String message = jsonObject.optString("message");
                        Intent intent = new Intent(MemberSearchActivity.this, AppInProgressActivity.class);
                        intent.putExtra("message", message);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    }


                } catch (JSONException e) {
                    main_progressbar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(String message) {
                main_progressbar.setVisibility(View.GONE);
            }
        });


    }


    private void parseArray(JSONArray jsonArray, int type) {

        ArrayList<Member> memberList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.optString("id");
                String name = jsonObject.optString("name");
                if(name.isEmpty()){
                    name = jsonObject.optString("userDetails");
                }
                String balance = jsonObject.optString("balance");
                String status = jsonObject.optString("status");
                String shopName = jsonObject.optString("shopName");
                String email = jsonObject.optString("email");
                String mobile = jsonObject.optString("mobile");
                String status_id = jsonObject.optString("statusId");
                String parentDetails = jsonObject.optString("parentDetails");
                String prefix = jsonObject.optString("prefix");


                Member member = new Member(id, name, balance, status, shopName,
                        email, mobile, status_id, parentDetails, prefix);
                memberList.add(member);

            } catch (JSONException e) {
                main_progressbar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }


        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            adapter.addAll(memberList);

            if (currentPage <= total_page) adapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(memberList);

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
        bundle.putString("searchType", searchType);
        bundle.putString("searchInput", searchInput);
        bundle.putString("user_type", userType);
        bundle.putString("search_for", searchFor);
    }

}
