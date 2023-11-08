package com.a2zsuvidhaa.in.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.adapter.ServiceManagementAdapter;
import com.a2zsuvidhaa.in.model.ServiceManagement;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServiceManagementFragment extends Fragment {


    public ServiceManagementFragment() {
    }

    private TextView tv_error_hint;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ServiceManagementAdapter adapter;
    private ArrayList<ServiceManagement> serviceList;

    public static ServiceManagementFragment newInstance() {
        return new ServiceManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_service_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_error_hint = view.findViewById(R.id.tv_error_hint);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        serviceList = new ArrayList<>();


        getServiceList();

    }

    private void getServiceList() {
        progressBar.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.SERVICE_MANAGEMENT,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            JSONArray listArray = jsonObject.getJSONArray("list");

                            for(int i = 0;i<listArray.length();i++){
                                JSONObject finalObject = listArray.getJSONObject(i);

                                ServiceManagement serviceManagement = new ServiceManagement();
                                serviceManagement.setId(finalObject.getString("id"));
                                serviceManagement.setName(finalObject.getString("name"));
                                serviceManagement.setCategory(finalObject.getString("category"));
                                serviceManagement.setStatus(finalObject.getString("status_id"));
                                serviceManagement.setMessage(finalObject.getString("message"));


                                serviceList.add(serviceManagement);
                            }

                            adapter = new ServiceManagementAdapter(serviceList,getActivity());
                            recyclerView.setAdapter(adapter);




                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        }
                        else{
                            tv_error_hint.setVisibility(View.VISIBLE);
                        }


                    } catch (JSONException e) {

                        tv_error_hint.setVisibility(View.VISIBLE);
                        MakeToast.show(getActivity(), e.getMessage());

                    }


                    progressBar.setVisibility(View.GONE);
                },
                error -> {
                    tv_error_hint.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    MakeToast.show(getActivity(), "onError");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(getActivity()).getToken()));
                return param;
            }

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}
