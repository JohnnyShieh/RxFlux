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

import androidx.collection.ArrayMap

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
    internal var isError = false

    /**
     * The target store to receive the action, like [android.os.Message.getTarget]
     */
    var target: Store? = null

    /**
     * Just like [android.view.View] setTag(), getTag()
     */
    var singleData: Any? = null
    /**
     * Just like [android.view.View] setTag(key, value), getTag(key)
     */
    val data: ArrayMap<String, Any> by lazy { ArrayMap<String, Any>() }

    override fun toString() = "Action(type='$type', throwable=$throwable, singleData=$singleData, data=$data)"

}