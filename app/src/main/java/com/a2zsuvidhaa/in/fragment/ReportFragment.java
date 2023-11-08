package com.a2zsuvidhaa.in.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.DmtTransactionResponseActivity;
import com.a2zsuvidhaa.in.activity.AadharTransactionResponseActivity;
import com.a2zsuvidhaa.in.listener.WebApiCallListener;
import com.a2zsuvidhaa.in.util.AppConstants;
import com.a2zsuvidhaa.in.util.WebApiCall;
import com.a2zsuvidhaa.in.util.dialogs.StatusDialog;
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

import com.a2zsuvidhaa.in.adapter.ReportAdapter2;
import com.a2zsuvidhaa.in.util.enums.DatePickerType;
import com.a2zsuvidhaa.in.util.enums.ReportTypes;
import com.a2zsuvidhaa.in.listener.OnDatePicker;
import com.a2zsuvidhaa.in.util.DatePicker;
import com.a2zsuvidhaa.in.util.ReportJsonParser;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.ReportSearchActivity;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.Report;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.adapter.ReportAdapter;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AppUitls;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ReportFragment extends Fragment implements OnDatePicker {

    public static final String REPORT_TYPE = "report_type";
    private ReportTypes reportType = ReportTypes.LEDGER_REPORT;
    private final static String TAG = "ReportFragment";
    private HashMap<String, String> searchTypeHashMap = new HashMap<>();

    private HashMap<String, String> orderStatusHashMap = new HashMap<>();
    private HashMap<String, String> orderProductHashMap = new HashMap<>();
    private HashMap<String, String> creditDebitHashMap = new HashMap<>();

    private String strSearchType = "Date";
    private String strStatus = "";
    private String strProduct = "";

    public ReportFragment() {
    }

    public static ReportFragment newInstance(ReportTypes reportTypes) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(REPORT_TYPE, reportTypes);

        fragment.setArguments(args);
        return fragment;
    }

    private String page = "";
    private String search_url;

    String start_date = "";
    String end_date = "";

    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton btn_search;
    private ReportAdapter adapter;
    private ReportAdapter2 adapter2;

    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private TextView tv_noResult;

    private DatePickerType datePickerType;
    private TextView tv_start_date;
    private TextView tv_end_date;
    private int adapterType =1;


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

        main_progressbar = view.findViewById(R.id.progressBar);
        tv_noResult = view.findViewById(R.id.tv_noResult);

        //search hashMap
        searchTypeHashMap.put("Record ID", "RECORD_ID");
        searchTypeHashMap.put("Txn ID", "TXN_ID");
        searchTypeHashMap.put("Account No", "ACCOUNT_NO");
        searchTypeHashMap.put("Mobile Number", "MOB_NO");
        searchTypeHashMap.put("Date", "DATE");

        creditDebitHashMap.put("CREDIT","CREDIT");
        creditDebitHashMap.put("DEBIT","DEBIT");

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
        btn_search = view.findViewById(R.id.btn_search);

        if(reportType == ReportTypes.ACCOUNT_STATEMENT || reportType == ReportTypes.NETWORK_RECHARGE)
            adapterType = 2;
        else adapterType =1;

        if (adapterType==2)
            adapter2 = new ReportAdapter2(getActivity(), reportType);
        else {
            adapter = new ReportAdapter(getActivity(), reportType);
            adapter.setupListener(this::fetchReceiptData);
        }

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (adapterType==2)
            recyclerView.setAdapter(adapter2);
        else recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getReports(2);

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

        getReports(1);

        btn_search.setOnClickListener(view1 -> {


            start_date = "";
            end_date = "";

            Dialog dialog = AppDialogs.searchReport1(getActivity());
            tv_start_date = dialog.findViewById(R.id.tv_start_date);
            tv_end_date = dialog.findViewById(R.id.tv_end_date);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            LinearLayout ll_date = dialog.findViewById(R.id.ll_date);

            LinearLayout ll_product = dialog.findViewById(R.id.ll_product);
            LinearLayout ll_status = dialog.findViewById(R.id.ll_status);
            Spinner spn_status = dialog.findViewById(R.id.spn_status);
            Spinner spn_product = dialog.findViewById(R.id.spn_product);

            setupStatus(spn_status);
            setupProduct(spn_product);
            EditText ed_searchInput = dialog.findViewById(R.id.ed_searchType);
            Spinner spn_search = dialog.findViewById(R.id.spn_searchType);
            setupSearchTypeSpn(spn_search, ed_searchInput, ll_date, ll_product, ll_status);

            tv_start_date.setOnClickListener(view2 -> {
                datePickerType = DatePickerType.START_DATE;
                DatePicker.datePicker(getActivity());
            });

            tv_end_date.setOnClickListener(view2 -> {
                datePickerType = DatePickerType.END_DATE;
                DatePicker.datePicker(getActivity());
            });

            btn_cancel.setOnClickListener(view3 -> dialog.dismiss());
            btn_search.setOnClickListener(view4 -> {

                Intent intent = new Intent(getActivity(), ReportSearchActivity.class);
                intent.putExtra(REPORT_TYPE, reportType);
                if (strSearchType.equalsIgnoreCase("Date")) {
                    if (!start_date.isEmpty() && !end_date.isEmpty()) {
                        intent.putExtra("type", strSearchType);
                        intent.putExtra("date_from", start_date);
                        intent.putExtra("date_to", end_date);
                        intent.putExtra("statusOf", strStatus);
                        intent.putExtra("productOf", strProduct);
                        intent.putExtra("search_url", search_url);
                        startActivity(intent);
                        dialog.dismiss();
                    } else MakeToast.show(getActivity(), "Select start and end date");
                } else {
                    if (!ed_searchInput.getText().toString().isEmpty()) {
                        intent.putExtra("type", strSearchType);
                        intent.putExtra("searchInput", ed_searchInput.getText().toString());
                        intent.putExtra("search_url", search_url);
                        startActivity(intent);
                        dialog.dismiss();

                    } else MakeToast.show(getActivity(), "Enter search input ");
                }


            });

            dialog.show();
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        DatePicker.setupOnDatePicker(this);
    }

    private void setupSearchTypeSpn(Spinner spinner, EditText ed_searchInput,
                                    LinearLayout ll_date, LinearLayout ll_product, LinearLayout ll_status) {
        String[] prepaidStrings = searchTypeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireActivity(),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);

        if (reportType == ReportTypes.ACCOUNT_STATEMENT) {
            int position = dataAdapter.getPosition("Date");
            spinner.setSelection(position);
            spinner.setEnabled(false);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSearchType = searchTypeHashMap.get(spinner.getSelectedItem().toString());

                if (spinner.getSelectedItem().toString().equalsIgnoreCase("Date")) {
                    ll_date.setVisibility(View.VISIBLE);
                    ed_searchInput.setVisibility(View.GONE);
                    if (reportType == ReportTypes.LEDGER_REPORT) {
                        ll_status.setVisibility(View.VISIBLE);
                        ll_product.setVisibility(View.VISIBLE);
                    } else if (ReportTypes.USAGE_REPORT == reportType) {
                        ll_status.setVisibility(View.VISIBLE);
                        ll_product.setVisibility(View.GONE);
                    }

                } else {
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void setupStatus(Spinner spinner) {
        String[] prepaidStrings = orderStatusHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireActivity(),
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireActivity(),
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


    private void getReports(int type) {
        String final_report_url = APIs.LEDGER_REPORT;
        if (reportType == ReportTypes.LEDGER_REPORT)
            final_report_url = APIs.LEDGER_REPORT;
        else if (reportType == ReportTypes.USAGE_REPORT)
            final_report_url = APIs.USAGE_REPORT;
        else if (reportType == ReportTypes.ACCOUNT_STATEMENT)
            final_report_url = APIs.ACCOUNT_STATEMENT_REPORT;
        else if (reportType == ReportTypes.NETWORK_RECHARGE)
            final_report_url = APIs.NETWORK_RECHARGE_D;


        final_report_url = final_report_url
                + "?" + APIs.USER_TAG + "=" + AppPreference.getInstance(getActivity()).getId()
                + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(getActivity()).getToken();

        search_url = final_report_url;
        if (type == 2) {
            final_report_url = final_report_url + "&" + page;
        } else main_progressbar.setVisibility(View.VISIBLE);


        WebApiCall.getRequest(requireContext(),final_report_url);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                try {
                    main_progressbar.setVisibility(View.GONE);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("1")) {

                        int count = jsonObject.getInt("count");
                        if (count > 0) {
                            total_page += 1;
                            page = jsonObject.getString("page");
                            JSONArray jsonArray = jsonObject.getJSONArray("reports");
                            parseData(jsonArray, type);
                        } else {
                            page = "";
                            if (adapterType==2)
                                adapter2.removeLoadingFooter();
                            else adapter.removeLoadingFooter();
                            isLoading = false;
                            isLastPage = true;
                            int adapterItemCount = 0;
                            if (adapterType==2)
                                adapterItemCount = adapter2.getItemCount();
                            else adapterItemCount = adapter.getItemCount();



                            if (adapterItemCount > 0) {
                                tv_noResult.setVisibility(View.GONE);
                            } else tv_noResult.setVisibility(View.VISIBLE);
                        }
                    } else if (status.equalsIgnoreCase("200")) {
                        String message = jsonObject.getString("message");
                        Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                        intent.putExtra("message", message);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    } else if (status.equalsIgnoreCase("300")) {
                        String message = jsonObject.getString("message");
                        Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
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

    private void parseData(JSONArray jsonArray, int type) {

        ArrayList<Report> reportList = new ArrayList<>();
        ReportJsonParser parser = new ReportJsonParser(jsonArray);
        try {
            reportList = parser.parseReportData(reportType);
        } catch (JSONException e) {
            MakeToast.show(getActivity(),e.getMessage());
            e.printStackTrace();
            main_progressbar.setVisibility(View.GONE);
        }

        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            if (adapterType==2)
                adapter2.addAll(reportList);
            else  adapter.addAll(reportList);


            if(adapterType==2){
                if (currentPage <= total_page) adapter2.addLoadingFooter();
                else isLastPage = true;
            }else{
                if (currentPage <= total_page) adapter.addLoadingFooter();
                else isLastPage = true;
            }
        } else if (type == 2) {
           if(adapterType==2){
               adapter2.removeLoadingFooter();
               isLoading = false;
               adapter2.addAll(reportList);

               if (currentPage != total_page) adapter2.addLoadingFooter();
               else isLastPage = true;
           }else{
               adapter.removeLoadingFooter();
               isLoading = false;
               adapter.addAll(reportList);

               if (currentPage != total_page) adapter.addLoadingFooter();
               else isLastPage = true;
           }


        }

      if(adapterType==2){
          if (adapter2.getItemCount() > 0) {
              tv_noResult.setVisibility(View.GONE);
          } else {
              tv_noResult.setVisibility(View.VISIBLE);
          }
      }else{
          if (adapter.getItemCount() > 0) {
              tv_noResult.setVisibility(View.GONE);
          } else {
              tv_noResult.setVisibility(View.VISIBLE);
          }
      }
    }


    @Override
    public void onDatePick(String date) {
        if (datePickerType == DatePickerType.START_DATE) {
            start_date = date;
            tv_start_date.setText(date);
        } else {
            end_date = date;
            tv_end_date.setText(date);
        }

    }

    private void fetchReceiptData(Report report ){

        Dialog dialog = StatusDialog.INSTANCE.progress(requireContext(),"Fetching...");

        String url = APIs.DOWNLOAD_RECEIPT + "/"+report.getId();

        if(report.getApiId().equals("58") || report.getApiId().equals("10") || report.getApiId().equals("28")){

            url = APIs.DOWNLOAD_RECEIPT_AEPS_THREE+"/"+report.getTxnId();

        }



        WebApiCall.getRequestWithHeader(requireContext(),url);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                dialog.dismiss();
                try {
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.optString("message");

                    if(status ==1){
                        String slipType = jsonObject.getString("slip_type");
                     //   BILLPAYMENT,INSURANCE,LOAN_REPAYMENT,RECHARGE_POSTPAID,FASTTAG,RELOAD_CARD,AEPS,ONESHOT_DMT,PIECE_DMT,OTHER


                       if(slipType.equals("BILLPAYMENT")

                       ){

                       }
                   /*    else if(slipType.equals("AEPS")){
                           Intent intent = new  Intent(requireActivity(), AadharTransactionResponseActivity.class);
                           intent.putExtra(AppConstants.DATA, jsonObject.toString());
                           intent.putExtra(AppConstants.ORIGIN, AppConstants.REPORT);
                           intent.putExtra(AppConstants.SERVICE_TYPE, AppConstants.AEPS);
                           requireActivity().startActivity(intent);
                       }*/
                       else if(slipType.equals("ONESHOT_DMT") || slipType.equals("PIECE_DMT")){

                           String dmtType;
                           if(slipType.equals("ONESHOT_DMT")) dmtType = AppConstants.ONE_SHOT_DMT;
                           else dmtType =  AppConstants.PIECE_DMT;

                           Intent intent = new  Intent(requireActivity(), DmtTransactionResponseActivity.class);
                           intent.putExtra(AppConstants.DATA, jsonObject.toString());
                           intent.putExtra(AppConstants.ORIGIN, AppConstants.REPORT_ORIGIN);
                           intent.putExtra(AppConstants.SERVICE_TYPE,dmtType );
                           requireActivity().startActivity(intent);

                       }
                       else  if(report.getApiId().equals("58") || report.getApiId().equals("10") || report.getApiId().equals("28")){

                           Intent intent = new Intent(requireActivity(), AadharTransactionResponseActivity.class);
                           intent.putExtra(AppConstants.DATA, jsonObject.toString());
                           intent.putExtra(AppConstants.ORIGIN, AppConstants.REPORT_ORIGIN);
                           requireActivity().startActivity(intent);
                       }

                    }else MakeToast.show(requireActivity(),message);

                }catch (Exception e){
                    MakeToast.show(requireActivity(),e.getMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                dialog.dismiss();
                MakeToast.show(requireActivity(),message);
            }
        });


    }


}
