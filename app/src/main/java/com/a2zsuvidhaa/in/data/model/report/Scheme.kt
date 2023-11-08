package com.a2zsuvidhaa.`in`.data.model.report

import com.google.gson.annotations.SerializedName

data class CommissionSchemeListResponse(
        val status: Int,
        val message: String,
        val result: List<CommissionSchemeList>? = null
)

data class CommissionSchemeList(
        val type: String,
        val title: String,
)

data class CommissionSchemeDetailResponse(
        val status : Int,
        val message: String,
        val type: String? = null,
        val title: String? = null,
        val result : List<CommissionSchemeDetail>? = null
)


data class CommissionSchemeDetail(
        @SerializedName("id")val id : String?,
        @SerializedName("service") val service : String?,
        @SerializedName("operatorName") val operator : String?,
        @SerializedName("category") val category : String?,
        @SerializedName("min_amt") val minAmount : String?,
        @SerializedName("max_amt")val maxAmount : String?,
        @SerializedName("type")val type : String?,
        @SerializedName("agent_charge_type")val agentChargeType : String?,
        @SerializedName("agent_charge")val agentCharge : String?,
        @SerializedName("agent_comm_type")val agentCommissionType : String?,
        @SerializedName("agent_comm")val agentCommission : String?,
)