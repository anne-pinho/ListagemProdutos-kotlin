package com.example.todolist

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.models.Todo
import com.example.todolist.databinding.ActivityAddTodoBinding
import java.text.SimpleDateFormat
import java.util.Date


class AddTodoActivity: AppCompatActivity(){
    private lateinit var binding: ActivityAddTodoBinding
    private lateinit var todo: Todo
    private lateinit var oldTodo: Todo
    var isUpdate = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            intent.getSerializableExtra("current_todo", Todo::class.java)?.let {
                oldTodo = it
                binding.etTitle.setText(oldTodo.title)
                binding.etNote.setText(oldTodo.note)
                isUpdate = true
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

        if (isUpdate){
            binding.imgDelete.visibility = View.VISIBLE
        } else {
            binding.imgDelete.visibility = View.INVISIBLE

        }

        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val todoDescription = binding.etNote.text.toString()

            if (title.isNotEmpty() && todoDescription.isNotEmpty()){
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")

                if (isUpdate){
                    todo = Todo(oldTodo.id, title, todoDescription, formatter.format(Date()))
                } else{
                    todo = Todo(null, title, todoDescription, formatter.format(Date()))
                }

                var intent = Intent()
                intent.putExtra("todo", todo)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this@AddTodoActivity, "please enter some data", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        
    }
}