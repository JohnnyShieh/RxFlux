package com.johnny.rxfluxtodo

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

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.johnny.rxfluxtodo.action.ActionType
import com.johnny.rxfluxtodo.action.TodoActionCreator
import com.johnny.rxfluxtodo.store.TodoStore

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2017/3/28
 */
class TodoActivity : AppCompatActivity() {

    private lateinit var vMainEdit: EditText
    private lateinit var vMainGroup: ViewGroup
    private lateinit var vMainCheck: CheckBox

    private lateinit var mAdapter: TodoRecyclerAdapter

    private lateinit var mActionCreator: TodoActionCreator
    private lateinit var mTodoStore: TodoStore

    private val inputText: String
        get() = vMainEdit.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDependencies()
        setupView()
    }

    private fun initDependencies() {
        mActionCreator = TodoActionCreator()
        mTodoStore = ViewModelProviders.of(this).get(TodoStore::class.java)
        mTodoStore.register(
            ActionType.TODO_COMPLETE,
            ActionType.TODO_CREATE,
            ActionType.TODO_DESTROY,
            ActionType.TODO_DESTROY_COMPLETED,
            ActionType.TODO_TOGGLE_COMPLETE_ALL,
            ActionType.TODO_UNDO_COMPLETE,
            ActionType.TODO_UNDO_DESTROY
        )
    }

    private fun setupView() {
        vMainGroup = findViewById(R.id.main_layout)
        vMainEdit = findViewById(R.id.main_input)

        val mainAdd = findViewById<Button>(R.id.main_add)
        mainAdd.setOnClickListener {
            addTodo()
            resetMainInput()
        }
        vMainCheck = findViewById(R.id.main_checkbox)
        vMainCheck.setOnClickListener { checkAll() }
        val mainClearCompleted = findViewById<Button>(R.id.main_clear_completed)
        mainClearCompleted.setOnClickListener {
            clearCompleted()
            resetMainCheck()
        }

        val mainList = findViewById<RecyclerView>(R.id.main_list)
        mainList.layoutManager = LinearLayoutManager(this)
        mAdapter = TodoRecyclerAdapter(mActionCreator)
        mainList.adapter = mAdapter

        mTodoStore.todoList.observe(this, Observer { mAdapter.setItems(mTodoStore.todoList.value!!) })
        mTodoStore.canUndo.observe(this, Observer { canUndo ->
            if (canUndo == true) {
                val snackbar = Snackbar.make(vMainGroup, "Element deleted", Snackbar.LENGTH_LONG)
                snackbar.setAction("Undo") { mActionCreator.undoDestroy() }
                snackbar.show()
            }
        })
    }

    private fun addTodo() {
        if (validateInput()) {
            mActionCreator.create(inputText)
        }
    }

    private fun checkAll() {
        mActionCreator.toggleCompleteAll()
    }

    private fun clearCompleted() {
        mActionCreator.destroyCompleted()
    }

    private fun resetMainInput() {
        vMainEdit.setText("")
    }

    private fun resetMainCheck() {
        if (vMainCheck.isChecked) {
            vMainCheck.isChecked = false
        }
    }

    private fun validateInput(): Boolean {
        return !TextUtils.isEmpty(inputText)
    }
}
