package com.example.virtuula.model

import com.google.gson.annotations.SerializedName

data class Clases(
    @SerializedName("codClase" ) var codClase : Int?    = null,
    @SerializedName("nombre"   ) var nombre   : String? = null,
    @SerializedName("tamanio"  ) var tamanio  : Int?    = null
)