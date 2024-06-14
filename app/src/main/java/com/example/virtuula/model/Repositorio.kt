package com.example.virtuula.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.virtuula.model.Retrofit.ListaReservas
import com.example.virtuula.model.Retrofit.VirtuaulaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repositorio(private val virtuaulaService: VirtuaulaService, private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    val reservas: LiveData<List<Reservas>> = liveData {
        val dni = sharedPreferences.getString("Dni", "Usuario no encontrado")
        val response = dni?.let { virtuaulaService.getReservasByDni(dni) }
        emit(response ?: emptyList())
    }

    val reservasTodas: LiveData<List<Reservas>> = liveData {
        val response = virtuaulaService.getReservasNoCaducadas()
        emit(response)
    }

    val sesiones: LiveData<List<Sesiones>> = liveData{
        val response=virtuaulaService.getAllSesiones()
        emit(response)
    }

    val equiposClase: LiveData<List<Equipos>> = liveData {
        val idClase=sharedPreferences.getInt("Cod Clase", 0)
        val r=virtuaulaService.getClasesById(idClase)
        val clase=r.body()
        val response= clase?.nombre?.let { virtuaulaService.getEquiposByNombreClase(it) }
        if (response != null) {
            emit(response)
        }
    }

    suspend fun reservasHoy(): List<Reservas> {
        return virtuaulaService.getReservasNoCaducadas()
    }

    suspend fun reservasClase(): List<Reservas> {
        val nombre = sharedPreferences.getString("Nombre Clase", "Clase no encontrada")
        return nombre?.let { virtuaulaService.getReservasByEquipoClaseNombreAndNoCaducadas(it) } ?: emptyList()
    }

    suspend fun reservasHora(): List<Reservas> {
        val hora = sharedPreferences.getString("Hora", null)
        Log.d("Repositorio", "Fetching reservations for hour: $hora")
        return hora?.let { virtuaulaService.getReservasByHoraAndNoCaducadas(it) } ?: emptyList()
    }
}
