package com.a2zsuvidhaa.`in`.model


data class MatmReportData(

    var record_id: String? = null,
    val status: Int,
    var status_desc: String? = null,
    var service_name: String? = null,
    var customer_number: String? = null,
    var txn_id: String? = null,
    var order_id: String? = null,
    var transaction_type: String? = null,
    var card_number: String? = null,
    var card_type: String? = null,
    var credit_debit_card_type: String? = null,
    var available_amount: String? = null,
    var transaction_amount: String? = null,
    var is_pin_verified: String? = null,
    var transaction_mode: String? = null,
    var bank_ref: String? = null,
    var txn_time: String? = null,
    var message: String? = null,


    var tds: String? = null,
    var credit_charge: String? = null,
    var debit_charge: String? = null,
    var gst: String? = null,

    val is_check_status: Boolean = true,
    val is_print: Boolean = true,
    val is_complain: Boolean = true,
    var isHide: Boolean = true,
)