package com.example.todolist.network

import com.example.todolist.models.Todo

data class ApiTodo(
    val userId: Int,
    val id: Int?,
    val title: String,
    val completed: Boolean
)

fun ApiTodo.toTodo(): Todo {
    return Todo(
        id = this.id,
        title = this.title,
        note = null,
        date = ""
    )
}
