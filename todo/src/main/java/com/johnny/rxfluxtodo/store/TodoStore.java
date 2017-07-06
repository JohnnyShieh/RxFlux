package com.johnny.rxfluxtodo.store;
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
import com.johnny.rxflux.Store;
import com.johnny.rxfluxtodo.action.ActionKey;
import com.johnny.rxfluxtodo.action.ActionType;
import com.johnny.rxfluxtodo.model.Todo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2017/3/28
 */
public class TodoStore extends Store{

    private final List<Todo> mTodoList;
    private Todo mLastDeleted;

    public TodoStore() {
        mTodoList = new ArrayList<>();
    }

    @Override
    protected boolean onAction(@Nonnull Action action) {
        long id;
        switch (action.getType()) {
            case ActionType.TODO_CREATE:
                String text = action.get(ActionKey.KEY_TEXT);
                create(text);
                break;

            case ActionType.TODO_DESTROY:
                id = action.get(ActionKey.KEY_ID);
                destroy(id);
                break;

            case ActionType.TODO_UNDO_DESTROY:
                undoDestroy();
                break;

            case ActionType.TODO_COMPLETE:
                id = action.get(ActionKey.KEY_ID);
                updateComplete(id, true);
                break;

            case ActionType.TODO_UNDO_COMPLETE:
                id = action.get(ActionKey.KEY_ID);
                updateComplete(id, false);
                break;

            case ActionType.TODO_DESTROY_COMPLETED:
                destroyCompleted();
                break;

            case ActionType.TODO_TOGGLE_COMPLETE_ALL:
                toggleCompleteAll();
                break;
        }
        return true;
    }

    @Override
    protected boolean onError(@Nonnull Action action, Throwable throwable) {

        return true;
    }

    public List<Todo> getTodoList() {
        return mTodoList;
    }

    public boolean canUndo() {
        return null != mLastDeleted;
    }

    private void destroyCompleted() {
        Iterator<Todo> iterator = mTodoList.iterator();
        while (iterator.hasNext()) {
            Todo todo = iterator.next();
            if (todo.isComplete()) {
                iterator.remove();
            }
        }
    }

    private void toggleCompleteAll() {
        if (areAllComplete()) {
            updateAllComplete(false);
        } else {
            updateAllComplete(true);
        }
    }

    private boolean areAllComplete() {
        for (Todo todo : mTodoList) {
            if (!todo.isComplete()) {
                return false;
            }
        }
        return true;
    }

    private void updateAllComplete(boolean complete) {
        for (Todo todo : mTodoList) {
            todo.setComplete(complete);
        }
    }

    private void updateComplete(long id, boolean complete) {
        Todo todo = getById(id);
        if (null != todo) {
            todo.setComplete(complete);
        }
    }

    private void undoDestroy() {
        if (null != mLastDeleted) {
            addElement(mLastDeleted.clone());
            mLastDeleted = null;
        }
    }

    private void create(String text) {
        long id = System.currentTimeMillis();
        Todo todo = new Todo(id, text);
        addElement(todo);
    }

    private void destroy(long id) {
        Iterator<Todo> iter = mTodoList.iterator();
        while (iter.hasNext()) {
            Todo todo = iter.next();
            if (todo.getId() == id) {
                mLastDeleted = todo.clone();
                iter.remove();
                break;
            }
        }
    }

    private Todo getById(long id) {
        Iterator<Todo> iter = mTodoList.iterator();
        while (iter.hasNext()) {
            Todo todo = iter.next();
            if (todo.getId() == id) {
                return todo;
            }
        }
        return null;
    }

    private void addElement(Todo clone) {
        mTodoList.add(clone);
        Collections.sort(mTodoList);
    }
}
