package com.a2zsuvidhaa.`in`.util.ents

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import java.util.*
import kotlin.collections.HashMap


fun <T> List<T>.merge( second: List<T>): List<T> {
    return this + second
}

fun Context.makeToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.customToast(message: String) {
    val t = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    t.setGravity(Gravity.CENTER, 0, 0)
    t.show()
}


fun Map<String,String>.sortByComparator(order: Boolean): Map<String, String> {
    val list: List<Map.Entry<String, String>> = LinkedList(this.entries)
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


typealias FieldMapData = HashMap<String,String>