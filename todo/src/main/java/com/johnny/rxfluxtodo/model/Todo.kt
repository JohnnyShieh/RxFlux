package com.johnny.rxfluxtodo.model

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
class Todo : Cloneable, Comparable<Todo> {

    val id: Long
    var isComplete: Boolean
    var text: String

    constructor(id: Long, text: String) {
        this.id = id
        this.text = text
        this.isComplete = false
    }

    constructor(id: Long, text: String, complete: Boolean) {
        this.id = id
        this.text = text
        this.isComplete = complete
    }

    public override fun clone(): Todo {
        return Todo(id, text, isComplete)
    }

    override fun compareTo(other: Todo): Int {
        return when {
            id == other.id -> 0
            id < other.id -> -1
            else -> 1
        }
    }
}
