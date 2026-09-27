package com.paraskcd.influentiallauncher.homescreen.infrastructure

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomeLayout
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomePage
import com.paraskcd.influentiallauncher.homescreen.domain.ports.HomeStore
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeEdits
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.homeStore: DataStore<Preferences> by preferencesDataStore(name = "homescreen")

@Singleton
class DataStoreHomeStore @Inject constructor(
    @ApplicationContext private val context: Context
) : HomeStore {

    override val layout: Flow<HomeLayout> = context.homeStore.data
        .catch { error ->
            if (error !is IOException) throw error
            Log.w(LogTag, "reading home layout failed", error)
            emit(emptyPreferences())
        }
        .map { decode(it[LayoutKey]) }
        .distinctUntilChanged()

    override suspend fun update(transform: (HomeLayout) -> HomeLayout) {
        context.homeStore.edit { preferences ->
            val next = HomeEdits.normalize(transform(decode(preferences[LayoutKey])), ::newId)
            preferences[LayoutKey] = encode(next)
        }
    }

    private fun decode(text: String?): HomeLayout {
        val empty = HomeEdits.normalize(HomeLayout(emptyList(), "")) { DefaultPageId }
        if (text.isNullOrBlank()) return empty
        return runCatching {
            val root = JSONObject(text)
            val pages = root.optJSONArray(Fields.Pages) ?: JSONArray()
            val parsed = (0 until pages.length()).mapNotNull { index ->
                val page = pages.optJSONObject(index) ?: return@mapNotNull null
                val apps = page.optJSONArray(Fields.Apps) ?: JSONArray()
                HomePage(
                    id = page.getString(Fields.Id),
                    apps = (0 until apps.length()).map { slot -> if (apps.isNull(slot)) null else AppId.fromKey(apps.optString(slot)) }
                )
            }
            HomeEdits.normalize(HomeLayout(parsed, root.optString(Fields.Home))) { DefaultPageId }
        }.onFailure { Log.w(LogTag, "decoding home layout failed", it) }.getOrDefault(empty)
    }

    private fun encode(layout: HomeLayout): String = JSONObject()
        .put(Fields.Home, layout.homePageId)
        .put(
            Fields.Pages,
            JSONArray().apply {
                layout.pages.forEach { page ->
                    put(
                        JSONObject()
                            .put(Fields.Id, page.id)
                            .put(Fields.Apps, JSONArray().apply { page.apps.forEach { put(it?.key ?: JSONObject.NULL) } })
                    )
                }
            }
        )
        .toString()

    private fun newId(): String = UUID.randomUUID().toString()

    private object Fields {
        const val Home = "home"
        const val Pages = "pages"
        const val Id = "id"
        const val Apps = "apps"
    }

    private companion object {
        const val LogTag = "HomeStore"
        const val DefaultPageId = "home"
        val LayoutKey = stringPreferencesKey("layout")
    }
}
