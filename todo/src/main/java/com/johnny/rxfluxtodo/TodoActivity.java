package com.johnny.rxfluxtodo;
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

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.johnny.rxflux.Dispatcher;
import com.johnny.rxflux.Store;
import com.johnny.rxflux.StoreObserver;
import com.johnny.rxfluxtodo.action.ActionType;
import com.johnny.rxfluxtodo.action.TodoActionCreator;
import com.johnny.rxfluxtodo.store.TodoStore;

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2017/3/28
 */
public class TodoActivity extends AppCompatActivity implements StoreObserver<Store.StoreChangeEvent>{

    private EditText vMainEdit;
    private ViewGroup vMainGroup;
    private CheckBox vMainCheck;

    private TodoRecyclerAdapter mAdapter;

    private TodoActionCreator mActionCreator;
    private TodoStore mTodoStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDependencies();
        setupView();
    }

    private void initDependencies() {
        mActionCreator = new TodoActionCreator();
        mTodoStore = new TodoStore();
    }

    private void setupView() {
        vMainGroup = ((ViewGroup) findViewById(R.id.main_layout));
        vMainEdit = (EditText) findViewById(R.id.main_input);

        Button mainAdd = (Button) findViewById(R.id.main_add);
        mainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTodo();
                resetMainInput();
            }
        });
        vMainCheck = (CheckBox) findViewById(R.id.main_checkbox);
        vMainCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAll();
            }
        });
        Button mainClearCompleted = (Button) findViewById(R.id.main_clear_completed);
        mainClearCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCompleted();
                resetMainCheck();
            }
        });


        RecyclerView mainList = (RecyclerView) findViewById(R.id.main_list);
        mainList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TodoRecyclerAdapter(mActionCreator);
        mainList.setAdapter(mAdapter);
    }

    private void updateUI() {
        mAdapter.setItems(mTodoStore.getTodoList());

        if (mTodoStore.canUndo()) {
            Snackbar snackbar = Snackbar.make(vMainGroup, "Element deleted", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActionCreator.undoDestroy();
                }
            });
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTodoStore.setObserver(this);
        Dispatcher.get().register(mTodoStore,
            ActionType.TODO_COMPLETE,
            ActionType.TODO_CREATE,
            ActionType.TODO_DESTROY,
            ActionType.TODO_DESTROY_COMPLETED,
            ActionType.TODO_TOGGLE_COMPLETE_ALL,
            ActionType.TODO_UNDO_COMPLETE,
            ActionType.TODO_UNDO_DESTROY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTodoStore.unRegister();
    }

    private void addTodo() {
        if (validateInput()) {
            mActionCreator.create(getInputText());
        }
    }

    private void checkAll() {
        mActionCreator.toggleCompleteAll();
    }

    private void clearCompleted() {
        mActionCreator.destroyCompleted();
    }

    private void resetMainInput() {
        vMainEdit.setText("");
    }

    private void resetMainCheck() {
        if (vMainCheck.isChecked()) {
            vMainCheck.setChecked(false);
        }
    }

    private boolean validateInput() {
        return !TextUtils.isEmpty(getInputText());
    }

    private String getInputText() {
        return vMainEdit.getText().toString();
    }

    @Override
    public void onChange(Store.StoreChangeEvent event) {
        updateUI();
    }

    @Override
    public void onError(Store.StoreChangeEvent event) {

    }
}
