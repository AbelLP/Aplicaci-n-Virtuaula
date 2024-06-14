package com.example.virtuula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtuula.ClassComputerListFragment
import com.example.virtuula.model.Repositorio

class ClassComputerListFragmentViewModelFactory(val repositorio: Repositorio): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClassComputerListFragmentViewModel (repositorio) as T
    }
}