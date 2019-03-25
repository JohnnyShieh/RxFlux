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
package com.johnny.rxfluxtodo.store

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.johnny.rxflux.Action
import com.johnny.rxflux.Store
import com.johnny.rxfluxtodo.action.ActionType
import com.johnny.rxfluxtodo.model.Todo

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2018/3/29
 */
class TodoStore : Store() {

    private val list = mutableListOf<Todo>()

    private var lastDeleted = MutableLiveData<Todo>()

    val todoList = MutableLiveData<List<Todo>>().apply { value = list }

    val canUndo = Transformations.map(lastDeleted) { lastDeleted -> lastDeleted != null }

    override fun onAction(action: Action) {
        when (action.type) {
            ActionType.TODO_CREATE -> create(action.singleData as String)
            ActionType.TODO_DESTROY -> destroy(action.singleData as Long)
            ActionType.TODO_UNDO_DESTROY -> undoDestroy()
            ActionType.TODO_COMPLETE -> updateComplete(action.singleData as Long, true)
            ActionType.TODO_UNDO_COMPLETE -> updateComplete(action.singleData as Long, false)
            ActionType.TODO_DESTROY_COMPLETED -> destroyCompleted()
            ActionType.TODO_TOGGLE_COMPLETE_ALL -> toggleCompleteAll()
        }
    }

    override fun onError(action: Action) {
    }

    private fun destroyCompleted() {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().isComplete) {
                iterator.remove()
            }
        }
        todoList.value = list
    }

    private fun toggleCompleteAll() {
        if (areAllComplete()) {
            updateAllComplete(false)
        } else {
            updateAllComplete(true)
        }
    }

    private fun areAllComplete(): Boolean {
        for (todo in list) {
            if (!todo.isComplete) {
                return false
            }
        }
        return true
    }

    private fun updateAllComplete(complete: Boolean) {
        list.forEach { it.isComplete = complete }
        todoList.value = list
    }

    private fun updateComplete(id: Long, complete: Boolean) {
        list.firstOrNull { it.id == id }?.isComplete = complete
        todoList.value = list
    }

    private fun undoDestroy() {
        lastDeleted.value?.let {
            addElement(it)
            lastDeleted.value = null
        }
    }

    private fun create(text: String) = addElement(Todo(System.currentTimeMillis(), text))

    private fun destroy(id: Long) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val todo = iterator.next()
            if (todo.id == id) {
                lastDeleted.value = todo
                iterator.remove()
                break
            }
        }
        todoList.value = list
    }

    private fun addElement(todo: Todo) {
        list.add(todo)
        list.sort()
        todoList.value = list
    }

}