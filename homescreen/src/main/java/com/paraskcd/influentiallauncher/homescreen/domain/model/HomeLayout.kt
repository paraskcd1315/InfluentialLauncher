package com.paraskcd.influentiallauncher.homescreen.domain.model

import com.paraskcd.influentiallauncher.apps.domain.model.AppId

/** One user page of ordered slots, where null is an empty cell. */
data class HomePage(
    val id: String,
    val apps: List<AppId?>
)

data class HomeLayout(
    val pages: List<HomePage>,
    val homePageId: String
) {
    val homeIndex: Int get() = pages.indexOfFirst { it.id == homePageId }.coerceAtLeast(0)
}
