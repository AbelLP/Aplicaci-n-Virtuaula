package com.example.virtuula.model.Retrofit

import com.example.virtuula.model.Usuarios
import com.google.gson.annotations.SerializedName

data class ListaUsuarios(
    @SerializedName("results")
    val usuarios:List<Usuarios>
)