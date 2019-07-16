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
package com.johnny.rxfluxtodo.action

import com.johnny.rxflux.RxFlux
import com.johnny.rxfluxtodo.model.Todo

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2018/3/29
 */
class TodoActionCreator {

    fun create(text: String) = RxFlux.postAction(ActionType.TODO_CREATE, successValue = text)

    fun destroy(id: Long) = RxFlux.postAction(ActionType.TODO_DESTROY, successValue = id)

    fun undoDestroy() = RxFlux.postAction(ActionType.TODO_UNDO_DESTROY)

    fun toggleComplete(todo: Todo) {
        val actionType = if (todo.isComplete) ActionType.TODO_UNDO_COMPLETE else ActionType.TODO_COMPLETE
        RxFlux.postAction(actionType, successValue = todo.id)
    }

    fun toggleCompleteAll() = RxFlux.postAction(ActionType.TODO_TOGGLE_COMPLETE_ALL)

    fun destroyCompleted() = RxFlux.postAction(ActionType.TODO_DESTROY_COMPLETED)
}