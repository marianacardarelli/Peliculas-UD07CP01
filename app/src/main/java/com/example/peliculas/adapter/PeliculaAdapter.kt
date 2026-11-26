package com.example.peliculas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.peliculas.Pelicula
import com.example.peliculas.databinding.ItemPeliculaBinding

class PeliculaAdapter(
    private val onClickListener: (Pelicula) -> Unit,
) : ListAdapter<Pelicula, PeliculaViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val binding = ItemPeliculaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PeliculaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        holder.render(getItem(position), onClickListener)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Pelicula>() {
            override fun areItemsTheSame(oldItem: Pelicula, newItem: Pelicula): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Pelicula, newItem: Pelicula): Boolean =
                oldItem == newItem
        }
    }
}