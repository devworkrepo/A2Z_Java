package com.a2zsuvidhaa.`in`.util.ui

import android.content.Context
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class ContextInjectUtil(val context: Context) {


    @Throws(Exception::class)
    suspend inline fun <reified T> loadJSONFromAsset(
        jsonFileName: String,
        delayInSecond: Int = 1,
    ): T {
        try {
            val data = GlobalScope.async(Dispatchers.Main) {
                delay((delayInSecond * 1000).toLong())
                val data = Utils.loadJSONFromAsset(context, jsonFileName)
                    ?: throw Exception("Fake Model is Empty!!!")

                AppUtil.logger("FAKE API CALL ::: " + T::class.simpleName)
                AppUtil.logger("=============FAKE API START===============")
                AppUtil.logger(data)
                AppUtil.logger("=============FAKE API END===============")
                Gson().fromJson(data, T::class.java)
            }
            return data.await()

        } catch (e: java.lang.Exception) {
            throw  e
        }
    }


}