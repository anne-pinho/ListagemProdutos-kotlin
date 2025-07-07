package com.example.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.models.Todo

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Query("SELECT * from todo_table order by localId ASC")
    fun getAllTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE apiId = :apiId LIMIT 1")
    suspend fun getTodoByApiId(apiId: Int): Todo?

    @Query("SELECT * FROM todo_table WHERE synced = 0")
    suspend fun getUnsyncedTodos(): List<Todo>

}