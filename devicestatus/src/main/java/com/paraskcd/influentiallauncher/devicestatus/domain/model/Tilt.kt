package com.paraskcd.influentiallauncher.devicestatus.domain.model

data class Tilt(val x: Float, val y: Float) {
    companion object {
        val Level = Tilt(0f, 0f)
    }
}
