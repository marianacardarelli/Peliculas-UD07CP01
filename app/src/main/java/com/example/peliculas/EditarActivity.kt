package com.example.peliculas

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.peliculas.databinding.ActivityEditarBinding

class EditarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val tv = TypedValue()
            theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)
            val barra = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            v.setPadding(systemBars.left, systemBars.top + barra, systemBars.right, systemBars.bottom)
            insets
        }

        // Recoger la película que envió MainActivity
        val pelicula = intent.getSerializableExtra("pelicula", Pelicula::class.java) ?: return

        binding.ivPelicula.setImageResource(pelicula.imagen)
        binding.etTitulo.setText(pelicula.titulo)

        binding.btnCambiar.setOnClickListener {
            val nuevoTitulo = binding.etTitulo.text.toString().trim()
            if (nuevoTitulo.isEmpty()) {
                binding.etTitulo.error = getString(R.string.titulo_vacio)
                return@setOnClickListener
            }
            // Devolver el resultado a MainActivity
            setResult(
                RESULT_OK,
                Intent().putExtra("id", pelicula.id).putExtra("titulo", nuevoTitulo)
            )
            finish()
        }

        binding.btnCancelar.setOnClickListener {
            finish() // sin resultado: no se cambia nada
        }
    }
}