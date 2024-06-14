package com.example.virtuula.model

import com.google.gson.annotations.SerializedName

data class Usuarios (
    @SerializedName("dni")
    var dni:String="",
    @SerializedName("nombre")
    var nombre:String="",
    @SerializedName("apellidos")
    var apellidos:String="",
    @SerializedName("telefono")
    var telefono:String="",
    @SerializedName("email")
    var email:String="",
    @SerializedName("clave")
    var clave:String="",
    @SerializedName("cambiar_clave")
    var cambiar_clave:Boolean=false,
    @SerializedName("descripcion")
    var descripcion:String=""
)