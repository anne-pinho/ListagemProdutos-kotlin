package com.example.todolist

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTodoBinding
import com.example.todolist.models.Todo
import java.text.SimpleDateFormat
import java.util.*

class AddTodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTodoBinding
    private lateinit var todo: Todo
    private lateinit var oldTodo: Todo
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera o todo passado por Intent, de forma compatível com qualquer versão
        try {
            oldTodo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("current_todo", Todo::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("current_todo") as Todo
            }

            binding.etTitle.setText(oldTodo.title)
            binding.etNote.setText(oldTodo.note)
            isUpdate = true

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Mostrar ou esconder o botão de deletar
        binding.imgDelete.visibility = if (isUpdate) View.VISIBLE else View.INVISIBLE

        // Botão de salvar (check)
        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val todoDescription = binding.etNote.text.toString()

            if (title.isNotEmpty() && todoDescription.isNotEmpty()) {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

                todo = if (isUpdate) {
                    Todo(oldTodo.id, title, todoDescription, formatter.format(Date()))
                } else {
                    Todo(null, title, todoDescription, formatter.format(Date()))
                }

                val resultIntent = Intent()
                resultIntent.putExtra("todo", todo)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()

            } else {
                Toast.makeText(this, "Please enter some data", Toast.LENGTH_LONG).show()
            }
        }

        // Botão de deletar
        binding.imgDelete.setOnClickListener {
            val deleteIntent = Intent()
            deleteIntent.putExtra("todo", oldTodo)
            deleteIntent.putExtra("delete_todo", true)
            setResult(Activity.RESULT_OK, deleteIntent)
            finish()
        }

        // Botão de voltar
        binding.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
