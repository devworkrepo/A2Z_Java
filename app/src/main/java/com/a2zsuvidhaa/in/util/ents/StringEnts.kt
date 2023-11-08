package com.a2zsuvidhaa.`in`.util.ents

fun String?.nullOrEmpty(): Boolean {
    if (this == null) return true
    return this.trim().isEmpty()


}

fun String?.naString(): String {
    if (this == null) return "N/A"
    return if (this.trim().isEmpty()) "N/A" else this
}

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

fun String.removeComa(): String {
    return if (this.contains(","))
        this.replace(",", "")
    else this
}

fun String?.makeDouble(): Double {
    return this?.let {
        try {
            it.toDouble()
        } catch (e: Exception) {
            0.0
        }
    } ?: 0.0
}

fun String?.makeInt(): Int {
    return this?.let {
        try {
            it.toInt()
        } catch (e: Exception) {
            0
        }
    } ?: 0
}
