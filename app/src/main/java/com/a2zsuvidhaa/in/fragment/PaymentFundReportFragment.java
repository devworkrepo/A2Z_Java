package com.a2zsuvidhaa.in.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.a2zsuvidhaa.in.AppPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.PaymentSearchActivity;
import com.a2zsuvidhaa.in.adapter.PaymentFundReportAdapter;
import com.a2zsuvidhaa.in.util.enums.DatePickerType;
import com.a2zsuvidhaa.in.listener.OnDatePicker;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.PaymentReport;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.DatePicker;
import com.a2zsuvidhaa.in.util.enums.PaymentReportType;
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
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class PaymentFundReportFragment extends Fragment implements OnDatePicker {


    private final static String TAG = "PaymentReportFragment";
    public static final String PAYMENT_REPORT_TYPE = "report_type";
    private PaymentReportType paymentReportType;
    private HashMap<String,String> userHashmap = new HashMap<>();

    public PaymentFundReportFragment() { }

    public static PaymentFundReportFragment newInstance(PaymentReportType paymentReportType) {
        PaymentFundReportFragment fragment = new PaymentFundReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(PAYMENT_REPORT_TYPE, paymentReportType);
        fragment.setArguments(args);
        return fragment;
    }
    private String page = "";
    private String search_url;

    String start_date="";
    String end_date="";

    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton btn_search;
    private PaymentFundReportAdapter adapter;

    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private TextView tv_noResult;

    private DatePickerType datePickerType;
    private TextView tv_start_date;
    private TextView tv_end_date;


    private String user;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paymentReportType = (PaymentReportType) getArguments().getSerializable(PAYMENT_REPORT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_retailer_fund_transfer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        main_progressbar = view.findViewById(R.id.progressBar);

        tv_noResult = view.findViewById(R.id.tv_noResult);
        btn_search = view.findViewById(R.id.btn_search);
        adapter = new PaymentFundReportAdapter(getActivity(),paymentReportType);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getPaymentReport(2);
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
        getPaymentReport(1);

        btn_search.setOnClickListener(view1->{

            start_date = "";
            end_date="";
            Spinner spn_user;
            Dialog dialog = AppDialogs.searchPaymentFundReport(getActivity());
            spn_user = dialog.findViewById(R.id.spn_user);
            tv_start_date = dialog.findViewById(R.id.tv_start_date);
            tv_end_date = dialog.findViewById(R.id.tv_end_date);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            LinearLayout ll_fund_user = dialog.findViewById(R.id.ll_fund_user);

            String[] prepaidStrings = userHashmap.keySet().toArray(new String[0]);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_layout, prepaidStrings);
            dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
            spn_user.setAdapter(dataAdapter);

            spn_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    user = userHashmap.get(spn_user.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if(paymentReportType == PaymentReportType.PAYMENT_REPORT)
                ll_fund_user.setVisibility(View.GONE);



            tv_start_date.setOnClickListener(view2->{
                datePickerType =  DatePickerType.START_DATE;
                DatePicker.datePicker(getActivity());
            });

            tv_end_date.setOnClickListener(view2 -> {
                datePickerType =  DatePickerType.END_DATE;
                DatePicker.datePicker(getActivity());
            });

            btn_cancel.setOnClickListener(view3-> dialog.dismiss());
            btn_search.setOnClickListener(view4->{

                if(!start_date.isEmpty() && !end_date.isEmpty()){
                    Intent intent = new Intent(getActivity(), PaymentSearchActivity.class);
                    intent.putExtra("date_from",start_date);
                    intent.putExtra("date_to",end_date);
                    intent.putExtra("user",user);
                    intent.putExtra("search_url",search_url);
                    intent.putExtra(PAYMENT_REPORT_TYPE,paymentReportType);
                    startActivity(intent);
                    dialog.dismiss();
                }
                else MakeToast.show(getActivity(),"Select start and end date");
            });

            dialog.show();



        });

        DatePicker.setupOnDatePicker(this);

    }


    private void getPaymentReport(int type) {

        String final_url;
        if(paymentReportType == PaymentReportType.PAYMENT_REPORT){
            final_url = APIs.PAYMENT_REPORT
                    + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                    + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken();
        }else{
            final_url = APIs.FUND_TRANSFER_REPORT
                    + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                    + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken();
        }

        search_url = final_url;
        if (type == 2) {
            final_url = final_url + "&" + page;
        } else main_progressbar.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.GET,
                final_url,
                response -> {
                    try {
                        main_progressbar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if(status.equalsIgnoreCase("1")) {
                            int count = jsonObject.getInt("count");
                            if(paymentReportType == PaymentReportType.FUND_TRANSFER_REPORT){
                                try{
                                    userHashmap.clear();
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("users");
                                    Iterator iterator = jsonObject1.keys();
                                    while (iterator.hasNext()) {
                                        String key = (String) iterator.next();
                                        userHashmap.put(jsonObject1.getString(key), key);
                                    }

                                }catch (Exception e){
                                }
                            }

                            if (count > 0) {
                                total_page += 1;
                                page = jsonObject.getString("page");
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
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
                        }else if(status.equalsIgnoreCase("200")){
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

        ArrayList<PaymentReport> reports = new ArrayList<>();

            try {
                ReportJsonParser parser = new ReportJsonParser(jsonArray);
                reports = parser.parsePaymentReport(paymentReportType);

            } catch (JSONException e) {
                main_progressbar.setVisibility(View.GONE);
                e.printStackTrace();
            }

        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            adapter.addAll(reports);

            if (currentPage <= total_page) adapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(reports);

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
    @Override
    public void onDatePick(String date) {
        if(datePickerType == DatePickerType.START_DATE){
            start_date = date;
            tv_start_date.setText(date);
        }
        else{
            end_date = date;
            tv_end_date.setText(date);
        }
    }
}
