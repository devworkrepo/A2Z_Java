package com.a2zsuvidhaa.in.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.FundTransferActivity;
import com.a2zsuvidhaa.in.activity.FundTransferSearchActivity;
import com.a2zsuvidhaa.in.adapter.FundTransferAdapter;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.FundTransfer;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class FundTransferFragment extends Fragment {


    public static final int DISTRIBUTOR = 1;
    public static final int RETAILER = 2;
    public int AGENT_TYPE = 1;

    private final static String TAG = "FundTransfer";
    private String strSearchType;

    public FundTransferFragment() { }

    public static FundTransferFragment newInstance(int type) {
        FundTransferFragment fragment = new FundTransferFragment();
        Bundle args = new Bundle();
        args.putInt("arg1",type);
        fragment.setArguments(args);
        return fragment;
    }
    private String page = "";
    private String search_url;


    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton btn_search;
    private FundTransferAdapter adapter;

    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private TextView tv_noResult;

    private HashMap<String,String> searchTypeHashMap= new HashMap<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            AGENT_TYPE = getArguments().getInt("arg1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fund_transfer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        searchTypeHashMap.put("Name","NAME");
        searchTypeHashMap.put("ID","ID");
        searchTypeHashMap.put("Mobile Number","MOB_NO");


        main_progressbar = view.findViewById(R.id.progressBar);
        tv_noResult = view.findViewById(R.id.tv_noResult);

        btn_search = view.findViewById(R.id.btn_search);

        adapter = new FundTransferAdapter(getActivity());


        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setupFundTransferListener(((fundTransfer,transfer_type) -> {

            Intent intent = new Intent(getActivity(), FundTransferActivity.class);

            intent.putExtra("AGENT_TYPE", AGENT_TYPE);
            intent.putExtra("transfer_type",transfer_type);
            intent.putExtra("fund_transfer",fundTransfer);
            requireActivity().startActivity(intent);
        }));


        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getLedgerData(2);

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
        getLedgerData(1);
        btn_search.setOnClickListener(view1->{


            Dialog dialog = AppDialogs.searchFundTranser(getActivity());
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            EditText ed_searchInput = dialog.findViewById(R.id.ed_searchType);
            Spinner spn_search = dialog.findViewById(R.id.spn_searchType);
            setupSearchTypeSpn(spn_search,ed_searchInput);



            btn_cancel.setOnClickListener(view3->dialog.dismiss());
            btn_search.setOnClickListener(view4->{
                String searchInput = ed_searchInput.getText().toString();
                if(!searchInput.isEmpty()) {
                    Intent intent = new Intent(getActivity(), FundTransferSearchActivity.class);
                    intent.putExtra("search_url", search_url);
                    intent.putExtra("search_type", strSearchType);
                    intent.putExtra("search_input", searchInput);
                    intent.putExtra("AGENT_TYPE", AGENT_TYPE);
                    intent.putExtra("launch_origin", "Fund Transfer");
                    startActivity(intent);
                    dialog.dismiss();
                }else MakeToast.show(getActivity(),"Enter Search Input");

            });

            dialog.show();


        });

    }

    private void getLedgerData(int type) {

        String userType="";
        if(AGENT_TYPE == DISTRIBUTOR){
            userType = "Distributor";
        }
        String final_report_url;
        if(/*AGENT_TYPE==DISTRIBUTOR*/ false) {
            final_report_url = APIs.DIST_FUND_LIST + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                    + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken();
        }else {
            final_report_url = APIs.RETAILER_FUND_TRANSFER + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                    + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken()+"&type="+userType;
        }

        search_url = final_report_url;
        if (type == 2) {
            final_report_url = final_report_url + "&" + page;
        } else main_progressbar.setVisibility(View.VISIBLE);

        final StringRequest request = new StringRequest(Request.Method.GET,
                final_report_url,
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
                                page = "";
                                adapter.removeLoadingFooter();
                                isLoading = false;
                                isLastPage = true;
                                if (adapter.getItemCount() > 0) {
                                    tv_noResult.setVisibility(View.GONE);
                                } else tv_noResult.setVisibility(View.VISIBLE);
                            }
                        }
                        else if(status.equalsIgnoreCase("200")){
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
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
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    private void parseArray(JSONArray jsonArray, int type) {

        ArrayList<FundTransfer> reportList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String idName="";

                String id = jsonObject.getString("id");
                if(AGENT_TYPE == DISTRIBUTOR)
                idName = jsonObject.getString("prefix");
                String name = jsonObject.getString("name");
                String shopName = jsonObject.getString("shopName");
                String mobile = jsonObject.getString("mobile");
                String moneyBalance = jsonObject.getString("moneyBalance");


                FundTransfer fundTransfer=new FundTransfer(id,idName,name,shopName,mobile,moneyBalance);

                reportList.add(fundTransfer);


            } catch (JSONException e) {
                main_progressbar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }


        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            adapter.addAll(reportList);

            if (currentPage <= total_page) adapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(reportList);

            if (currentPage != total_page) adapter.addLoadingFooter();
            else isLastPage = true;


        }

        if(adapter.getItemCount()>0){
            tv_noResult.setVisibility(View.GONE);
        }
        else{
            tv_noResult.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearchTypeSpn(Spinner spinner, EditText ed_searchInput){
        String[] prepaidStrings = searchTypeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireActivity(),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSearchType = searchTypeHashMap.get(spinner.getSelectedItem().toString());
                if(spinner.getSelectedItem().toString().equalsIgnoreCase("Name")){
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else{
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



}
