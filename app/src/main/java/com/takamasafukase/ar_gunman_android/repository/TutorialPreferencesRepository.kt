package com.takamasafukase.ar_gunman_android.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.takamasafukase.ar_gunman_android.const.PrefDataStoreKey
import com.takamasafukase.ar_gunman_android.dataStore
import kotlinx.coroutines.flow.map


class TutorialPreferencesRepository(
    private val context: Context
) {
    suspend fun saveTutorialSeenStatus(isSeen: Boolean) {
        context.dataStore.edit {
            it[PrefDataStoreKey.isTutorialAlreadySeen] = isSeen
        }
    }

    suspend fun clearTutorialSeenStatus() {
        context.dataStore.edit {
            it.remove(PrefDataStoreKey.isTutorialAlreadySeen)
        }
    }

    suspend fun getTutorialSeenStatus(onData: (Boolean) -> Unit) {
        context.dataStore.data
            .map { it[PrefDataStoreKey.isTutorialAlreadySeen] ?: false }
            .collect {
                onData(it)
            }
    }
}