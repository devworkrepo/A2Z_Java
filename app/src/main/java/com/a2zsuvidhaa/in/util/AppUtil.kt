package com.a2zsuvidhaa.`in`.util

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.model.DoubleTxn
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import org.json.JSONObject
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.regex.Pattern


object AppUtil {

    fun apiCallLogging(value: String) {
        if (BuildConfig.DEBUG) {
            Log.d("ApiCallLog", value)
        }
    }

    fun logger(value: String) {
        if (BuildConfig.DEBUG)
            Log.d("AppLogger", "value : $value")
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceId(context: Context?): String {
        var imei: String? = null
        try {
            val telephonyManager =
                context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    telephonyManager.imei
                } catch (e: Exception) {
                    e.printStackTrace()
                    Settings.Secure.getString(
                            context.contentResolver,
                            Settings.Secure.ANDROID_ID
                    )
                }
            } else telephonyManager.deviceId

        } catch (e: Exception) {
            imei = null
        }
        return imei ?: ""
    }

    fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile(
                "^" +
                        "(?=.*[0-9])" +         //at least 1 digit
                        "(?=.*[a-z])" +         //at least 1 lower case letter
                        "(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        "(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{8,}" +               //at least 8 characters
                        "$"
        );
        return passwordREGEX.matcher(password).matches()
    }

    fun base64ToString(value: String, c: Int): String {
        var mData = value
        for (i in 0..c - 4) mData = String(Base64.decode(mData, Base64.DEFAULT))
        return mData
    }


  /*  fun encryptData(value: String): String =
        AESEnDecryption.networkEncryptStrAndToBase64(SpayApp.networkKey, SpayApp.networkKey, value)
            .trim()

    fun decryptData(value: String): String = AESEnDecryption.networkDecryptStrAndFromBase64(
        SpayApp.networkKey,
        SpayApp.networkKey,
        value
    )
*/
    fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }

    fun canMakeTransaction(
            context: Context,
            doubleTxn: DoubleTxn,
            number: String,
            amount: String,
            type: String
    ): Boolean {

        val value = if (doubleTxn.type == type) {
            if (number == doubleTxn.number && amount == doubleTxn.amount) {
                System.currentTimeMillis() > doubleTxn.time
            } else true
        } else true

        return if (value) value
        else {
            StatusDialog.failure(
                    context as Activity,
                    "Please wait for 15 minutes, recently transaction with same amount and number ended with exception with check report for previous transaction"
            )
            false
        }
    }


    fun urlToJson(url: String?): JSONObject? {
        if(url== null) return null
        return try {
            val map: MutableMap<String?, Any?> = HashMap()
            val hashes = url.slice((url.indexOf("?") + 1)..url.lastIndex).split("&")
            for (hash in hashes) {
                val subHashes = hash.split("=")

                map[subHashes[0]] = subHashes[1]
            }
            JSONObject(map)
        }catch (e : java.lang.Exception) {
            null
        }

    }


}

