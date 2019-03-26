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
        const val TAG = "RxFlux"
        var instance: IDispatcher = Dispatcher()
    }

    private val bus = RxBus()

    @MainThread
    override fun register(store: Store, vararg actionTypes: String) {
        if (enableLog) {
            if (actionTypes.isEmpty()) {
                Log.d(TAG, "Store ${store.javaClass.simpleName} has registered all action")
            } else {
                Log.d(TAG, "Store ${store.javaClass.simpleName} has registered action : ${actionTypes.contentToString()}")
            }
        }
        store.disposable = bus.toObservable(Action::class.java)
                .filter { action ->
                    // the target of action has the highest priority
                    if (action.target != null) {
                        return@filter action.target === store
                    } else {
                        if (actionTypes.isEmpty()) {
                            return@filter true
                        }
                        return@filter actionTypes.any { it == action.type }
                    }
                }.subscribe { action ->
                    // catch exception avoid complete subscribe relationship
                    try {
                        store.handleAction(action)
                    } catch (e: Exception) {
                        Log.e(TAG, "Store ${store.javaClass.simpleName} handle action ${action.type} throws Exceptiop", e)
                    }
                }
    }

    @MainThread
    override fun postAction(action: Action) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postAction() method on main thread!")
        }
        action.isError = false
        if (enableLog) {
            Log.d(TAG, "post action : $action")
        }
        bus.post(action)
    }

    @MainThread
    override fun postError(action: Action) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postError() method on main thread!")
        }
        action.isError = true
        if (enableLog) {
            Log.d(TAG, "post error : $action")
        }
        bus.post(action)
    }
}