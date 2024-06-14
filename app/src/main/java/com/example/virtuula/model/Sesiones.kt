package com.example.virtuula.model

import com.google.gson.annotations.SerializedName

data class Sesiones (
    @SerializedName("idSesion"     ) var idSesion     : Int?      = null,
    @SerializedName("fechaHoraComienzo" ) var fechaHoraComienzo : String?   = null,
    @SerializedName("fechaHoraFin"      ) var fechaHoraFin      : String?   = null,
    @SerializedName("usuarios"     ) var usuarios     : Usuarios? = Usuarios(),
    @SerializedName("equipos"      ) var equipos      : Equipos?  = Equipos()
)