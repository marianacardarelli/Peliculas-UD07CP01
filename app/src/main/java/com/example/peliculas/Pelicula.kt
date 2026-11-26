package com.example.peliculas

import java.io.Serializable

data class Pelicula(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val imagen: Int,
    val duracion: Int,
    val fechaEstreno: Int,
    val produccion: String
): Serializable