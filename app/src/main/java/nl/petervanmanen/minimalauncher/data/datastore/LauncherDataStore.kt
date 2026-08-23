package nl.petervanmanen.minimalauncher.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.launcherDataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_settings")
