package com.example.virtuula.viewmodel

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import com.example.virtuula.model.Repositorio
import com.example.virtuula.model.Retrofit.VirtuaulaService

class UserFragmentViewModel (val repositorio: Repositorio): ViewModel(){
   val reservas=repositorio.reservas
}