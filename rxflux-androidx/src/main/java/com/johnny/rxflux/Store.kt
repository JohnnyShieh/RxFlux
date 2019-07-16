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

import android.util.Log
import androidx.lifecycle.ViewModel
import com.johnny.rxflux.RxFlux.RxFluxTag
import com.johnny.rxflux.RxFlux.enableLog
import io.reactivex.internal.disposables.ListCompositeDisposable

/**
 * Flux store, It's recommend to register som action when initialize store.
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.2
 *
 */
open class Store : ViewModel() {

    protected val disposableList = ListCompositeDisposable()

    override fun onCleared() {
        unregister()
    }

    protected fun register(
        actionType: ActionType<Unit, Unit>,
        successHandler : (() -> Unit)
    ) {
        val disposable = Dispatcher.normalBus
            .ofType(Action::class.java)
            .filter {
                if (null != it.target) {
                    it.target === this && it.type == actionType
                } else {
                    it.type == actionType
                }
            }
            .subscribe {
                it.handled = true
                // catch exception avoid complete subscribe relationship
                try {
                    if (enableLog) {
                        Log.d(RxFluxTag, "Store ${this.javaClass.simpleName} handle normal action $it")
                    }
                    successHandler.invoke()
                } catch (e: Throwable) {
                    Log.e(RxFluxTag, "Store ${this.javaClass.simpleName} handle normal action $it throws Exceptiop", e)
                }
            }
        disposableList.add(disposable)
    }

    protected inline fun <reified V> register(
        actionType: ActionType<Unit, V>,
        crossinline successHandler : ((successValue: V) -> Unit)
    ) {
        val disposable = Dispatcher.normalBus
            .ofType(Action::class.java)
            .filter {
                if (null != it.target) {
                    it.target === this && it.type == actionType
                } else {
                    it.type == actionType
                }
            }
            .subscribe {
                it.handled = true
                // catch exception avoid complete subscribe relationship
                try {
                    if (enableLog) {
                        Log.d(RxFluxTag, "Store ${this.javaClass.simpleName} handle normal action $it")
                    }
                    successHandler.invoke(it.successValue as V)
                } catch (e: Throwable) {
                    Log.e(RxFluxTag, "Store ${this.javaClass.simpleName} handle normal action $it throws Exceptiop", e)
                }
            }
        disposableList.add(disposable)
    }

    protected inline fun <reified T, reified V> register(
        actionType: ActionType<T, V>,
        crossinline successHandler : ((initValue: T, successValue: V) -> Unit)
    ) {
        val disposable = Dispatcher.normalBus
            .ofType(Action::class.java)
            .filter {
                if (null != it.target) {
                    it.target === this && it.type == actionType
                } else {
                    it.type == actionType
                }
            }
            .subscribe {
                it.handled = true
                // catch exception avoid complete subscribe relationship
                try {
                    if (enableLog) {
                        Log.d(RxFluxTag, "Store ${this.javaClass.simpleName} handle normal action $it")
                    }
                    successHandler.invoke(it.initValue as T, it.successValue as V)
                } catch (e: Throwable) {
                    Log.e(RxFluxTag, "Store ${this.javaClass.simpleName} handle normal action $it throws Exceptiop", e)
                }
            }
        disposableList.add(disposable)
    }

    protected fun register(
        actionType: ActionType<Unit, Unit>,
        successHandler : (() -> Unit),
        erorHandler : ((Throwable?) -> Unit)
    ) {
        register(actionType, successHandler)
        val disposable = Dispatcher.errorBus
            .ofType(ErrorAction::class.java)
            .filter {
                if (null != it.target) {
                    it.target === this && it.type == actionType
                } else {
                    it.type == actionType
                }
            }.subscribe {
                it.handled = true
                // catch exception avoid complete subscribe relationship
                try {
                    if (enableLog) {
                        Log.d(RxFluxTag, "Store ${this.javaClass.simpleName} handle error action $it")
                    }
                    erorHandler.invoke(it.throwable)
                } catch (e: Throwable) {
                    Log.e(RxFluxTag, "Store ${this.javaClass.simpleName} handle error action $it throws Exceptiop", e)
                }
            }
        disposableList.add(disposable)
    }

    protected inline fun <reified V> register(
        actionType: ActionType<Unit, V>,
        crossinline successHandler : ((successValue: V) -> Unit),
        crossinline erorHandler : ((Throwable?) -> Unit)
    ) {
        register(actionType, successHandler)
        val disposable = Dispatcher.errorBus
            .ofType(ErrorAction::class.java)
            .filter {
                if (null != it.target) {
                    it.target === this && it.type == actionType
                } else {
                    it.type == actionType
                }
            }.subscribe {
                it.handled = true
                // catch exception avoid complete subscribe relationship
                try {
                    if (enableLog) {
                        Log.d(RxFluxTag, "Store ${this.javaClass.simpleName} handle error action $it")
                    }
                    erorHandler.invoke(it.throwable)
                } catch (e: Throwable) {
                    Log.e(RxFluxTag, "Store ${this.javaClass.simpleName} handle error action $it throws Exceptiop", e)
                }
            }
        disposableList.add(disposable)
    }

    protected inline fun <reified T, reified V> register(
        actionType: ActionType<T, V>,
        crossinline successHandler : ((initValue: T, successValue: V) -> Unit),
        crossinline erorHandler : ((initValue: T, Throwable?) -> Unit)
    ) {
        register(actionType, successHandler)
        val disposable = Dispatcher.errorBus
            .ofType(ErrorAction::class.java)
            .filter {
                if (null != it.target) {
                    it.target === this && it.type == actionType
                } else {
                    it.type == actionType
                }
            }.subscribe {
                it.handled = true
                // catch exception avoid complete subscribe relationship
                try {
                    if (enableLog) {
                        Log.d(RxFluxTag, "Store ${this.javaClass.simpleName} handle error action $it")
                    }
                    erorHandler.invoke(it.initValue as T, it.throwable)
                } catch (e: Throwable) {
                    Log.e(RxFluxTag, "Store ${this.javaClass.simpleName} handle error action $it throws Exceptiop", e)
                }
            }
        disposableList.add(disposable)
    }

    private fun unregister() {
        if (enableLog) {
            Log.d(RxFluxTag, "Store ${this.javaClass.simpleName} has unregistered")
        }
        disposableList.dispose()
    }
}