/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.models.Error

import com.zerebrez.zerebrez.models.enums.LoginErrorType

/**
 * Created by Jorge Zepeda Tinoco on 06/05/18.
 * jorzet.94@gmail.com
 */

class FirebaseError : Throwable() {
    private lateinit var errorType : LoginErrorType
    private lateinit var errorMessage : String

    fun setErrorType(errorType : LoginErrorType) {
        this.errorType = errorType
    }

    fun getErrorType() : LoginErrorType {
        return this.errorType
    }

    fun setErrorMessage(errorMessage : String) {
        this.errorMessage = errorMessage
    }

    fun getErrorMessage() : String {
        return this.errorMessage
    }
}