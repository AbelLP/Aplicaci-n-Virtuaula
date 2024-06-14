package com.example.virtuula.model

import com.google.gson.annotations.SerializedName

data class Programas (
    @SerializedName("cod_programa")
    var codPrograma:String,
    @SerializedName("nombre")
    var nombre:String,
    @SerializedName("categoria")
    var categoria:String,
    @SerializedName("fabricante")
    var fabricante:String,
)