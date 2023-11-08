package com.a2zsuvidhaa.`in`.util

import android.content.Context
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.RequestHandler
import com.a2zsuvidhaa.`in`.activity.login.DeviceVerificationActivity
import com.a2zsuvidhaa.`in`.activity.login.LoginActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VolleyClient @Inject constructor(
    private val context: Context,
    private val appPreference: AppPreference,
) {

    lateinit var apiUrl: String


    fun postRequest(
        url: String, params: HashMap<String, String> = hashMapOf(),
        onSuccess: (JSONObject) -> Unit, onFailure: (java.lang.Exception) -> Unit,
        useFakeApiResponse: Boolean = false,
        fakeApiFileName: String? = null,
        timeOutInSecond: Int = 300
    ) {

        apiUrl = url

        AppLog.d("TestingTimeOut")


        if (useFakeApiResponse) {
            val data = Utils.loadJSONFromAsset(context, fakeApiFileName.orEmpty()).orEmpty()
            val jsonObject = JSONObject(data)
            onSuccess(jsonObject)
            return
        }


        val headerParam: MutableMap<String, String> = HashMap()
        headerParam["user-id"] = appPreference.id.toString() + ""
        headerParam["token"] = appPreference.token
        if (appPreference.id > 0 && context !is LoginActivity && context !is DeviceVerificationActivity) {
            params["token"] = appPreference.token
            params["userId"] = appPreference.id.toString()
        }
        val classTAg = context.javaClass.simpleName
        AppLog.d(classTAg)
        AppLog.d("Method: POST")
        AppLog.d("Url: $apiUrl")
        AppLog.d("param: " + JSONObject.wrap(params))
        AppLog.d("headers: " + JSONObject.wrap(headerParam))
        if (InternetConnection.isConnected(context)) {
            val request: StringRequest = object : StringRequest(Method.POST, apiUrl,
                Response.Listener { response: String ->
                    AppLog.d("response : $response")
                    try {
                        val jsonObject = JSONObject(response)
                        onSuccess(jsonObject)
                    } catch (e: JSONException) {
                        onFailure(e)
                    }
                },
                Response.ErrorListener { error: VolleyError? ->
                    AppLog.d("Error : " + error?.message)
                    val exception = error ?: java.lang.Exception("Response is null")
                    var message = error?.message
                    if (message == null) message = "Undefined Error"

                    if (error is TimeoutError) {
                        val timeInMillis = error.networkTimeMs.toString()
                        sendTimeoutLogToServer(apiUrl, timeInMillis, "1") {
                            onFailure(exception)
                        }
                    } else onFailure(exception)


                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    return headerParam
                }

                override fun getParams(): Map<String, String> {
                    return params
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    val timeInMillis = response?.networkTimeMs.toString()
                    sendTimeoutLogToServer(apiUrl, timeInMillis, "0") {}
                    return super.parseNetworkResponse(response)
                }
            }
            RequestHandler.getInstance(context).addToRequestQueue(request)
            request.retryPolicy = DefaultRetryPolicy(
                TimeUnit.SECONDS.toMillis(0.toLong()).toInt(),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        } else {
            AppLog.d("NO CONNECTION AVAILABLE")
            MakeToast.show(context, "No internet connection is available")
        }
    }

    private fun sendTimeoutLogToServer(
        mUrl: String,
        responseTime: String,
        isServerTimeOut: String,
        mCallBack: () -> Unit
    ) {

        if (
            apiUrl == AppConstants.BASE_URL + "aeps/three-new" ||
            apiUrl == AppConstants.BASE_URL + "make-recharge" ||
            apiUrl == AppConstants.BASE_URL + "aeps/three/table-check-status" ||
            apiUrl == AppConstants.BASE_URL + "bill-payment-one" ||
            apiUrl == AppConstants.BASE_URL + "bill-payment-two" ||
            apiUrl == AppConstants.BASE_URL + "a2z/plus/wallet/transaction" ||
            apiUrl == AppConstants.BASE_URL + "a2z/plus/wallet-two/transaction" ||
            apiUrl == AppConstants.BASE_URL + "a2z/plus/wallet-three/transaction" ||
            apiUrl == AppConstants.BASE_URL + "vpa/payment"
        ) {

            val url = AppConstants.BASE_URL + "store/resource/consume/information"
            postRequest(
                url = url,
                params = hashMapOf(
                    "user_id" to appPreference.id.toString(),
                    "is_request_timeout" to isServerTimeOut,
                    "service_consume_time" to responseTime,
                    "end_point_url" to mUrl
                ),
                onFailure = {
                    mCallBack()
                },
                onSuccess = {
                    mCallBack()
                }
            )
        }


    }


    fun getRequest(
        url: String,
        queryParam: HashMap<String, String>,
        onSuccess: (JSONObject) -> Unit,
        onFailure: (Exception) -> Unit,
        tag: String? = null
    ) {

        val builder = StringBuilder()

        builder.append("?${APIs.USER_TAG}=${appPreference.id}&${APIs.TOKEN_TAG}=${appPreference.token}")

        for ((key, value) in queryParam) {
            builder.append("&${key}=${value}")
        }
        val paraUrl = url + builder.toString()

        val classTAg = context.javaClass.simpleName
        AppLog.d(classTAg)
        AppLog.d("Method: GET")
        AppLog.d("Url: $paraUrl")
        if (InternetConnection.isConnected(context)) {
            val request: StringRequest = object : StringRequest(Method.GET, paraUrl,
                Response.Listener { response: String ->
                    AppLog.d("response : $response")
                    try {
                        val jsonObject = JSONObject(response)
                        onSuccess(jsonObject)
                    } catch (e: JSONException) {
                        onFailure(e)
                        AppLog.d("TESTING : " + e.message)
                        AppDialogs.transactionStatus(context, "Json Parse Error :", 2)
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    onFailure(error)
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["user-id"] = appPreference.id.toString()
                    params["token"] = appPreference.token
                    return params
                }
            }
            RequestHandler.getInstance(context).addToRequestQueue(request)
            if (tag != null) request.tag = tag
            request.retryPolicy = DefaultRetryPolicy(
                TimeUnit.SECONDS.toMillis(AppUitls.REQUEST_TIME_OUT).toInt(),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        } else {
            AppLog.d("NO CONNECTION AVAILABLE")
            MakeToast.show(context, "No internet connection is available")
        }
    }
}