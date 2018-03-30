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
import android.support.annotation.MainThread

/**
 * Flux dispatcher, contains a rxbus used to send action to store
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.1
 *
 */
class Dispatcher private constructor() : IDispatcher {

    companion object {
        internal var instance: IDispatcher = Dispatcher()
    }

    private val bus = RxBus()

    @MainThread
    override fun register(store: Store, vararg actionTypes: String) {
        Logger.logRegisterStore(store.javaClass.simpleName, *actionTypes)
        store.disposable = bus.toObservable(Action::class.java)
                .filter { action ->
                    if (actionTypes.isEmpty()) {
                        return@filter true
                    }
                    return@filter actionTypes.any { it == action.type }
                }.subscribe { action ->
                    // catch exception avoid complete subscribe relationship
                    try {
                        store.handleAction(action)
                    } catch (e: Exception) {
                        Logger.logHandleException(store.javaClass.simpleName, action.type, e)
                    }
                }
    }

    @MainThread
    override fun postAction(action: Action) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postAction method on main thread!")
        }
        if (action.isError) {
            throw IllegalArgumentException("post Action isError should not be true!")
        }

        Logger.logPostAction(action)
        bus.post(action)
    }

    @MainThread
    override fun postError(action: Action) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postAction method on main thread!")
        }
        if (!action.isError) {
            throw IllegalArgumentException("post error Action isError should be true!")
        }

        Logger.logPostError(action)
        bus.post(action)
    }
}

fun mockDispatcher(mockObj: IDispatcher) {
    Dispatcher.instance = mockObj
}

internal fun register(store: Store, vararg actionTypes: String) = Dispatcher.instance.register(store, *actionTypes)

fun postAction(action: Action) = Dispatcher.instance.postAction(action)

fun postError(action: Action) = Dispatcher.instance.postError(action)