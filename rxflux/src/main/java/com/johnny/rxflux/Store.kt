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

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable

/**
 * Flux store
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.2
 *
 */
abstract class Store : ViewModel() {

    internal var disposable: Disposable? = null
        set(value) {
            val oldValue = field
            if (null != oldValue && !oldValue.isDisposed) {
                oldValue.dispose()
            }
            field = value
        }

    val isRegistered: Boolean
        get() = (disposable != null)

    /**
     * handle normal action which has registered
     * @param action normal action which has registered
     */
    protected abstract fun onAction(action: Action)

    /**
     * handle error action which has registered
     * @param action error action which has registered
     */
    protected abstract fun onError(action: Action)

    override fun onCleared() {
        unRegister()
    }

    fun register(vararg actionType: String) = register(this, *actionType)

    fun unRegister() {
        Logger.logUnregisterStore(javaClass.simpleName)
        disposable = null
    }

    internal fun handleAction(action: Action) {
        if (action.isError) {
            Logger.logHandleError(javaClass.simpleName, action)
            onError(action)
        } else {
            Logger.logHandleAction(javaClass.simpleName, action)
            onAction(action)
        }
    }
}