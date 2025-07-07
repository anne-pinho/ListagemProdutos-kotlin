package com.example.todolist.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "todo_table")
data class Todo (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "localId") val localId: Int? = null,
    @ColumnInfo(name = "apiId") val apiId: Int? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "synced") val synced: Boolean = false
): java.io.Serializable
