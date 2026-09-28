// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

private val Context.meteocatBudgetStore: DataStore<Preferences> by preferencesDataStore(name = "meteocat_budget")

@Singleton
class MeteocatBudget @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun take(plan: MeteocatPlan): Boolean {
        var granted = false
        context.meteocatBudgetStore.edit { preferences ->
            val month = YearMonth.now().toString()
            val monthKey = stringPreferencesKey(plan.name + MonthSuffix)
            val usedKey = intPreferencesKey(plan.name + UsedSuffix)
            if (preferences[monthKey] != month) {
                preferences[monthKey] = month
                preferences[usedKey] = 0
            }
            val used = preferences[usedKey] ?: 0
            if (used < plan.monthlyBudget) {
                preferences[usedKey] = used + 1
                granted = true
            }
        }
        if (!granted) Log.w(LogTag, "${plan.name} budget spent for this month")
        return granted
    }

    private companion object {
        const val LogTag = "MeteocatBudget"
        const val MonthSuffix = "_month"
        const val UsedSuffix = "_used"
    }
}
