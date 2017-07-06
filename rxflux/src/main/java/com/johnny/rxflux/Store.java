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

import javax.annotation.Nonnull;

import io.reactivex.disposables.Disposable;

/**
 * Flux store
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.1
 */
public abstract class Store extends Observable{

    private Disposable mDisposable;

    public void register(String... actionTypes) {
        RxFlux.register(this, actionTypes);
    }

    void setDisposable(Disposable disposable) {
        if(null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = disposable;
    }

    /**
     * You should call this method to avoid memroy leak when activity or fragment destroyed.
     */
    public void unRegister() {
        Logger.logUnregisterStore(getClass().getSimpleName());
        setDisposable(null);
        removeObserver();
    }

    /**
     * handle normal action which has registered
     * @param action normal action which has registered
     * @return true, notify observer to update; false, means store has no change, there is no need to update.
     */
    protected abstract boolean onAction(@Nonnull Action action);

    /**
     * handle error action which has registered
     * @param action error action which has registered
     * @return true, notify observer to update; false, means store has no change, there is no need to update.
     */
    protected abstract boolean onError(@Nonnull Action action, Throwable throwable);

    void handleAction(Action action) {
        if (action instanceof ErrorAction) {
            Logger.logOnError(getClass().getSimpleName(), action.getType());
            ErrorAction errorAction = (ErrorAction) action;
            if (onError(errorAction.getAction(), errorAction.getThrowable())) {
                postError(errorAction.getAction().getType());
            }
        } else {
            Logger.logOnAction(getClass().getSimpleName(), action);
            if (onAction(action)) {
                postChange(action.getType());
            }
        }
    }

    private void postChange(String type) {
        Logger.logPostStoreChange(getClass().getSimpleName(), type);
        notifyChange(type);
    }

    private void postError(String type) {
        Logger.logPostStoreError(getClass().getSimpleName(), type);
        notifyError(type);
    }
}
