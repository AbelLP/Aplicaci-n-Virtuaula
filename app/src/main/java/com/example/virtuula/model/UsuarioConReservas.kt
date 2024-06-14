package com.example.virtuula.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.virtuula.model.Retrofit.ListaReservas

data class UsuarioConReservas(
    @Embedded
    val usuario:Usuarios,

    @Relation(
        parentColumn="dni",
        entityColumn="dniUsuario"
    )
    val reservas: ListaReservas

)
