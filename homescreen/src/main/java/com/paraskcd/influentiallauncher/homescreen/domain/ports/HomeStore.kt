package com.paraskcd.influentiallauncher.homescreen.domain.ports

import com.paraskcd.influentiallauncher.homescreen.domain.model.HomeLayout
import kotlinx.coroutines.flow.Flow

interface HomeStore {
    val layout: Flow<HomeLayout>

    suspend fun update(transform: (HomeLayout) -> HomeLayout)
}
