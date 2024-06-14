package com.example.virtuula.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.virtuula.model.Repositorio
import com.example.virtuula.model.Reservas
import kotlinx.coroutines.launch

class AdministerBookingListFragmentViewModel(val repositorio: Repositorio): ViewModel() {
    private val _reservas = MutableLiveData<List<Reservas>>()
    val reservas: LiveData<List<Reservas>> get() = _reservas

    fun cargarReservasClase() {
        viewModelScope.launch {
            val response = repositorio.reservasClase()
            _reservas.postValue(response)
        }
    }

    fun cargarReservasHora() {
        viewModelScope.launch {
            Log.d("ViewModel", "Loading hour reservations")
            val response = repositorio.reservasHora()
            Log.d("ViewModel", "Loaded hour reservations: ${response.size}")
            _reservas.postValue(response)
        }
    }

    /*fun cargarReservas() {
        viewModelScope.launch {
            Log.d("ViewModel", "Loading all reservations")
            val response = repositorio.reservasTodas()
            Log.d("ViewModel", "Loaded all reservations: ${response.size}")
            _reservas.postValue(response)
        }
    }*/

    fun cargarReservasHoy() {
        viewModelScope.launch {
            val response = repositorio.reservasHoy()
            Log.d("ViewModel", "Loaded today's reservations: ${response.size}")
            _reservas.postValue(response)
        }
    }
}
