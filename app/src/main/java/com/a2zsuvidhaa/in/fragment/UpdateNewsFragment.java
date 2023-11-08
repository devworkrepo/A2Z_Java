package com.a2zsuvidhaa.in.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UpdateNewsFragment extends Fragment {


    private static final String TAG = "UpdateNews";
    public UpdateNewsFragment() {
        // Required empty public constructor
    }

    public static UpdateNewsFragment newInstance() {
        return new UpdateNewsFragment();
    }


    private EditText ed_retailerNews;
    private EditText ed_distributorNews;
    private Button btn_submit;
    private RelativeLayout rl_progress;
    private ProgressBar progressBar;
    private ProgressBar mainProgressBar;
    private TextView tv_incorrect;
    private LinearLayout linearLayout ;
    private String companyId = "-1";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ed_retailerNews = view.findViewById(R.id.ed_retailerNews);
        ed_distributorNews = view.findViewById(R.id.ed_distributorNews);
        btn_submit = view.findViewById(R.id.btn_submit);
        tv_incorrect = view.findViewById(R.id.tv_incorrect);
        rl_progress = view.findViewById(R.id.rl_progress);
        progressBar= view.findViewById(R.id.progressBar);
        linearLayout = view.findViewById(R.id.ll_layout);
        mainProgressBar= view.findViewById(R.id.mainProgressBar);
;
        btn_submit.setOnClickListener(view1 -> {
            String retailerNews = ed_retailerNews.getText().toString();
            String distributorNews = ed_distributorNews.getText().toString();
            if(!retailerNews.isEmpty() && !distributorNews.isEmpty()){
                if(companyId.equalsIgnoreCase("-1")) {
                    tv_incorrect.setText("Something went wrong! try again");
                    tv_incorrect.setVisibility(View.VISIBLE);
                }
                else
                updateNews(retailerNews,distributorNews);
            }
            else MakeToast.show(getActivity(),"Yogaish ji news daliye!\nnews box khali h");
        });
        getAllNews();
    }

    private void updateNews(String retailerNews,String distributorNews) {

        tv_incorrect.setVisibility(View.GONE);
        btn_submit.setVisibility(View.GONE);
        rl_progress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.UPDATE_NEWS,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if(status.equalsIgnoreCase("1")){

                            Dialog dialog = AppDialogs.transactionStatus(getActivity(),message,1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view->dialog.dismiss());
                            dialog.show();

                        }
                        else if(status.equalsIgnoreCase("200")){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }


                    } catch (JSONException e) {
                        tv_incorrect.setVisibility(View.VISIBLE);
                        tv_incorrect.setText("Something went wrong!try again");

                    }

                    btn_submit.setVisibility(View.VISIBLE);
                    rl_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                },
                error -> {
                    btn_submit.setVisibility(View.VISIBLE);
                    rl_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    tv_incorrect.setVisibility(View.VISIBLE);
                    tv_incorrect.setText("Something went wrong!try again");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(getActivity()).getToken()));
                param.put("retailerNews",retailerNews);
                param.put("distributorNews",distributorNews);
                param.put("companyId",companyId);
                return param;
            }

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void getAllNews(){
        final StringRequest request = new StringRequest(Request.Method.GET,APIs.GET_NEWS
                + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken(),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {

                            String retailerNews = jsonObject.getString("retailerNews");
                            String distributorNews = jsonObject.getString("distributorNews");
                            companyId = jsonObject.getString("companyId");
                            ed_retailerNews.setText(retailerNews);
                            ed_distributorNews.setText(distributorNews);

                        }
                        else if(status==200){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status==300){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {

                        tv_incorrect.setVisibility(View.VISIBLE);
                        tv_incorrect.setText("Something went wrong!try again");
                    }
                    mainProgressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                },
                error -> {
                    mainProgressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }) {

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


}
