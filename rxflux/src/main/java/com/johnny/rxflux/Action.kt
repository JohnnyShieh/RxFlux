/*
 * Copyright (C) 2018 Johnny Shieh Open Source Project
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

import android.support.v4.util.ArrayMap

/**
 * Object class that hold the type of action and the data we want to attach to it
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.1
 *
 * Created on 2018/3/29
 */
data class Action(
    val type: String,
    val throwable: Throwable? = null
) {
    // the flag indicate the action is normal action or error action
    var isError: Boolean = false

    // the target store to receive the action
    var target: Store? = null

    val data: ArrayMap<String, Any> = ArrayMap()
}