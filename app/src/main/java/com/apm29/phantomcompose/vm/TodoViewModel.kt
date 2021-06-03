package com.apm29.phantomcompose.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.apm29.phantomcompose.ui.sample.Todo
import com.apm29.phantomcompose.ui.sample.TodoStatus

class TodoViewModel : ViewModel() {


    private val _todoList = mutableStateListOf(
        *((0..10).map { Todo("待办事项$it") }).toTypedArray()
    )

    val todoList: List<Todo>
        get() = _todoList

    fun changeStatus(todo: Todo, status: TodoStatus) {
        _todoList[_todoList.indexOf(todo)] = todo.copy(
            status = status
        )
    }

    fun addItem(task: String) {
        _todoList.add(Todo(task))
    }

}

