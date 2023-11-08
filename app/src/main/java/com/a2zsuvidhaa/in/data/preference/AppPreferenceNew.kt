package com.a2zsuvidhaa.`in`.data.preference

import android.content.SharedPreferences
import com.a2zsuvidhaa.`in`.model.DoubleTxn
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.google.gson.Gson
import javax.inject.Inject


class AppPreference @Inject constructor(private val sharedPreferences: SharedPreferences) {



    var doubleTxn: DoubleTxn
        set(value) = setString(Gson().toJson(value), DOUBLE_TXN)
        get() {
            val strDetail: String = getString(DOUBLE_TXN)
            return if (strDetail.isEmpty()) DoubleTxn()
            else Gson().fromJson(
                    strDetail,
                    DoubleTxn::class.java
            )
        }


    private fun setBoolean(value: Boolean, tag: String) =
            sharedPreferences.edit().putBoolean(tag, value).apply()

    private fun getBoolean(tag: String) = sharedPreferences.getBoolean(tag, false)


    private fun setLong(value: Long, tag: String) =
            sharedPreferences.edit().putLong(tag, value).apply()

    private fun getLong(tag: String) = sharedPreferences.getLong(tag, 0L)


    private fun setString(value: String, tag: String) {
        sharedPreferences.edit().putString(tag, value).apply()
    }

    private fun getString(tag: String): String {
        return sharedPreferences.getString(tag, AppConstants.EMPTY) ?: ""
    }




    companion object {

        const val DOUBLE_TXN = "double_txn"


    }
}