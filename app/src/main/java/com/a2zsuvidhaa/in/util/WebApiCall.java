package com.a2zsuvidhaa.in.util;

import android.content.Context;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.activity.login.DeviceVerificationActivity;
import com.a2zsuvidhaa.in.activity.login.LoginActivity;
import com.a2zsuvidhaa.in.listener.WebApiCallListener;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WebApiCall {
    public static void postRequest(Context context, String url, HashMap<String, String> params) {

        AppPreference userPref = AppPreference.getInstance(context);

        Map<String, String> headerParam = new HashMap<>();
        headerParam.put("user-id", userPref.getId() + "");
        headerParam.put("token", userPref.getToken());


        if(userPref.getId()>0 && !(context instanceof LoginActivity) && !(context instanceof DeviceVerificationActivity)){
            params.put("token", userPref.getToken());
            params.put("userId", String.valueOf(userPref.getId()));
        }

        String classTAg = context.getClass().getSimpleName();
        AppLog.d(classTAg);
        AppLog.d("Method: POST");
        AppLog.d("Url: " + url);
        AppLog.d("param: " + JSONObject.wrap(params));
        AppLog.d("headers: " + JSONObject.wrap(headerParam));
        if (InternetConnection.isConnected(context)) {
            final StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        AppLog.d("response : " + response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            webApiCallListener.onSuccessResponse(jsonObject);

                        } catch (JSONException e) {
                            webApiCallListener.onFailure(e.getMessage());
                            MakeToast.show(context,"Exception : "+e.getMessage());
                        }
                    },
                    error -> {
                        AppLog.d("Error : " + error.getMessage());
                        String message = error.getMessage();
                        if (message == null) message = "Undefined Error";
                        webApiCallListener.onFailure(message);
                       MakeToast.show(context,"error : "+error.getMessage());
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return headerParam;
                }

                @Override
                protected Map<String, String> getParams() {
                    return params;
                }

            };
            RequestHandler.getInstance(context).addToRequestQueue(request);

            request.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(AppUitls.REQUEST_TIME_OUT),
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        } else {
            AppLog.d("NO CONNECTION AVAILABLE");
            MakeToast.show(context, "No internet connection is available");
        }
    }

    public static void getRequest(Context context, String url) {
        AppPreference userPref = AppPreference.getInstance(context);
        int length = url.length()-1;
        if(url.charAt(length)=='?'){
            url = url+APIs.USER_TAG + "=" + userPref.getId()+
                    "&" + APIs.TOKEN_TAG + "=" +userPref.getToken();
        }else {
            url = url+ "&"+APIs.USER_TAG + "=" + userPref.getId()+
                    "&" + APIs.TOKEN_TAG + "=" + userPref.getToken();
        }

        String classTAg = context.getClass().getSimpleName();
        AppLog.d(classTAg);
        AppLog.d("Method: GET");
        AppLog.d("Url: " + url);
        if (InternetConnection.isConnected(context)) {
            final StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        AppLog.d("response : " + response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            webApiCallListener.onSuccessResponse(jsonObject);

                        } catch (JSONException e) {
                            webApiCallListener.onFailure(e.getMessage());
                            AppLog.d("TESTING : "+e.getMessage());
                            AppDialogs.transactionStatus(context, "Json Parse Error :", 2);
                        }
                    },
                    error -> {
                        AppLog.d("Error : " + error.getMessage());
                        String message = error.getMessage();
                        if (message == null) message = "Undefined Error";
                        webApiCallListener.onFailure(message);
                    }) {



                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user-id", userPref.getId() + "");
                    params.put("token", userPref.getToken());
                    return params;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(AppUitls.REQUEST_TIME_OUT),
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else {
            AppLog.d("NO CONNECTION AVAILABLE");
            MakeToast.show(context, "No internet connection is available");
        }
    }

    public static void getRequestWithHeader(Context context, String url) {
        AppPreference userPref = AppPreference.getInstance(context);

        Map<String, String> params = new HashMap<>();
        params.put("user-id", userPref.getId() + "");
        params.put("token", userPref.getToken());




        String classTAg = context.getClass().getSimpleName();
        AppLog.d(classTAg);
        AppLog.d("Method: GET");
        AppLog.d("Url: " + url);
        AppLog.d("Header Data: "+JSONObject.wrap(params));
        if (InternetConnection.isConnected(context)) {
            final StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        AppLog.d("response : " + response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            webApiCallListener.onSuccessResponse(jsonObject);

                        } catch (JSONException e) {
                            webApiCallListener.onFailure(e.getMessage());
                            AppDialogs.transactionStatus(context, "Json Parse Error", 2);
                        }
                    },
                    error -> {
                        AppLog.d("Error : " + error.getMessage());
                        String message = error.getMessage();
                        if (message == null) message = "Undefined Error";
                        webApiCallListener.onFailure(message);
                    }) {



                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return params;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(AppUitls.REQUEST_TIME_OUT),
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else {
            AppLog.d("NO CONNECTION AVAILABLE");
            MakeToast.show(context, "No internet connection is available");
        }
    }

    private static WebApiCallListener webApiCallListener;

    public static void webApiCallback(WebApiCallListener listener) {
        webApiCallListener = listener;
    }
}
