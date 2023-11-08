package com.a2zsuvidhaa.`in`.model

data class Offer(val price : String, val offerDesc : String) : Comparable<Offer>{
    override fun compareTo(other: Offer): Int = this.price.compareTo(other.price)
    override fun toString(): String =super.toString()

}