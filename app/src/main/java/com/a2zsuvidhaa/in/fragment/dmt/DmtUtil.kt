package com.a2zsuvidhaa.`in`.fragment.dmt

import com.a2zsuvidhaa.`in`.util.enums.DmtType

object DmtUtil {

    fun getDmtServiceName(dmtType : DmtType) = when (dmtType) {
        DmtType.WALLET_ONE -> "A2Z Plus One"
        DmtType.WALLET_TWO -> "A2Z Plus Two"
        DmtType.WALLET_THREE -> "A2Z Plus Three"
        DmtType.DMT_THREE -> "Dmt One"
        DmtType.UPI -> "UPI"
    }
}