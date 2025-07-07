package com.example.todolist.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.database.TodoDatabase
import com.example.todolist.database.TodoRepository
import com.example.todolist.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.todolist.utils.toApiTodo
import com.example.todolist.utils.toTodo


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
        val newTodo = todo.copy(synced = false, apiId = null)
        repository.insert(newTodo)
    }

    fun updateTodo(todo: Todo)= viewModelScope.launch(Dispatchers.IO) {
        val newTodo = todo.copy(synced = false)
        repository.update(newTodo)
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
                   // var newInsert = 0
                    apiTodos.forEach { apiTodo ->
                        val apiId = apiTodo.id
                        if (apiId != null) {
                            val existingTodo = repository.getTodoByApiId(apiId)
                            if (existingTodo == null) {
                                repository.insert(apiTodo.toTodo().copy(synced = true))
                              //  newInsert++
                            } else {
//                                // Atualiza apenas se o título mudar
                                if (existingTodo.title != apiTodo.title || existingTodo.note != apiTodo.note) {
                                    val updatedTodo = existingTodo.copy(
                                        title = apiTodo.title,
                                        note = apiTodo.note,
                                        synced = true
                                        )
                                    repository.update(updatedTodo)
                                }
                                Log.d("TodoViewModel", "Item já existe localmente, ignorando ID: $apiId")

                            }
                        } else {
                            Log.w("TodoViewModel", "Item da API sem ID: $apiTodo")
                        }
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


    fun createTodoApi(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.createTodo(todo.toApiTodo())
            if (response.isSuccessful) {
                response.body()?.let { apiTodo ->
                    val syncedTodo = todo.copy(apiId = apiTodo.id, synced = true)
                    repository.update(syncedTodo)                }
            } else {
                Log.e("TodoViewModel", "Erro ao criar tarefa via API")
            }
        } catch (e: Exception) {
            Log.e("TodoViewModel", "Exceção ao criar tarefa: ${e.message}")
        }
    }

    fun updateTodoApi(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val apiId = todo.apiId ?: return@launch
            val response = RetrofitInstance.api.updateTodo(apiId, todo.toApiTodo())
            if (response.isSuccessful) {
                val updated = todo.copy(synced = true)
                repository.update(updated)
            } else {
                Log.e("TodoViewModel", "Erro ao atualizar tarefa na API")
            }
        } catch (e: Exception) {
            Log.e("TodoViewModel", "Exceção ao atualizar tarefa: ${e.message}")
        }
    }

    fun deleteTodoApi(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val apiId = todo.apiId ?: return@launch
            val response = RetrofitInstance.api.deleteTodo(apiId)
            if (response.isSuccessful) {
                repository.delete(todo)
            } else {
                Log.e("TodoViewModel", "Erro ao deletar tarefa na API")
            }
        } catch (e: Exception) {
            Log.e("TodoViewModel", "Exceção ao deletar tarefa: ${e.message}")
        }
    }

    fun syncPendingTodos() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val unsyncedTodos = repository.getUnsyncedTodos()
            unsyncedTodos.forEach { todo ->
                val apiTodo = todo.toApiTodo()
                val response = if (todo.apiId == null) {
                    RetrofitInstance.api.createTodo(apiTodo)
                } else {
                    RetrofitInstance.api.updateTodo(todo.apiId, apiTodo)
                }

                if (response.isSuccessful) {
                    response.body()?.let { syncedApiTodo ->
                        val syncedTodo = todo.copy(apiId = syncedApiTodo.id, synced = true)
                        repository.update(syncedTodo)
                        Log.d("TodoViewModel", "Tarefa sincronizada com sucesso: ${syncedTodo.title}")
                    }
                } else {
                    Log.w("TodoViewModel", "Falha ao sincronizar tarefa: ${todo.title}")
                }
            }
        } catch (e: Exception) {
            Log.e("TodoViewModel", "Erro na sincronização: ${e.message}")
        }
    }
}