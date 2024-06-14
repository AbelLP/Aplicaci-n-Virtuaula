package com.example.virtuula.viewmodel

import androidx.lifecycle.ViewModel
import com.example.virtuula.model.Repositorio

class SessionsListFragmentViewModel(val repositorio: Repositorio): ViewModel() {
    val sesiones=repositorio.sesiones
}