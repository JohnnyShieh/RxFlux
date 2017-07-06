package com.johnny.rxflux;
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

import android.support.annotation.MainThread;
import android.text.TextUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Flux dispatcher, contains a rxbus used to send action to store
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class Dispatcher implements IDispatcher {

    private final RxBus bus;

    public Dispatcher() {
        this.bus = RxBus.get();
    }

    public void register(@NonNull final Store store, final String... actionTypes) {
        Logger.logRegisterStore(store.getClass().getSimpleName(), actionTypes);
        store.setDisposable(bus.toObservable(Action.class)
            .filter(new Predicate<Action>() {
                @Override
                public boolean test(@NonNull Action action) throws Exception {
                    if (null == actionTypes || actionTypes.length == 0) {
                        return true;
                    }
                    for (String type : actionTypes) {
                        if (TextUtils.equals(type, action.getType())
                            || TextUtils.equals(Action.getErrorType(type), action.getType())) {
                            return true;
                        }
                    }
                    return false;
                }
            }).subscribe(new Consumer<Action>() {
                @Override
                public void accept(@NonNull Action action) throws Exception {
                    // catch exception avoid complete subscribe relationship
                    try {
                        store.handleAction(action);
                    } catch (Exception e) {
                        Logger.logHandleActionException(store.getClass().getSimpleName(), action.getType(), e);
                    }
                }
            })
        );
    }

    @MainThread
    public void postAction(@NonNull final Action action) {
        Logger.logPostAction(action);
        bus.post(action);
    }

    @MainThread
    public void postError(@NonNull final Action action, Throwable throwable) {
        Logger.logPostErrorAction(action, throwable);
        bus.post(ErrorAction.newErrorAction(action, throwable));
    }

}
