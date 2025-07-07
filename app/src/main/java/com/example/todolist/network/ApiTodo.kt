package com.example.todolist.network

import com.example.todolist.models.Todo

data class ApiTodo(
    val userId: Int,
    val id: Int?,
    val note: String,
    val title: String,
    val completed: Boolean
)

fun ApiTodo.toTodo(): Todo {
    return Todo(
        localId = null,  // LocalId gerado pelo Room
        apiId = this.id,
        title = this.title.ifBlank { "Sem título" }, // Garantir não nulo e não vazio
        note = "Sincronizado com API",
        date = "",
        synced = true
    )
}

fun Todo.toApiTodo(): ApiTodo {
    return ApiTodo(
        userId = 1, // Você pode definir de forma fixa ou dinâmica
        id = this.apiId,
        note= this.note,
        title = this.title,
        completed = false // Ou defina de acordo com seu app
    )
}


