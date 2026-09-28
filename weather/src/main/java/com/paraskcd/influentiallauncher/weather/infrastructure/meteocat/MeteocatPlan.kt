package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

enum class MeteocatPlan(val monthlyBudget: Int) {
    Forecast(90),
    Stations(700)
}
