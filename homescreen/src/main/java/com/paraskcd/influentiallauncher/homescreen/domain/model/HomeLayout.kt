package com.paraskcd.influentiallauncher.homescreen.domain.model

import com.paraskcd.influentiallauncher.apps.domain.model.AppId

data class HomePage(
    val id: String,
    val apps: List<AppId>
)

data class HomeLayout(
    val pages: List<HomePage>,
    val homePageId: String
) {
    val homeIndex: Int get() = pages.indexOfFirst { it.id == homePageId }.coerceAtLeast(0)

    companion object {
        const val PageCapacity = 16
    }
}
