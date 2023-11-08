package com.a2zsuvidhaa.in.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.listener.WebApiCallListener;
import com.a2zsuvidhaa.in.util.AppConstants;
import com.a2zsuvidhaa.in.util.WebApiCall;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.activity.MemberSearchActivity;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.adapter.MemberAdapter;
import com.a2zsuvidhaa.in.listener.PaginationScrollListener;
import com.a2zsuvidhaa.in.model.Member;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MemberFragment extends Fragment {


    String userType = "";
    String searchFor = "";

    public MemberFragment() {
    }

    public static MemberFragment newInstance(String type, String searchFor) {
        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("search_for", searchFor);
        fragment.setArguments(args);
        return fragment;
    }

    private String page = "";
    private String search_url;


    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton btn_search;
    private MemberAdapter adapter;

    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private TextView tv_noResult;
    private HashMap<String, String> searchTypeHashMap = new HashMap<>();
    private String searchType = "Name";


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userType = getArguments().getString("type");
            searchFor = getArguments().getString("search_for");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        searchTypeHashMap.put("Name", "NAME");
        searchTypeHashMap.put("Mobile", "MOB");
        searchTypeHashMap.put("ID", "ID");


        main_progressbar = view.findViewById(R.id.progressBar);

        tv_noResult = view.findViewById(R.id.tv_noResult);
        btn_search = view.findViewById(R.id.btn_search);
        adapter = new MemberAdapter(getActivity());
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
                getMembers(2);
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
        getMembers(1);
        btn_search.setOnClickListener(view1 -> {


            Dialog dialog = AppDialogs.searchReport1(getActivity());
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            LinearLayout ll_date = dialog.findViewById(R.id.ll_date);
            ll_date.setVisibility(View.GONE);

            EditText ed_searchInput = dialog.findViewById(R.id.ed_searchType);
            Spinner spn_search = dialog.findViewById(R.id.spn_searchType);
            setupSearchTypeSpn(spn_search);


            btn_cancel.setOnClickListener(view3 -> dialog.dismiss());
            btn_search.setOnClickListener(view4 -> {

                Intent intent = new Intent(getActivity(), MemberSearchActivity.class);

                if (!ed_searchInput.getText().toString().isEmpty()) {
                    intent.putExtra("searchType", searchType);
                    intent.putExtra("searchInput", ed_searchInput.getText().toString());
                    intent.putExtra("search_url", search_url);
                    intent.putExtra("user_type", userType);
                    intent.putExtra("search_for", searchFor);
                    startActivity(intent);
                    dialog.dismiss();
                } else MakeToast.show(getActivity(), "Enter search input ");

            });

            dialog.show();


        });

    }

    private void setupSearchTypeSpn(Spinner spinner) {
        String[] prepaidStrings = searchTypeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireActivity(),
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


    private void getMembers(int type) {
        String final_url = APIs.GET_MEMBER
                + "?" + APIs.USER_TAG + "=" + AppPreference.getInstance(getActivity()).getId()
                + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(getActivity()).getToken()
                + "&type=" + userType;


        if (!searchFor.equals("")) {


            String mUrl = getSaleUrls();

            final_url = AppConstants.BASE_URL + mUrl;
        }

        search_url = final_url;
        if (type == 2) {
            final_url = final_url + "?" + page;
        } else main_progressbar.setVisibility(View.VISIBLE);


        if (!searchFor.equals(""))
            WebApiCall.getRequestWithHeader(requireContext(), final_url);
        else WebApiCall.getRequest(requireContext(), final_url);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                main_progressbar.setVisibility(View.GONE);

                try {
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        int count = jsonObject.getInt("count");

                        if (count > 0) {
                            total_page += 1;
                            page = jsonObject.getString("page");
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
                } catch (JSONException exception) {
                    main_progressbar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(String message) {
                main_progressbar.setVisibility(View.GONE);
            }
        });


    }

    @NonNull
    private String getSaleUrls() {
        String mUrl;
        switch (searchFor) {
            case "ASM":
                mUrl = "sales/member/asm-list";
                break;
            case "API":
                mUrl = "sales/member/api-list";

                break;
            case "FOS":
                mUrl = "sales/member/fos-list";

                break;
            case "MD":
                mUrl = "sales/member/md-list";

                break;
            case "Distributor":
                mUrl = "sales/member/distributor-list";
                break;
            case "Retailer":
                mUrl = "sales/member/retailer-list";
                break;
            default:
                mUrl = "";
                break;
        }
        return mUrl;
    }

    private void parseArray(JSONArray jsonArray, int type) {

        ArrayList<Member> memberArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.optString("id");
                String name = jsonObject.optString("name");
                if (name.isEmpty()) {
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


                Member member = new Member(id, name, balance, status, shopName, email, mobile, status_id, parentDetails, prefix);
                memberArrayList.add(member);

            } catch (JSONException e) {
                main_progressbar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }


        if (type == 1) {
            main_progressbar.setVisibility(View.GONE);
            adapter.addAll(memberArrayList);

            if (currentPage <= total_page) adapter.addLoadingFooter();
            else isLastPage = true;
        } else if (type == 2) {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(memberArrayList);

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
