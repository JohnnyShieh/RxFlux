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
        var instance: IDispatcher = Dispatcher()
    }

    private val bus = RxBus()

    @MainThread
    override fun register(store: Store, vararg actionTypes: String) {
        Logger.logRegisterStore(store.javaClass.simpleName, *actionTypes)
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
                        Logger.logHandleException(store.javaClass.simpleName, action.type, e)
                    }
                }
    }

    @MainThread
    override fun postAction(action: Action) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postAction() method on main thread!")
        }
        action.isError = false
        Logger.logPostAction(action)
        bus.post(action)
    }

    @MainThread
    override fun postError(action: Action) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("You must call postError() method on main thread!")
        }
        action.isError = true
        Logger.logPostError(action)
        bus.post(action)
    }
}

internal fun register(store: Store, vararg actionTypes: String) = Dispatcher.instance.register(store, *actionTypes)

fun postError(type: String, throwable: Throwable? = null, data: Any? = null) = Dispatcher.instance.postError(Action(type, throwable).apply { singleData = data })

fun postError(type: String, throwable: Throwable? = null, vararg params: Pair<String, Any>) {
    val action = Action(type, throwable)
    params.forEach {
        action.data[it.first] = it.second
    }
    Dispatcher.instance.postError(action)
}

fun postAction(type: String, data: Any? = null) = Dispatcher.instance.postAction(Action(type).apply { singleData = data })

fun postAction(type: String, vararg params: Pair<String, Any>) {
    val action = Action(type)
    params.forEach {
        action.data[it.first] = it.second
    }
    Dispatcher.instance.postAction(action)
}

