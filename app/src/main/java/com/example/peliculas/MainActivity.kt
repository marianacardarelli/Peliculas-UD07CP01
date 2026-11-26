package com.example.peliculas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peliculas.adapter.PeliculaAdapter
import com.example.peliculas.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listaPeliculas: MutableList<Pelicula> = mutableListOf()
    private lateinit var adapter: PeliculaAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var positionLongClicked: Int = -1

    private val editarLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val id = result.data?.getLongExtra("id", -1L) ?: -1L
            val nuevoTitulo = result.data?.getStringExtra("titulo").orEmpty()
            val posicion = listaPeliculas.indexOfFirst { it.id == id }
            if (posicion != -1 && nuevoTitulo.isNotBlank()) {
                listaPeliculas[posicion] = listaPeliculas[posicion].copy(titulo = nuevoTitulo)
                adapter.submitList(listaPeliculas.toList())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Altura de la barra de acción (para que no tape el contenido)
            val tv = TypedValue()
            theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)
            val barra = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            v.setPadding(systemBars.left, systemBars.top + barra, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Cargar los datos (una copia de la lista original)
        listaPeliculas.addAll(PeliculaProvider.cargarLista())

        // 2. Configurar el RecyclerView
        layoutManager = LinearLayoutManager(this)
        binding.rvPeliculas.layoutManager = layoutManager
        binding.rvPeliculas.setHasFixedSize(true)
        binding.rvPeliculas.itemAnimator = DefaultItemAnimator()

        // 3. Crear el adapter y pasarle la lista
        adapter = PeliculaAdapter { pelicula -> onItemSelected(pelicula) }
        binding.rvPeliculas.adapter = adapter
        adapter.submitList(listaPeliculas.toList())

        setupSwipeRefresh() //recarga la lista cuando haces scroll
        setupContextMenu() //menu contextual
    }

    private fun onItemSelected(pelicula: Pelicula) {
        val intent = Intent(this, DetalleActivity::class.java)
        intent.putExtra("pelicula", pelicula)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.recargar -> {
                recargarLista()
                true
            }
            R.id.limpiar -> {
                confirmarBorradoTotal()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun recargarLista() {
        listaPeliculas.clear()
        listaPeliculas.addAll(PeliculaProvider.cargarLista())
        adapter.submitList(listaPeliculas.toList())
    }

    //cuadro de dialoogo al querer borrar
    private fun confirmarBorradoTotal() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.eliminar_todas_peliculas))
            .setMessage(getString(R.string.confirmar_eliminar_todas))
            .setNeutralButton(getString(R.string.cerrar), null)
            .setPositiveButton(getString(R.string.aceptar)) { _, _ -> limpiarLista() }
            .show()
    }

    private fun limpiarLista() {
        if (listaPeliculas.isEmpty()) return
        listaPeliculas.clear()
        adapter.submitList(listaPeliculas.toList())
        Snackbar.make(binding.root, getString(R.string.lista_eliminada), Snackbar.LENGTH_LONG).show()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            recargarLista()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupContextMenu() {
        binding.rvPeliculas.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    val child = rv.findChildViewUnder(e.x, e.y)
                    if (child != null) {
                        child.setOnLongClickListener {
                            val position = rv.getChildAdapterPosition(child)
                            if (position == RecyclerView.NO_POSITION) {
                                return@setOnLongClickListener false
                            }
                            positionLongClicked = position
                            registerForContextMenu(child)
                            openContextMenu(child)
                            unregisterForContextMenu(child)
                            true
                        }
                    }
                }
                return false
            }
        })
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_contextual, menu)
        if (positionLongClicked in listaPeliculas.indices) {
            menu.setHeaderTitle(listaPeliculas[positionLongClicked].titulo)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (positionLongClicked !in listaPeliculas.indices) {
            return super.onContextItemSelected(item)
        }
        return when (item.itemId) {
            R.id.action_edit -> {
                editarPelicula(positionLongClicked)
                true
            }
            R.id.action_delete -> {
                confirmarEliminar(positionLongClicked)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun confirmarEliminar(position: Int) {
        val pelicula = listaPeliculas[position]
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.eliminar_pelicula, pelicula.titulo))
            .setMessage(getString(R.string.confirmar_eliminar_pelicula, pelicula.titulo))
            .setNeutralButton(getString(R.string.cerrar), null)
            .setPositiveButton(getString(R.string.aceptar)) { _, _ ->
                listaPeliculas.remove(pelicula)
                adapter.submitList(listaPeliculas.toList())
            }
            .show()
    }
    private fun editarPelicula(position: Int) {
        val intent = Intent(this, EditarActivity::class.java)
        intent.putExtra("pelicula", listaPeliculas[position])
        editarLauncher.launch(intent)
    }
}