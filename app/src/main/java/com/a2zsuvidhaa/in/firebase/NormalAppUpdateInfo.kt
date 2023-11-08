package com.a2zsuvidhaa.`in`.firebase

import com.a2zsuvidhaa.`in`.MyApplication
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.util.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject


class NormalAppUpdateInfo @Inject constructor(
        private val appPreference: AppPreference,
        private val firebaseFireStoreService: FirebaseService
) {

    fun checkForNormalUpdate(packageName: String, onUpdate: (force:Boolean,enable : Boolean,delay : Long) -> Unit) {
        fetchUpdateInfo(packageName, onResult = { playVersion ->


            firebaseFireStoreService.getNormalUpdateData { force,enable,delay ->

                val appVersion = MyApplication.currentAppVersion
                if (playVersion > appVersion.toFloat()) {


                    AppLog.d("appPreference real delay time ${appPreference.normalRealUpdateTime}")
                    AppLog.d("delay time $delay")
                    if(appPreference.normalRealUpdateTime != delay){
                        appPreference.normalRealUpdateTime = delay
                        appPreference.normalUpdateTime = 0L
                    }


                    if(delay == 0L){
                        appPreference.normalUpdateTime = 0L
                        onUpdate(force,enable,delay)
                    }
                    else{
                        if (appPreference.normalUpdateTime == 0L)
                        {
                            appPreference.normalUpdateTime =
                                System.currentTimeMillis() + delay
                        }
                        else if (appPreference.normalUpdateTime < System.currentTimeMillis()) {
                            AppLog.d("update : enable 2")
                            onUpdate(force,enable,delay)
                        }
                    }

                } else {
                    appPreference.normalUpdateTime = 0L
                }
            }
        }, onFailure = {
            appPreference.normalUpdateTime = 0L
        })
    }

    private fun fetchUpdateInfo(
        packageName: String,
        onResult: (Float) -> Unit,
        onFailure: () -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                getPlayStoreAppVersion(
                    String.format(
                        "https://play.google.com/store/apps/details?id=%s",
                        packageName
                    )
                )
            }
            result?.let {
                try {
                    val playVersion = it.toFloat()
                    onResult(playVersion)
                } catch (e: Exception) {
                    onFailure()
                }
            } ?: run {
                onFailure()
            }
        }
    }

    private fun getPlayStoreAppVersion(appUrlString: String): String? {
        val currentVersionPatternSeq =
            "<div[^>]*?>Current\\sVersion</div><span[^>]*?>(.*?)><div[^>]*?>(.*?)><span[^>]*?>(.*?)</span>"
        val appVersionPatternSeq = "htlgb\">([^<]*)</s"
        try {
            val connection = URL(appUrlString).openConnection()
            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6"
            )
            BufferedReader(InputStreamReader(connection.getInputStream()))
                .use { br ->
                    val sourceCode = StringBuilder()
                    var line: String?
                    while (br.readLine().also { line = it } != null) sourceCode.append(line)
                    val versionString = getAppVersion(
                        currentVersionPatternSeq,
                        sourceCode.toString()
                    ) ?: return null
                    return getAppVersion(
                        appVersionPatternSeq,
                        versionString
                    )
                }
        } catch (e: IOException) {
        }
        return null
    }

    private fun getAppVersion(patternString: String, input: String): String? {
        try {
            val pattern =
                Pattern.compile(patternString)
            val matcher = pattern.matcher(input)
            if (matcher.find()) return matcher.group(1)
        } catch (e: PatternSyntaxException) {
        }
        return null
    }

}