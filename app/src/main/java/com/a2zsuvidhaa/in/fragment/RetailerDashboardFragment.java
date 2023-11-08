package com.a2zsuvidhaa.in.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.model.Report;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RetailerDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboard";

    //sales
    private TextView tv_lmsCount;
    private TextView tv_lms;
    private TextView tv_tmsCount;
    private TextView tv_tms;
    private TextView tv_tsCount;
    private TextView tv_ts;
    //holiday
    private TextView tv_holiday1;
    private TextView tv_holidayDate1;
    private TextView tv_holiday2;
    private TextView tv_holidayDate2;
    private TextView tv_holiday3;
    private TextView tv_holidayDate3;
    private TextView tv_bankDown;


    private ImageButton btn_refresh;
    private ProgressBar progressBar;
    private TextView tv_incorrect;

    private RecyclerView recyclerView;

    public RetailerDashboardFragment() {
    }

    public static RetailerDashboardFragment newInstance() {
        return new RetailerDashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_retailer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        getData();
        btn_refresh.setOnClickListener(view1 -> {
            setProgressVisibility(false);
            tv_incorrect.setVisibility(View.GONE);
            getData();
        });
    }

    private void init(View view) {


        tv_lmsCount = view.findViewById(R.id.tv_lmsCount);
        tv_lms = view.findViewById(R.id.tv_lms);
        tv_tmsCount = view.findViewById(R.id.tv_tmsCount);
        tv_tms = view.findViewById(R.id.tv_tms);
        tv_tsCount = view.findViewById(R.id.tv_tsCount);
        tv_ts = view.findViewById(R.id.tv_ts);

        tv_holiday1 = view.findViewById(R.id.tv_holiday1);
        tv_holiday2 = view.findViewById(R.id.tv_holiday2);
        tv_holiday3 = view.findViewById(R.id.tv_holiday3);
        tv_holidayDate1 = view.findViewById(R.id.tv_holidayDate1);
        tv_holidayDate2 = view.findViewById(R.id.tv_holidayDate2);
        tv_holidayDate3 = view.findViewById(R.id.tv_holidayDate3);

        tv_bankDown = view.findViewById(R.id.tv_bankDown);


        progressBar = view.findViewById(R.id.progressBar);
        tv_incorrect = view.findViewById(R.id.tv_incorrect);
        btn_refresh = view.findViewById(R.id.btn_refresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
    }


    private void getData() {

        String url = APIs.RETAILER_DASHBOARD
                + "?" + APIs.USER_TAG + "=" + AppPreference.getInstance(getActivity()).getId()
                + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(getActivity()).getToken();
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {

                            String bankDown = object.getString("bankDownList");
                            if (!bankDown.equalsIgnoreCase(""))
                                tv_bankDown.setText(bankDown);

                            tv_bankDown.setSelected(true);
                            tv_bankDown.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            tv_bankDown.setSingleLine(true);
                            //lms
                            JSONObject lmsObject = object.getJSONObject("LMS");
                            tv_lms.setText(lmsObject.getString("totalVolume"));
                            tv_lmsCount.setText("[" + lmsObject.getString("txnCount") + "]");
                            //cms
                            JSONObject cmsObject = object.getJSONObject("CMS");
                            tv_tms.setText(cmsObject.getString("totalVolume"));
                            tv_tmsCount.setText("[" + cmsObject.getString("txnCount") + "]");
                            //ts
                            JSONObject tsObject = object.getJSONObject("TS");
                            tv_ts.setText(tsObject.getString("totalVolume"));
                            tv_tsCount.setText("[" + tsObject.getString("txnCount") + "]");

                            boolean hasHoliday = object.getBoolean("hasHoliday");
                            if (hasHoliday) {
                                JSONArray holidays = object.getJSONArray("holidays");
                                int size = holidays.length();
                                for (int i = 0; i < size; i++) {
                                    JSONObject holidayObject = holidays.getJSONObject(i);
                                    if (i == 0) {
                                        tv_holiday1.setText(holidayObject.getString("name"));
                                        tv_holidayDate1.setText(holidayObject.getString("holiday_date"));
                                    }

                                    if (i == 1) {
                                        tv_holiday2.setText(holidayObject.getString("name"));
                                        tv_holidayDate2.setText(holidayObject.getString("holiday_date"));
                                    }
                                    if (i == 2) {
                                        tv_holiday3.setText(holidayObject.getString("name"));
                                        tv_holidayDate3.setText(holidayObject.getString("holiday_date"));
                                    }
                                }
                            }

                            boolean hasReport = object.getBoolean("hasReport");
                            if (hasReport) {

                                ArrayList<Report> reports = new ArrayList<>();

                                JSONArray reportArray = object.getJSONArray("reports");
                                int size = reportArray.length();
                                for (int i = 0; i < size; i++) {
                                    JSONObject reportObject = reportArray.getJSONObject(i);
                                    Report report = new Report();
                                    report.setDate(reportObject.getString("created_at"));
                                    report.setOperator(reportObject.getString("operator"));
                                    report.setNumber(reportObject.getString("number"));
                                    report.setStatus(reportObject.getString("status"));
                                    report.setAmount(reportObject.getString("amount"));
                                    reports.add(report);
                                }

                                ReportAdapter adapter = new ReportAdapter(reports);

                                if (getActivity() != null) {
                                    final float scale = Objects.requireNonNull(getContext())
                                            .getResources().getDisplayMetrics().density;
                                    int pixels = (int) (80 * scale);
                                    recyclerView.getLayoutParams().height = reports.size() * pixels;
                                    recyclerView.setAdapter(adapter);
                                }
                            }


                        } else if (status.equalsIgnoreCase("200")) {
                            String message = object.getString("message");
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            String message = object.getString("message");
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        MakeToast.show(getActivity(), e.getMessage());
                        setProgressVisibility(true);
                        tv_incorrect.setText("Something went wrong!\ntry again");
                        tv_incorrect.setVisibility(View.VISIBLE);
                    }
                    setProgressVisibility(true);

                },
                error -> {
                    tv_incorrect.setText("Something went wrong!\ntry again");
                    tv_incorrect.setVisibility(View.VISIBLE);
                    setProgressVisibility(true);
                    MakeToast.show(getActivity(), "on error");
                }) {

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setProgressVisibility(boolean visibility) {
        if (visibility) {
            btn_refresh.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            btn_refresh.setVisibility(View.GONE);

        }
    }

    public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

        private ArrayList<Report> reportList;

        ReportAdapter(ArrayList<Report> reportList) {
            this.reportList = reportList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_report_dashboard, viewGroup,
                    false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            Report report = reportList.get(i);

            viewHolder.tv_date.setText(report.getDate());
            viewHolder.tv_operator.setText(report.getOperator());
            viewHolder.tv_amount.setText(report.getAmount());
            viewHolder.tv_number.setText(report.getNumber());
            viewHolder.tv_status.setText(report.getStatus());

            if (report.getStatus().equalsIgnoreCase("success") ||
                    report.getStatus().equalsIgnoreCase("complete") ||
                    report.getStatus().equalsIgnoreCase("active") ||
                    report.getStatus().equalsIgnoreCase("successfully submitted") ||
                    report.getStatus().equalsIgnoreCase("credit")) {
                viewHolder.tv_status.setTextColor(getActivity().getResources().getColor(R.color.success));
            } else if (
                    report.getStatus().equalsIgnoreCase("failure") ||
                            report.getStatus().equalsIgnoreCase("debit")) {
                viewHolder.tv_status.setTextColor(getActivity().getResources().getColor(R.color.red));
            } else {
                viewHolder.tv_status.setTextColor(getActivity().getResources().getColor(R.color.yellow_dark));
            }
        }

        @Override
        public int getItemCount() {
            return reportList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            TextView tv_date;
            TextView tv_operator;
            TextView tv_number;
            TextView tv_status;
            TextView tv_amount;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_date = itemView.findViewById(R.id.tv_date);
                tv_operator = itemView.findViewById(R.id.tv_operator);
                tv_number = itemView.findViewById(R.id.tv_number);
                tv_status = itemView.findViewById(R.id.tv_status);
                tv_amount = itemView.findViewById(R.id.tv_amount);

            }
        }

    }
}
