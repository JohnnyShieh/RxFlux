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

/**
 * The Action type define the identified id of action and the data class type.
 */
data class ActionType<out T, out V>(
    val id: String,
    val initValueClass: Class<out T>,
    val successValueClass: Class<out V>
)

/**
 * Normal action class that hold the type of action and the data we want to attach to it
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.1
 *
 * Created on 2018/3/29
 */
data class Action<out T, out V>(
    val type: ActionType<T, V>,
    /** The target store to receive the action, like [android.os.Message.getTarget] */
    val target: Store?,
    val initValue: T,
    val successValue: V
) : Handle {
    /** If the action is not accept by any Store, You will see it at error log. */
    override var handled: Boolean = false
}

/**
 * Error action class that hold the type of action and the data we want to attach to it
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.1
 *
 * Created on 2018/3/29
 */
data class ErrorAction<out T>(
    val type: ActionType<T, *>,
    /** The target store to receive the action, like [android.os.Message.getTarget] */
    val target: Store?,
    val initValue: T,
    val throwable: Throwable?
) : Handle {
    /** If the action is not accept by any Store, You will see it at error log. */
    override var handled: Boolean = false
}

interface Handle {
    /** whether the object be handled or not */
    var handled: Boolean
}