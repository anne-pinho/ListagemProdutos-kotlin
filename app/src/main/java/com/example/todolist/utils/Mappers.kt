package com.example.todolist.utils

import com.example.todolist.models.Todo
import com.example.todolist.network.ApiTodo

fun ApiTodo.toTodo(): Todo {
    return Todo(
        apiId = this.id,
        title = this.title,
        note = "",
        date = ""
    )
}

fun Todo.toApiTodo(): ApiTodo {
    return ApiTodo(
        userId = 1,
        id = this.apiId?:0,
        title = this.title,
        note = this.note,
        completed = false
    )
}
