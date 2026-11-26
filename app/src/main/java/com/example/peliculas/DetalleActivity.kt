package com.example.peliculas

import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.peliculas.databinding.ActivityDetalleBinding

class DetalleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetalleBinding.inflate(layoutInflater)
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

        supportActionBar?.title = pelicula.titulo

        binding.tvTitulo.text = pelicula.titulo
        binding.ivPelicula.setImageResource(pelicula.imagen)
        binding.tvDescripcion.text = getString(R.string.detalle_descripcion, pelicula.descripcion)
        binding.tvFecha.text = getString(R.string.detalle_fecha, pelicula.fechaEstreno)
        binding.tvDuracion.text = getString(R.string.detalle_duracion, pelicula.duracion)
        binding.tvProduccion.text = getString(R.string.detalle_produccion, pelicula.produccion)
    }
}