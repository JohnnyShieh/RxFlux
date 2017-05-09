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

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

/**
 * The Logger to track flux flow
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
class Logger {

    private static final String TAG = "RxFlux";

    private static boolean logEnabled = BuildConfig.DEBUG;

    public static void setLogEnabled(boolean enabled) {
        logEnabled = enabled;
    }

    static void logRegisterStore(@NonNull String storeName, String[] actionType) {
        if(logEnabled) {
            if(null == actionType || actionType.length == 0) {
                Log.d(TAG, "Store " + storeName + " has registered all action");
            }else {
                Log.d(TAG, "Store " + storeName + " has registered action : " + Arrays.toString(actionType));
            }
        }
    }

    static void logUnregisterStore(@NonNull String storeName) {
        if(logEnabled) {
            Log.d(TAG, "Store " + storeName + " has unregistered");
        }
    }

    static void logPostAction(@NonNull Action action) {
        if(logEnabled) {
            Log.d(TAG, "Post " + action.toString());
        }
    }

    static void logPostErrorAction(@NonNull Action action, Throwable throwable) {
        if(logEnabled) {
            Log.d(TAG, "Post error action " + action.toString() + " cause message : " + (null == throwable ? "null" : throwable.getMessage()));
        }
    }

    static void logOnAction(@NonNull String storeName, @NonNull Action action) {
        if(logEnabled) {
            Log.d(TAG, "Store " + storeName + " onAction " + action.toString());
        }
    }

    static void logOnError(@NonNull String storeName, @NonNull String actionName) {
        if(logEnabled) {
            Log.d(TAG, "Store " + storeName + " onError " + actionName);
        }
    }

    static void logPostStoreChange(@NonNull String storeName, @NonNull String actionType) {
        if(logEnabled) {
            Log.d(TAG, "Store " + storeName + " post change " + actionType);
        }
    }

    static void logPostStoreError(@NonNull String storeName, @NonNull String actionType) {
        if(logEnabled) {
            Log.d(TAG, "Store " + storeName + " post error " + actionType);
        }
    }

    static void logHandleActionException(@NonNull String storeName, @NonNull String actionName, Exception e) {
        if(logEnabled) {
            Log.d(TAG, "Store " + storeName + " handle action " + actionName + " throws Exception.\n", e);
        }
    }
}
