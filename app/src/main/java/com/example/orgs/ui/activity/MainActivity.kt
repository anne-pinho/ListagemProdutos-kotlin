package com.example.orgs.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orgs.R
import com.example.orgs.model.Produto
import com.example.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import java.math.BigDecimal

class MainActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val nome = findViewById<TextView>(R.id.nome)
//        nome.text = "Cesta de Frutas"
//        val descricao = findViewById<TextView>(R.id.descricao)
//        descricao.text = "Laranja, manga e uva"
//        val valor = findViewById<TextView>(R.id.valor)
//        valor.text = "19,99"

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = ListaProdutosAdapter(this, listOf(
            Produto(nome = "teste",
                descricao = "teste desc",
                valor = BigDecimal("19.99")
            ),
            Produto(nome = "teste 1",
                descricao = "teste desc 1",
                valor = BigDecimal("29.99")
            ),
        ))
        //recyclerView.layoutManager = LinearLayoutManager(this)
    }
}