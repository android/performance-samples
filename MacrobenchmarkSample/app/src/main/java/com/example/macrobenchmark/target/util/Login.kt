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

abstract class Login {
    abstract suspend fun isLoggedIn(): Boolean
    abstract suspend fun login(): Boolean
    abstract suspend fun logout()

    lateinit var password: String

    lateinit var userName: String
}

open class AppLogin : Login() {

    override suspend fun isLoggedIn(): Boolean {
        // There's no verification here.
        return userName.isNotEmpty() && password.isNotEmpty()
    }

    override suspend fun login(): Boolean {
        return !(userName.isEmpty() || password.isEmpty())
    }

    override suspend fun logout() {
        userName = ""
        password = ""
    }
}

