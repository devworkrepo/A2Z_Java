package com.a2zsuvidhaa.in.fragment;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a2zsuvidhaa.in.util.enums.ReportTypes;
import com.a2zsuvidhaa.in.model.FundReport;
import com.a2zsuvidhaa.in.util.ReportJsonParser;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.ReportSearchActivity;
import com.a2zsuvidhaa.in.adapter.FundReportAdapter;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class FundReportFragment
        extends Fragment {

    public static final String REPORT_TYPE = "report_type";
    private ReportTypes reportType = ReportTypes.FUND_REPORT;
    private final static String TAG = "FundReportFragment";
    private String page = "";
    private String search_url;
    private int year, month, day;
    Calendar myCalendar;

    String start_date="";
    String end_date="";
    private TextView tv_noResult;
    public FundReportFragment() {
    }

    public static FundReportFragment newInstance(ReportTypes reportTypes) {
        FundReportFragment fragment = new FundReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(REPORT_TYPE,reportTypes);
        fragment.setArguments(args);
        return fragment;
    }


    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton btn_search;
    private FundReportAdapter adapter;

    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportType = (ReportTypes) getArguments().getSerializable(REPORT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        myCalendar = Calendar.getInstance();
        year= myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);
        tv_noResult = view.findViewById(R.id.tv_noResult);

        main_progressbar = view.findViewById(R.id.progressBar);


        btn_search = view.findViewById(R.id.btn_search);
        adapter = new FundReportAdapter(getActivity(),reportType);
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
                getFundReportData(2);

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
        getFundReportData(1);
        btn_search.setOnClickListener(view1->{

            start_date = "";
            end_date="";

            Dialog dialog = AppDialogs.searchReport(getActivity());
            TextView tv_start_date = dialog.findViewById(R.id.tv_start_date);
            TextView tv_end_date = dialog.findViewById(R.id.tv_end_date);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);

            tv_start_date.setOnClickListener(view2->{
                datePicker(tv_start_date,1);
            });

            tv_end_date.setOnClickListener(view2 -> {
                datePicker(tv_end_date,2);
            });

            btn_cancel.setOnClickListener(view3->{
                dialog.dismiss();
            });
            btn_search.setOnClickListener(view4->{

                if(!start_date.isEmpty() && !end_date.isEmpty()){
                    Intent intent = new Intent(getActivity(), ReportSearchActivity.class);
                    intent.putExtra("report_type", reportType);
                    intent.putExtra("date_from",start_date);
                    intent.putExtra("date_to",end_date);
                    intent.putExtra("search_url",search_url);
                    startActivity(intent);
                    dialog.dismiss();
                }
                else MakeToast.show(getActivity(),"Select start and end date");
            });

            dialog.show();



        });

    }

    private void getFundReportData(int type) {

        String url = APIs.FUND_REPORT;
        if(reportType == ReportTypes.DT_FUND_REPORT)
            url = APIs.DIRECT_FUND_TRANSFER;

        String final_report_url = url + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken();

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
                        if(status.equalsIgnoreCase("1")){
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

        ArrayList<FundReport> reportList = new ArrayList<>();
        ReportJsonParser jsonParser = new ReportJsonParser(jsonArray);
        try {
            reportList = jsonParser.parseFundRequest(reportType);
        } catch (JSONException e) {
            e.printStackTrace();
            main_progressbar.setVisibility(View.GONE);
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

    private void datePicker(TextView tv_date, int type){

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                (view, year, monthOfYear, dayOfMonth) -> {

                    monthOfYear = monthOfYear+1;

                    String m= String.valueOf(monthOfYear);
                    String d = String.valueOf(dayOfMonth);

                    if(monthOfYear < 10){

                        m = ("0" +(monthOfYear));
                    }
                    if(dayOfMonth < 10){

                        d = ("0" + (dayOfMonth)) ;
                    }

                    tv_date.setText(d + "-" + (m) + "-" +year );
                    if(type ==1){
                        start_date = year + "-" + (m) + "-" +d;
                    }else {
                        end_date=year + "-" + (m) + "-" +d;
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
