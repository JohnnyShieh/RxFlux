package com.johnny.rxfluxtodo.action;
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

import com.johnny.rxflux.Action;
import com.johnny.rxflux.Dispatcher;
import com.johnny.rxfluxtodo.model.Todo;

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2017/3/28
 */
public class TodoActionCreator {

    public void create(String text) {
        Action action = Action.type(ActionType.TODO_CREATE)
            .bundle(ActionKey.KEY_TEXT, text)
            .build();
        Dispatcher.get().postAction(action);
    }

    public void destroy(long id) {
        Action action = Action.type(ActionType.TODO_DESTROY)
            .bundle(ActionKey.KEY_ID, id)
            .build();
        Dispatcher.get().postAction(action);
    }

    public void undoDestroy() {
        Dispatcher.get().postAction(Action.type(ActionType.TODO_UNDO_DESTROY).build());
    }

    public void toggleComplete(Todo todo) {
        long id = todo.getId();
        String actionType = todo.isComplete() ? ActionType.TODO_UNDO_COMPLETE : ActionType.TODO_COMPLETE;
        Action action = Action.type(actionType)
            .bundle(ActionKey.KEY_ID, id)
            .build();
        Dispatcher.get().postAction(action);
    }

    public void toggleCompleteAll() {
        Dispatcher.get().postAction(Action.type(ActionType.TODO_TOGGLE_COMPLETE_ALL).build());
    }

    public void destroyCompleted() {
        Dispatcher.get().postAction(Action.type(ActionType.TODO_DESTROY_COMPLETED).build());
    }

}
