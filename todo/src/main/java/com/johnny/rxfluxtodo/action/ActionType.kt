package com.johnny.rxfluxtodo.action

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
 *
 * Created on 2017/3/28
 */
object ActionType {
    val TODO_CREATE = "todo-create"
    val TODO_COMPLETE = "todo-complete"
    val TODO_DESTROY = "todo-destroy"
    val TODO_DESTROY_COMPLETED = "todo-destroy-completed"
    val TODO_TOGGLE_COMPLETE_ALL = "todo-toggle-complete-all"
    val TODO_UNDO_COMPLETE = "todo-undo-complete"
    val TODO_UNDO_DESTROY = "todo-undo-destroy"
}
