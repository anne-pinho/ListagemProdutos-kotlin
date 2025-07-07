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
    private var isUpdate = false
    private var oldTodo: Todo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica se é atualização
        oldTodo = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("current_todo", Todo::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("current_todo") as? Todo
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        oldTodo?.let {
            binding.etTitle.setText(it.title)
            binding.etNote.setText(it.note)
            isUpdate = true
        }

        binding.imgDelete.visibility = if (isUpdate) View.VISIBLE else View.INVISIBLE

        // Botão de salvar
        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val note = binding.etNote.text.toString()

            if (title.isNotEmpty() && note.isNotEmpty()) {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
                val date = formatter.format(Date())

                val updatedTodo = Todo(
                    localId = oldTodo?.localId, // MANTÉM o ID ao atualizar
                    apiId = oldTodo?.apiId,     // Preserva apiId
                    title = title,
                    note = note,
                    date = date,
                    synced = false              // Marcar como não sincronizado após alteração
                )

                val resultIntent = Intent()
                resultIntent.putExtra("todo", updatedTodo)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show()
            }
        }

        // Botão de deletar
        binding.imgDelete.setOnClickListener {
            oldTodo?.let {
                val deleteIntent = Intent().apply {
                    putExtra("todo", it)
                    putExtra("delete_todo", true)
                }
                setResult(Activity.RESULT_OK, deleteIntent)
                finish()
            }
        }

        // Botão voltar
        binding.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
