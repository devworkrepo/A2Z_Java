package com.a2zsuvidhaa.in.listener;

import org.json.JSONObject;

public interface WebApiCallListener {
    void onSuccessResponse(JSONObject jsonObject);
    void onFailure(String message);
}

