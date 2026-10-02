// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.infrastructure

import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import com.paraskcd.influentiallauncher.devicestatus.domain.model.MobileDataType

internal fun mobileDataTypeOf(networkType: Int, overrideType: Int, naming: CarrierDataNaming): MobileDataType? =
    when (overrideType) {
        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED -> MobileDataType.FiveGPlus
        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA -> MobileDataType.FiveG
        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> MobileDataType.FiveGE
        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA -> lteTypeOf(naming, aggregated = !naming.hideLtePlus)
        else -> baseDataTypeOf(networkType, naming)
    }

private fun baseDataTypeOf(networkType: Int, naming: CarrierDataNaming): MobileDataType? = when (networkType) {
    TelephonyManager.NETWORK_TYPE_NR -> MobileDataType.FiveG
    TelephonyManager.NETWORK_TYPE_LTE -> lteTypeOf(naming, aggregated = false)
    TelephonyManager.NETWORK_TYPE_UMTS,
    TelephonyManager.NETWORK_TYPE_HSDPA,
    TelephonyManager.NETWORK_TYPE_HSUPA,
    TelephonyManager.NETWORK_TYPE_HSPA,
    TelephonyManager.NETWORK_TYPE_HSPAP -> if (naming.show4gFor3g) MobileDataType.FourG else MobileDataType.ThreeG
    TelephonyManager.NETWORK_TYPE_EVDO_0,
    TelephonyManager.NETWORK_TYPE_EVDO_A,
    TelephonyManager.NETWORK_TYPE_EVDO_B,
    TelephonyManager.NETWORK_TYPE_EHRPD,
    TelephonyManager.NETWORK_TYPE_TD_SCDMA -> MobileDataType.ThreeG
    TelephonyManager.NETWORK_TYPE_EDGE -> MobileDataType.E
    TelephonyManager.NETWORK_TYPE_GPRS,
    TelephonyManager.NETWORK_TYPE_GSM -> MobileDataType.G
    TelephonyManager.NETWORK_TYPE_CDMA,
    TelephonyManager.NETWORK_TYPE_1xRTT -> MobileDataType.OneX
    else -> null
}

private fun lteTypeOf(naming: CarrierDataNaming, aggregated: Boolean): MobileDataType = when {
    naming.show4gForLte -> if (aggregated) MobileDataType.FourGPlus else MobileDataType.FourG
    naming.show4gLteForLte -> if (aggregated) MobileDataType.FourGLtePlus else MobileDataType.FourGLte
    else -> if (aggregated) MobileDataType.LtePlus else MobileDataType.Lte
}
