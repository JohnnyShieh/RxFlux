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

import io.reactivex.annotations.NonNull;

/**
 * RxFlux
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class RxFlux {

    private RxFlux() {
        throw new IllegalStateException("Should never new this class");
    }

    static void register(@NonNull final Store store, final String... actionTypes) {
        DispatcherHolder.DISPATCHER.register(store, actionTypes);
    }

    public static void postAction(@NonNull final Action action) {
        DispatcherHolder.DISPATCHER.postAction(action);
    }

    public static void postError(@NonNull final Action action, Throwable throwable) {
        DispatcherHolder.DISPATCHER.postError(action, throwable);
    }

    /**
     * You should use this method only when test code.
     * @param mockObj mock dispatcher
     */
    public static void mock(IDispatcher mockObj) {
        DispatcherHolder.DISPATCHER = mockObj;
    }

    private static class DispatcherHolder {
        private static IDispatcher DISPATCHER = new Dispatcher();
    }
}
