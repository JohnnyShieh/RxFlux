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

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.johnny.rxfluxtodo.model.Todo
import com.johnny.rxfluxtodo.todo.TodoActionCreator
import kotlinx.android.synthetic.main.todo_row_layout.view.*


/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2017/3/28
 */
class TodoRecyclerAdapter(private val mActionCreator: TodoActionCreator) :
    RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder>() {

    private var mTodoList: List<Todo>? = null

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.todo_row_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.bindView(mActionCreator, mTodoList!![i])
    }

    override fun getItemCount(): Int {
        return if (null == mTodoList) 0 else mTodoList!!.size
    }

    fun setItems(todoList: List<Todo>) {
        mTodoList = todoList
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        fun bindView(actionCreator: TodoActionCreator, todo: Todo) {
            if (todo.isComplete) {
                val spanString = SpannableString(todo.text)
                spanString.setSpan(StrikethroughSpan(), 0, spanString.length, 0)
                itemView.tvTodoContent.text = spanString
            } else {
                itemView.tvTodoContent.text = todo.text
            }

            itemView.chkComplete.isChecked = todo.isComplete
            itemView.chkComplete.setOnClickListener { actionCreator.toggleComplete(todo) }

            itemView.btnDelete.setOnClickListener { actionCreator.destroy(todo.id) }
        }
    }
}
