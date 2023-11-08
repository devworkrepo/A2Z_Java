package com.a2zsuvidhaa.`in`.util

import android.content.Context
import java.io.*
import java.util.*
import java.util.regex.Pattern

object Utils {

    fun sortByComparator(unSortMap: Map<String, String>, order: Boolean): Map<String, String>? {
        val list: List<Map.Entry<String, String>> = LinkedList(unSortMap.entries)
        Collections.sort(list) { o1: Map.Entry<String, String>, o2: Map.Entry<String, String> ->
            if (order) {
                return@sort o1.value.compareTo(o2.value)
            } else {
                return@sort o2.value.compareTo(o1.value)
            }
        }
        val sortedMap: MutableMap<String, String> =
                LinkedHashMap()
        for ((key, value) in list) {
            sortedMap[key] = value
        }
        return sortedMap
    }

    fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$");
        return passwordREGEX.matcher(password).matches()
    }

    fun isAlphabeticInput(input : String) : Boolean{
        val reg = Pattern.compile("^[a-zA-Z0-9 ]*$")
        return reg.matcher(input).matches()
    }


    fun loadJSONFromAsset(context : Context,jsonFileName : String): String? {
        val json: String? = try {
            val inputStream: InputStream = context.assets.open("json/"+jsonFileName.trim()+".json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

}