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

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.johnny.rxflux.RxFlux
import com.johnny.rxfluxtodo.todo.TodoActionCreator
import com.johnny.rxfluxtodo.todo.TodoStore
import kotlinx.android.synthetic.main.activity_main.*

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2017/3/28
 */
class TodoActivity : AppCompatActivity() {

    private lateinit var mAdapter: TodoRecyclerAdapter

    private lateinit var mActionCreator: TodoActionCreator
    private lateinit var mTodoStore: TodoStore

    private val inputText: String
        get() = edtInput.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxFlux.enableRxFluxLog(true)
        setContentView(R.layout.activity_main)
        initDependencies()
        setupView()
    }

    private fun initDependencies() {
        mActionCreator = TodoActionCreator()
        mTodoStore = ViewModelProviders.of(this).get(TodoStore::class.java)
    }

    private fun setupView() {
        btnAddTodo.setOnClickListener {
            addTodo()
            resetMainInput()
        }
        chkAllComplete.setOnClickListener { checkAll() }
        btnClearCompleted.setOnClickListener {
            clearCompleted()
            resetMainCheck()
        }

        rvTodo.layoutManager = LinearLayoutManager(this)
        mAdapter = TodoRecyclerAdapter(mActionCreator)
        rvTodo.adapter = mAdapter

        mTodoStore.todoList.observe(
            this,
            Observer { mAdapter.setItems(mTodoStore.todoList.value!!) })
        mTodoStore.canUndo.observe(this, Observer { canUndo ->
            if (canUndo == true) {
                val snackbar = Snackbar.make(vContainer, "Element deleted", Snackbar.LENGTH_LONG)
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
        edtInput.setText("")
    }

    private fun resetMainCheck() {
        if (chkAllComplete.isChecked) {
            chkAllComplete.isChecked = false
        }
    }

    private fun validateInput(): Boolean {
        return !TextUtils.isEmpty(inputText)
    }
}
