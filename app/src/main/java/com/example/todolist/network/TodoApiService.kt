package com.example.todolist.network

import com.example.todolist.models.Todo
import retrofit2.Response
import retrofit2.http.*

interface TodoApiService {

    // Busca a lista de todos os todos
    @GET("todos")
    suspend fun getTodos(): Response<List<ApiTodo>>

    // Cria um novo tod enviando o objeto no corpo da requisição
    @POST("todos")
    suspend fun createTodo(@Body todo: ApiTodo): Response<ApiTodo>

    // Atualiza um tod pelo ID, enviando o objeto no corpo da requisição
    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body todo: ApiTodo): Response<ApiTodo>

    // Deleta um tod pelo ID, retornando Response<Unit> (sem corpo)
    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int): Response<Unit>
}