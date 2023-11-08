package com.a2zsuvidhaa.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.adapter.NotificationAdapter;
import com.a2zsuvidhaa.in.model.NotificationModel;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.SessionManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tv_no;
    LinearLayout lin_not;
    ArrayList<NotificationModel> list;
    TextView text,notification_error;
    SessionManager sessionManager;
    NotificationAdapter notificationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        sessionManager=new SessionManager(NotificationActivity.this);
        recyclerView=(RecyclerView) findViewById(R.id.recycler);
        tv_no= findViewById(R.id.tv_no);
        lin_not= findViewById(R.id.lin_not);

        text=findViewById(R.id.text);

        notification();
    }

    private void notification() {



        final ProgressDialog progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StringRequest request = new StringRequest(Request.Method.POST, APIs.MY_NOTIFICATION,
                response -> {
                    try {
                        progressDialog.dismiss();
                        list = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e(" notification", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                NotificationModel mainModel = new NotificationModel();


                                mainModel.setNotificationMessage("" + jsonObject1.getString("name"));

                                list.add(mainModel);
                                //Log.e("names",""+response.array.getJSONObject(i).getJSONObject("measurement").getString("name"));
                            }
                            notificationAdapter = new NotificationAdapter(NotificationActivity.this, list);
                            final LinearLayoutManager linearLayoutManagerm;
                            linearLayoutManagerm = new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(linearLayoutManagerm);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(notificationAdapter);
                        } else {
                         //   Toast.makeText(NotificationActivity.this, "", Toast.LENGTH_SHORT).show();
                            recyclerView.setVisibility(View.GONE);
                            lin_not.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        lin_not.setVisibility(View.VISIBLE);
                        tv_no.setText("Something went wrong.");
                    }

                },
                error -> {
                    progressDialog.dismiss();


                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("token", AppPreference.getInstance(NotificationActivity.this).getToken());
                param.put("userId", String.valueOf(AppPreference.getInstance(NotificationActivity.this).getId()));
                Log.d("Notification",param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(NotificationActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
