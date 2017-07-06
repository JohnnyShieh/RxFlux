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
class Observable {

    private StoreObserver mObserver;

    public void setObserver(StoreObserver observer) {
        if(null == observer) {
            throw new NullPointerException();
        }else {
            mObserver = observer;
        }
    }

    void notifyChange(String actionType) {
        if (null != mObserver) {
            mObserver.onChange(actionType);
        }
    }

    void notifyError(String actionType) {
        if (null != mObserver) {
            mObserver.onError(actionType);
        }
    }

    void removeObserver() {
        mObserver = null;
    }
}
