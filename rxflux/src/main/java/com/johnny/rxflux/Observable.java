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

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class Observable {

    private StoreObserver mObserver;

    public void setObserver(StoreObserver observer) {
        if(null == observer) {
            throw new NullPointerException();
        }else {
            mObserver = observer;
        }
    }

    void notifyChange(Store store, String actionType) {
        if (null != mObserver) {
            mObserver.onChange(store, actionType);
        }
    }

    void notifyError(Store store, String actionType) {
        if (null != mObserver) {
            mObserver.onError(store, actionType);
        }
    }

    void removeObserver() {
        mObserver = null;
    }
}