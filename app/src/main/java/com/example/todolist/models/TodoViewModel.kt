package com.example.todolist.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.database.TodoDatabase
import com.example.todolist.database.TodoRepository
import com.example.todolist.network.ApiTodo
import com.example.todolist.network.RetrofitInstance
import com.example.todolist.network.toTodo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TodoRepository
    val allTodo: LiveData<List<Todo>>

    init {
        val dao = TodoDatabase.getDatabase(application).getTodoDao()
        repository = TodoRepository(dao)
        allTodo = repository.allTodos
    }

    //Room
    fun insertTodo(todo: Todo)= viewModelScope.launch(Dispatchers.IO) {
        repository.insert(todo)
    }

    fun updateTodo(todo: Todo)= viewModelScope.launch(Dispatchers.IO) {
        repository.update(todo)
    }

    fun deleteTodo(todo: Todo)= viewModelScope.launch(Dispatchers.IO) {
        repository.delete(todo)
    }

    //Retrofit
    fun fetchTodosFromApi() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.getTodos()
            if (response.isSuccessful) {
                response.body()?.let { apiTodos ->
                    apiTodos.forEach { apiTodo ->
                        repository.insert(apiTodo.toTodo())
                    }
                    Log.d("TodoViewModel", "Dados baixados e salvos com sucesso: ${apiTodos.size} itens")
                }
            } else {
                Log.d("TodoViewModel", "Error: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TodoViewModel", "Falha na requisição: ${e.message}")
        }
    }

}