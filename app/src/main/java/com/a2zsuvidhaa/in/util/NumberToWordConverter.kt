package com.a2zsuvidhaa.`in`.util

import com.a2zsuvidhaa.`in`.util.ents.capitalizeWords
import java.util.*


object NumberToWordConverter {
    val units = arrayOf(
            "", " one", "two", "three", "four", "five", "six", "seven",
            "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    )
    val tens = arrayOf(
            "",  // 0
            "",  // 1
            "twenty",  // 2
            "thirty",  // 3
            "forty",  // 4
            "fifty",  // 5
            "sixty",  // 6
            "seventy",  // 7
            "eighty",  // 8
            "ninety" // 9
    )

    fun doubleConvert(n: Double): String {
        var pass = n.toString() + ""
        val token = StringTokenizer(pass, ".")
        val first = token.nextToken()
        val last = token.nextToken()
        try {
            pass = convert(first.toInt()) + " "
            pass += "rupees and"
            for (element in last) {
                val get = convert((element.toString() + "").toInt())
                pass = if (get.isEmpty()) {
                    "$pass zero paisa"
                } else {
                    "$pass $get"
                }
            }
        } catch (nf: NumberFormatException) {
        }
        return pass.capitalizeWords()
    }

    private fun convert(n: Int): String {
        if (n < 0) {
            return "minus " + convert(-n)
        }
        if (n < 20) {
            return units[n]
        }
        if (n < 100) {
            return tens[n / 10] + (if (n % 10 != 0) " " else "") + units[n % 10]
        }
        if (n < 1000) {
            return units[n / 100] + " hundred " + (if (n % 100 != 0) " " else "") + convert(n % 100)
        }
        if (n < 1000000) {
            return convert(n / 1000) + " thousand " + (if (n % 1000 != 0) " " else "") + convert(n % 1000)
        }
        return if (n < 1000000000) {
            convert(n / 1000000) + " million " + (if (n % 1000000 != 0) " " else "") + convert(n % 1000000)
        } else convert(n / 1000000000) + " billion " + (if (n % 1000000000 != 0) " " else "") + convert(n % 1000000000)
    }
}