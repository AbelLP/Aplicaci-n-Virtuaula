package com.example.virtuula.model

import com.google.gson.annotations.SerializedName

data class Reservas(
    @SerializedName("idReserva"   ) var idReserva   : Int?     = null,
    @SerializedName("fechaHoraReserva" ) var fechaHoraReserva : String?  = null,
    @SerializedName("caducado" ) var caducado : String?  = null,
    @SerializedName("clave") var clave: Int?    = null,
    @SerializedName("usuario"     ) var usuario     : Usuarios? = Usuarios(),
    @SerializedName("equipo"      ) var equipo      : Equipos?  = Equipos()
)