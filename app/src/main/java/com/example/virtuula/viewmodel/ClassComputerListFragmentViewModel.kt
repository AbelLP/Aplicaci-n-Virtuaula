package com.example.virtuula.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.virtuula.model.Repositorio

class ClassComputerListFragmentViewModel(val repositorio: Repositorio): ViewModel() {
    val equiposClase=repositorio.equiposClase
}