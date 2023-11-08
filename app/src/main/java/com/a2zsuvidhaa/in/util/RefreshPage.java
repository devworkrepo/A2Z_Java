package com.a2zsuvidhaa.in.util;

import android.content.Context;
import android.content.Intent;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.RequestHandler;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class RefreshPage {

    public static void getData(Context context) {
        final StringRequest request = new StringRequest(Request.Method.GET, APIs.REFRESH_PAGE
                + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(context).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(context).getToken(),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {
                            AppPreference appPreference = AppPreference.getInstance(context);
                            String balance = jsonObject.getString("remainingBalance");
                            appPreference.setUserBalance(balance);
                            String retailerNews = jsonObject.getString("retailerNews");
                            String distributorNews = jsonObject.getString("distributorNews");
                            String bankDownList = jsonObject.getString("bankDownList");
                            listener.onRefreshPage(true,balance,retailerNews,distributorNews,bankDownList);
                        } else if (status == 200) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status == 300) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        }

                    } catch (JSONException e) {
                        listener.onRefreshPage(false,"0.0","N/A","N/A","N/A");
                    }
                },
                error -> {
                    listener.onRefreshPage(false,"0.0","N/A","N/A","N/A");
                }) {

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private static UpddateBankDownNewsBalanceListener listener;
    public static void setBankDownNewsBalanceListener(UpddateBankDownNewsBalanceListener listener){
        RefreshPage.listener = listener;
    }
    public interface UpddateBankDownNewsBalanceListener {
        void onRefreshPage(boolean isRefresh, String balance,String distributorNews,String retailerNews,String bankDownList);
    }
}
