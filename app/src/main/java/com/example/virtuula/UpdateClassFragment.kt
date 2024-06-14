package com.example.virtuula

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.virtuula.databinding.FragmentUpdateClassBinding
import com.example.virtuula.model.Clases
import com.example.virtuula.model.Equipos
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateClassFragment : Fragment() {

    lateinit var binding:FragmentUpdateClassBinding
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_update_class, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id=sharedPreferences.getInt("Id Clase", 0)
        var claseRecogida:Clases
        GlobalScope.launch (Dispatchers.Main){
            val response=retrofit.getClasesById(id)
            claseRecogida= response.body()!!
            var codCl:String?=claseRecogida.codClase.toString()
            var nomCl:String?=claseRecogida.nombre.toString()
            var tamCl:String?=claseRecogida.tamanio.toString()
            binding.etIdClase2.setText(codCl)
            binding.etNombreClase2.setText(nomCl)
            binding.etTamanioClase2.setText(tamCl)
        }
        binding.btnAceptarCC2.setOnClickListener(){
            val idS=binding.etIdClase2.text.toString()
            val id=idS.toInt()
            val nombre=binding.etNombreClase2.text.toString()
            val tamanioS=binding.etTamanioClase2.text.toString()
            val tamanio=tamanioS.toInt()
            val clase= Clases(id, nombre, tamanio)
            GlobalScope.launch (Dispatchers.Main) {
                retrofit.updateClases(clase)
                Toast.makeText(requireContext(), "Clase actualizada con éxito", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().remove("Id Clase").apply()
                it.findNavController().navigate(R.id.action_updateClassFragment_to_administerFragment)
            }
        }
        binding.btnBorrarCls.setOnClickListener(){
            GlobalScope.launch (Dispatchers.Main){
                val mensaje=retrofit.deleteClases(sharedPreferences.getInt("Id Clase", 0))
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().remove("Id Clase").apply()
                it.findNavController().navigate(R.id.action_updateClassFragment_to_administerFragment)
            }

        }

        return binding.root
    }


}