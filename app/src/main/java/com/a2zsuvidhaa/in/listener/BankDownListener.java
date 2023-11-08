package com.a2zsuvidhaa.in.listener;

import com.android.volley.VolleyError;

public interface BankDownListener {
    void onResponse(String response);
    void onError(VolleyError error);
}
