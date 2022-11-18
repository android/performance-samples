/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.macrobenchmark.target.util

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** ViewModel for this sample */
class SampleViewModel(application: Application) : AndroidViewModel(application) {

    private val KEY_USER = stringPreferencesKey("user")
    private val KEY_PASSWORD = stringPreferencesKey("password")

    private val _data = SampleData()
    private val _login = AppLogin()

    val data: SampleData = _data
    val login = _login

    init {
        loadAppLogin()
    }

    /**
     * Attempts to log in with the provided credentials.
     * @return `true` if successful, `false` otherwise.
     */
    suspend fun login(userName: String, password: String) {
        _login.userName = userName
        _login.password = password

            val context = getApplication<Application>()
            context.dataStore.edit { settings ->
                settings[KEY_USER] = userName
                settings[KEY_PASSWORD] = password
                Log.d(TAG, "Wrote AppLogin to Data Store.")
            }
    }

    fun loginSynchronous(userName: String, password: String) {
        viewModelScope.launch {
            login(userName, password)
        }
    }

    /**
     * Loads existing [AppLogin] data asynchronously.
     */
    private fun loadAppLogin() {
        viewModelScope.launch {
            loadAppLoginFromPreferences().collect() {
                _login.userName = it.userName
                _login.password = it.password
            }
        }
    }

    /**
     * Loads [AppLogin] from local data store.
     */
    private fun loadAppLoginFromPreferences(): Flow<AppLogin> =
        getApplication<Application>().dataStore.data.map { settings ->
            val userName = settings[KEY_USER] ?: ""
            val password = settings[KEY_PASSWORD] ?: ""
            Log.d(TAG, "Read AppLogin from Data Store.")
            AppLogin(userName = userName, password = password)
        }

}