package com.example.virtuula.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Equipos (
    @SerializedName("codEquipo"        ) var codEquipo        : Int?    = null,
    @SerializedName("nomEquipo"        ) var nomEquipo        : String? = null,
    @SerializedName("estado"           ) var estado           : String? = null,
    @SerializedName("fechaAlta"        ) var fechaAlta        : String? = null,
    @SerializedName("especificaciones" ) var especificaciones : String? = null,
    @SerializedName("clase"            ) var clase            : Clases?  = Clases()
)