package com.a2zsuvidhaa.`in`.listener

interface KycRequiredListener {
    fun onAadhaarKycRequired()
    fun onDocumentKycRequired()
    fun onMPOSService()
    fun onMATMService()
}

interface OnSaleItemClickListener{
    fun onSaleItemClick(searchFor : String)
}