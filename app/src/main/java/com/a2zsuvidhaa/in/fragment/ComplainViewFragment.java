package com.a2zsuvidhaa.in.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.adapter.ComplainAdapter;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.Complain;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ComplainViewFragment extends Fragment {

    private final static String TAG = "ComplainViewFragment";

    public ComplainViewFragment() {
    }

    public static ComplainViewFragment newInstance() {
        return new ComplainViewFragment();
    }

    private String page = "";


    LinearLayoutManager linearLayoutManager;
    private ComplainAdapter adapter;

    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private TextView tv_noResult;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        main_progressbar = view.findViewById(R.id.progressBar);

        tv_noResult = view.findViewById(R.id.tv_noResult);
        adapter = new ComplainAdapter(getActivity());
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getComplains(2);

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
        getComplains(1);

    }


    private void getComplains(int type) {
        String final_url = APIs.VIEW_COMPLAIN
                + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken();

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

                        if (status.equalsIgnoreCase("1")) {
                            int count = jsonObject.getInt("count");

                            if (count > 0) {
                                total_page += 1;
                                page = jsonObject.getString("page");
                                JSONArray jsonArray = jsonObject.getJSONArray("complains");
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

        ArrayList<Complain> complainList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String user_id = jsonObject.getString("user_id");
                String created_at = jsonObject.getString("created_at");
                String issue_type = jsonObject.getString("issue_type");
                String txn_id = jsonObject.getString("txn_id");
                String looking_by = jsonObject.getString("looking_by");
                String approved_by = jsonObject.getString("approved_by");
                String status_id = jsonObject.getString("status_id");
                String status = jsonObject.getString("status");
                String approved_date = jsonObject.getString("approved_date");
                String remark = jsonObject.getString("remark");
                String current_status_remark = jsonObject.getString("current_status_remark");


                Complain member = new Complain(id, user_id, created_at, issue_type, txn_id,
                        looking_by, approved_by, status, status_id, approved_date,remark, current_status_remark);
                complainList.add(member);

            } catch (JSONException e) {
                main_progressbar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }


        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            adapter.addAll(complainList);

            if (currentPage <= total_page) adapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(complainList);

            if (currentPage != total_page) adapter.addLoadingFooter();
            else isLastPage = true;


        }
        if (adapter.getItemCount() > 0) {
            tv_noResult.setVisibility(View.GONE);
        } else {
            tv_noResult.setVisibility(View.VISIBLE);
        }
    }

}
