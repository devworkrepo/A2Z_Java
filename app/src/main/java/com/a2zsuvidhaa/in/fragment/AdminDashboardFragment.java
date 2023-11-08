package com.a2zsuvidhaa.in.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class AdminDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboard";

    //balances
    private TextView tv_totalBalance;
    private TextView tv_superAdminBalance;
    private TextView tv_superAdminCount;
    private TextView tv_distributorBalance;
    private TextView tv_distributorCount;
    private TextView tv_retailerBalance;
    private TextView tv_retailerCount;
    //transaction status
    private TextView tv_successBalance;
    private TextView tv_successCount;
    private TextView tv_pendingBalance;
    private TextView tv_pendingCount;
    private TextView tv_failedBalance;
    private TextView tv_failedCount;
    private TextView tv_refundedBalance;
    private TextView tv_refundedCount;

    private ImageButton btn_refresh;
    private ProgressBar progressBar;
    private TextView tv_incorrect;


    public AdminDashboardFragment() {
    }

    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        getData();
        btn_refresh.setOnClickListener(view1->{
            setProgressVisibility(false);
            tv_incorrect.setVisibility(View.GONE);
            getData();
        });
    }

    private void init(View view) {

        tv_totalBalance = view.findViewById(R.id.tv_total_balance);
        tv_superAdminBalance = view.findViewById(R.id.tv_superAdminBalance);
        tv_superAdminCount = view.findViewById(R.id.tv_superAdminCount);
        tv_distributorBalance = view.findViewById(R.id.tv_distributorBalance);
        tv_distributorCount = view.findViewById(R.id.tv_distributorCount);
        tv_retailerBalance = view.findViewById(R.id.tv_retailerBalance);
        tv_retailerCount = view.findViewById(R.id.tv_retailerCount);

        tv_successBalance = view.findViewById(R.id.tv_successBalance);
        tv_successCount = view.findViewById(R.id.tv_succesCount);
        tv_pendingBalance = view.findViewById(R.id.tv_pendingBalance);
        tv_pendingCount = view.findViewById(R.id.tv_pendingCount);
        tv_failedBalance = view.findViewById(R.id.tv_failedBalance);
        tv_failedCount = view.findViewById(R.id.tv_failedCount);
        tv_refundedBalance = view.findViewById(R.id.tv_refundedBalance);
        tv_refundedCount = view.findViewById(R.id.tv_refundedCount);

        progressBar = view.findViewById(R.id.progressBar);
        tv_incorrect = view.findViewById(R.id.tv_incorrect);
        btn_refresh = view.findViewById(R.id.btn_refresh);
    }

    private void getData() {

        String url = APIs.ADMIN_DASHBOARD
                + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken();

        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {

                            tv_totalBalance.setText(object.getString("distributed_balance"));
                            JSONArray balanceArray = object.getJSONArray("balanceData");

                            for(int i = 0;i<balanceArray.length();i++){
                                JSONObject jsonObject = balanceArray.getJSONObject(i);
                                if(jsonObject.getString("roleName").equalsIgnoreCase("Super Admin")){
                                    tv_superAdminBalance.setText(jsonObject.getString("amount"));
                                    tv_superAdminCount.setText("["+jsonObject.getString("id")+"]");
                                }
                                else if(jsonObject.getString("roleName").equalsIgnoreCase("Distributor")){
                                    tv_distributorBalance.setText(jsonObject.getString("amount"));
                                    tv_distributorCount.setText("["+jsonObject.getString("id")+"]");
                                }
                                else if(jsonObject.getString("roleName").equalsIgnoreCase("Retailer")){
                                    tv_retailerBalance.setText(jsonObject.getString("amount"));
                                    tv_retailerCount.setText("["+jsonObject.getString("id")+"]");
                                }

                            }

                            JSONObject jsonObject = object.getJSONObject("data");
                            tv_successBalance.setText(jsonObject.getString("successVolume"));
                            tv_successCount.setText("["+jsonObject.getString("successTxnCount")+"]");
                            tv_pendingBalance.setText(jsonObject.getString("pendingVolumeToday"));
                            tv_pendingCount.setText("["+jsonObject.getString("pendingTxnCountToday")+"]");
                            tv_failedBalance.setText(jsonObject.getString("failVolume"));
                            tv_failedCount.setText("["+jsonObject.getString("failTxnCount")+"]");
                            tv_refundedBalance.setText(jsonObject.getString("refundedVolume"));
                            tv_refundedCount.setText("["+jsonObject.getString("refundedTxnCount")+"]");




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

    private void setProgressVisibility(boolean visibility){
        if(visibility){
            btn_refresh.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.VISIBLE);
            btn_refresh.setVisibility(View.GONE);

        }
    }
}
