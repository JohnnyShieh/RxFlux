/*
 * Copyright (C) 2017 Johnny Shieh Open Source Project
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
package com.johnny.rxflux

import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import com.johnny.rxflux.RxFlux.RxFluxTag
import com.johnny.rxflux.RxFlux.enableLog

/**
 * Flux dispatcher, contains a rxbus used to send action to store
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.1
 *
 */
object Dispatcher {

    val normalBus = PublishSubject.create().toSerialized()

    val errorBus = PublishSubject.create().toSerialized()

    @MainThread
    fun postAction(action: Action<*, *>) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postAction() method on main thread!")
        }
        if (enableLog) {
            Log.d(RxFluxTag, "post action : $action")
        }
        normalBus.onNext(action)
    }

    @MainThread
    fun postError(action: ErrorAction<*>) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postError() method on main thread!")
        }
        if (enableLog) {
            Log.d(RxFluxTag, "post error : $action")
        }
        errorBus.onNext(action)
    }
}
