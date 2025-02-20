package com.ssafy.ganhoho.presentation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "userData")

class UserDataStore() {
    private val context = App.context()
    private val dataStore : DataStore<Preferences> = context.dataStore

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }

    suspend fun saveAccessToken(token : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.ACCESS_TOKEN] = token
            }
        }
    }

    suspend fun getAccessToken():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref.clear()
            }
        }
    }

}