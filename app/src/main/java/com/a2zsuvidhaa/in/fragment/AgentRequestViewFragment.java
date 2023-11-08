package com.a2zsuvidhaa.in.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a2zsuvidhaa.in.activity.MainActivity;
import com.a2zsuvidhaa.in.model.Remark;
import com.a2zsuvidhaa.in.util.RefreshPage;
import com.a2zsuvidhaa.in.util.Security;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.adapter.AgentRequestViewAdapter;
import com.a2zsuvidhaa.in.model.AgentRequestView;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class AgentRequestViewFragment extends Fragment {


    private final static String TAG = "AgentRequestView";

    public AgentRequestViewFragment() {
    }

    public static AgentRequestViewFragment newInstance() {
        AgentRequestViewFragment fragment = new AgentRequestViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    Calendar myCalendar;


    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private AgentRequestViewAdapter adapter;

    private ProgressBar main_progressbar;
    private TextView tv_number;
    private TextView tv_role;
    private TextView tv_noResult;
    private TextView tv_news;
    private AppPreference appPreference;
    private Security security;


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
        return inflater.inflate(R.layout.fragment_agent_request_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        appPreference = AppPreference.getInstance(requireContext());
        security = new Security(APIs.ENCRYPTED_KEY);

        myCalendar = Calendar.getInstance();
        main_progressbar = view.findViewById(R.id.progressBar);
        tv_noResult = view.findViewById(R.id.tv_noResult);
        //Button add=view.findViewById(R.id.btn_addmoney);

        tv_number = view.findViewById(R.id.tv_number);
        tv_role = view.findViewById(R.id.tv_role);



        tv_number.setText(security.decrypt(appPreference.getMobile()));
        tv_role.setText(appPreference.getRoleTitle());

        view.findViewById(R.id.ll_top_up_wallet).setOnClickListener(view1 -> {
            /*String upi_str = "upi://pay?pa=excelone@icici&pn=Excel Stop&tr=132"+"&am=1.00"+"&cu=INR&mc=5411&tn=By Rahul";
            Intent intent = new Intent();
            intent.setData(Uri.parse(upi_str));
            Intent chooser = Intent.createChooser(intent, "Pay with...");
            startActivityForResult(chooser, 1, null);*/
            MainActivity.spos=0;
            ((MainActivity)getActivity()).replaceFragment(BankDetailFragment.newInstance());
        });

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tv_news = view.findViewById(R.id.tv_news);
        tv_news.setSelected(true);
        tv_news.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv_news.setSingleLine(true);

        if (AppPreference.getInstance(getContext()).getRollId() == 1)
            tv_news.setVisibility(View.GONE);


        getAgentRequestViewList();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        RefreshPage.getData(context);
        RefreshPage.setBankDownNewsBalanceListener((isRefresh, balance, retailerNews, distributorNews, bankDownList) -> {
            if(isRefresh){
                if(distributorNews != null)
                tv_news.setText(distributorNews);
                listener.onBalanceUpdate(balance);
            }
        });
    }

    private void getAgentRequestViewList() {

        main_progressbar.setVisibility(View.VISIBLE);
        String url = APIs.AGENT_REQUEST_VIEW
                + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken();
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            int count = object.getInt("count");
                            if (count > 0) {

                                JSONObject remarkObject = object.getJSONObject("remarks");
                                Iterator iter = remarkObject.keys();
                                ArrayList<Remark> remarks = new ArrayList<>();
                                while (iter.hasNext()) {

                                    String key = iter.next().toString();
                                    String value = remarkObject.getString(key);

                                    Remark remark = new Remark(key,value);
                                    remarks.add(remark);
                                }

                                ArrayList<AgentRequestView> list = new ArrayList<>();
                                JSONArray resultArray = object.getJSONArray("result");
                                for (int i = 0; i < resultArray.length(); i++) {
                                    JSONObject jsonObject = resultArray.getJSONObject(i);
                                    String created_at = jsonObject.getString("created_at");
                                    String id = jsonObject.getString("id");
                                    String user_id = jsonObject.getString("user_id");
                                    String user_name = jsonObject.getString("user_name");
                                    String firm_name = jsonObject.getString("firm_name");
                                    String role = jsonObject.getString("role");
                                    String mobile = jsonObject.getString("mobile");
                                    String mode = jsonObject.getString("mode");
                                    String branch_code = jsonObject.getString("branch_code");
                                    String online_payment_mode = jsonObject.getString("online_payment_mode");
                                    String deposit_date = jsonObject.getString("deposit_date");
                                    String bank_name = jsonObject.getString("bank_name");
                                    String remark = jsonObject.getString("remark");
                                    String slip = jsonObject.getString("slip");
                                    String ref_id = jsonObject.getString("ref_id");
                                    String amount = jsonObject.getString("amount");
                                    String status1 = jsonObject.getString("status");
                                    String status_id = jsonObject.getString("status_id");

                                    AgentRequestView agentRequestView = new AgentRequestView(
                                            created_at, id, user_id, user_name, firm_name, role, mobile,
                                            mode, branch_code, online_payment_mode, deposit_date,
                                            bank_name, remark, slip, ref_id, amount, status1, status_id,
                                            remarks
                                    );
                                    list.add(agentRequestView);
                                }
                                adapter = new AgentRequestViewAdapter(getActivity(), list);
                                recyclerView.setAdapter(adapter);

                                adapter.setupOnClickListener(() -> {

                                    requireActivity().recreate();

                                });


                                if (adapter.getItemCount() > 0) {
                                    tv_noResult.setVisibility(View.GONE);
                                } else {
                                    tv_noResult.setVisibility(View.VISIBLE);
                                }

                            } else {
                                tv_noResult.setVisibility(View.VISIBLE);
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
                        main_progressbar.setVisibility(View.GONE);
                        MakeToast.show(getActivity(), e.getMessage());

                    }
                    main_progressbar.setVisibility(View.GONE);
                },
                error -> {
                    main_progressbar.setVisibility(View.GONE);
                    MakeToast.show(getActivity(), "on error");
                }) {

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private static UpdateBalanceListener listener;
    public static void setBalanceListener(UpdateBalanceListener listener){
        AgentRequestViewFragment.listener = listener;
    }
    public interface UpdateBalanceListener{
        void onBalanceUpdate(String balance);
    }

}
