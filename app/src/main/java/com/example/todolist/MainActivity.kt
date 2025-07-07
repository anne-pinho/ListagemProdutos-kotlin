package com.example.todolist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.adaptors.TodoAdapter
import com.example.todolist.database.TodoDatabase
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.models.Todo
import com.example.todolist.models.TodoViewModel

class MainActivity : AppCompatActivity(), TodoAdapter.TodoClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: TodoDatabase
    private lateinit var addTodoLauncher: ActivityResultLauncher<Intent>
    private lateinit var updateOrDeleteTodo: ActivityResultLauncher<Intent>

    private val todoViewModel: TodoViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        if (savedInstanceState == null) {
            todoViewModel.fetchTodosFromApi()
        }

        // Inicializa os launchers
        addTodoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val todo = result.data?.getSerializableExtra("todo") as? Todo
                val isDelete = result.data?.getBooleanExtra("delete_todo", false) ?: false

                if (todo != null && !isDelete) {
                    val isUpdate = todo.localId != null
                    if (isUpdate) {
                        todoViewModel.updateTodo(todo)
                        todoViewModel.updateTodoApi(todo)
                        Log.d("MainActivity", "ToDo atualizado (addTodoLauncher): ${todo.title}")
                    } else {
                        todoViewModel.insertTodo(todo)
                        todoViewModel.createTodoApi(todo)
                        Log.d("MainActivity", "ToDo criado (addTodoLauncher): ${todo.title}")
                    }
                } else if (todo != null && isDelete) {
                    todoViewModel.deleteTodo(todo)
                    todoViewModel.deleteTodoApi(todo)
                    Log.d("MainActivity", "ToDo deletado (addTodoLauncher): ${todo.title}")
                }
            }
        }

        updateOrDeleteTodo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val todo = result.data?.getSerializableExtra("todo") as? Todo
                val isDelete = result.data?.getBooleanExtra("delete_todo", false) ?: false

                if (todo != null && !isDelete) {
                    todoViewModel.updateTodo(todo)
                    todoViewModel.updateTodoApi(todo)
                    Log.d("MainActivity", "ToDo atualizado (updateOrDelete): ${todo.title}")
                } else if (todo != null && isDelete) {
                    todoViewModel.deleteTodo(todo)
                    todoViewModel.deleteTodoApi(todo)
                    Log.d("MainActivity", "ToDo deletado (updateOrDelete): ${todo.title}")
                }
            }
        }

        // Inicializa UI após os launchers
        initUI()

        // Observa mudanças nos dados
        todoViewModel.allTodo.observe(this) { list ->
            list?.let {
                adapter.updateList(it)
            }
        }

        database = TodoDatabase.getDatabase(this)
    }

    override fun onResume() {
        super.onResume()
        todoViewModel.syncPendingTodos()
    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TodoAdapter(this, this)
        binding.recyclerView.adapter = adapter

        binding.fabAddTodo.setOnClickListener {
            val intent = Intent(this, AddTodoActivity::class.java)
            addTodoLauncher.launch(intent)
        }
    }

    override fun onItemClicked(todo: Todo) {
        val intent = Intent(this@MainActivity, AddTodoActivity::class.java)
        intent.putExtra("current_todo", todo)
        updateOrDeleteTodo.launch(intent)
    }
}
