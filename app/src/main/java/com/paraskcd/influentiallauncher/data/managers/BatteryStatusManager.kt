package com.paraskcd.influentiallauncher.data.managers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.microsoft.fluent.mobile.icons.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryStatusManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val _level = MutableStateFlow(0)
    val level: StateFlow<Int> = _level.asStateFlow()

    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()

    val _isFullCharge = MutableStateFlow(false)
    val isFullCharge: StateFlow<Boolean> = _isFullCharge.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            val levelRaw = intent.getIntExtra("level", -1)
            val scale = intent.getIntExtra("scale", -1).takeIf { it > 0 } ?: 100
            val percent = if (levelRaw >= 0) (levelRaw * 100 / scale) else 0
            val bucket = (percent * 10 / 100).coerceIn(0, 10)
            _level.value = bucket
            val status = intent.getIntExtra("status", -1)
            _isCharging.value = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING
            _isFullCharge.value = status == android.os.BatteryManager.BATTERY_STATUS_FULL
        }
    }

    init {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    fun getBatteryDrawable(levelBucket: Int, charging: Boolean, isFullCharge: Boolean): Int {
        if (charging) return R.drawable.ic_fluent_battery_charge_24_regular
        else if (isFullCharge) return R.drawable.ic_fluent_battery_10_24_regular
        return when (levelBucket.coerceIn(0, 10)) {
            0 -> R.drawable.ic_fluent_battery_0_24_regular
            1 -> R.drawable.ic_fluent_battery_1_24_regular
            2 -> R.drawable.ic_fluent_battery_2_24_regular
            3 -> R.drawable.ic_fluent_battery_3_24_regular
            4 -> R.drawable.ic_fluent_battery_4_24_regular
            5 -> R.drawable.ic_fluent_battery_5_24_regular
            6 -> R.drawable.ic_fluent_battery_6_24_regular
            7 -> R.drawable.ic_fluent_battery_7_24_regular
            8 -> R.drawable.ic_fluent_battery_8_24_regular
            9 -> R.drawable.ic_fluent_battery_9_24_regular
            else -> R.drawable.ic_fluent_battery_10_24_regular
        }
    }
}