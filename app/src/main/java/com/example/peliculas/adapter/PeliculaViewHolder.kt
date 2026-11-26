package com.example.peliculas.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.peliculas.Pelicula
import com.example.peliculas.databinding.ItemPeliculaBinding

class PeliculaViewHolder(private val binding: ItemPeliculaBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(pelicula: Pelicula, onClickListener: (Pelicula) -> Unit) {
        binding.tvPeliculaTitulo.text = pelicula.titulo
        Glide.with(binding.ivPelicula.context)
            .load(pelicula.imagen)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivPelicula)
        binding.root.setOnClickListener {
            onClickListener(pelicula)
        }
    }
}