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
import com.example.virtuula.databinding.FragmentCreateClassBinding
import com.example.virtuula.model.Clases
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreateClassFragment : Fragment() {

    lateinit var binding:FragmentCreateClassBinding
    private lateinit var sharedPreferences: SharedPreferences
    val retrofit=RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_create_class, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding.btnAceptarCC.setOnClickListener(){
            val idS=binding.etIdClase.text.toString()
            val id=idS.toInt()
            val nombre=binding.etNombreClase.text.toString()
            val tamanioS=binding.etTamanioClase.text.toString()
            val tamanio=tamanioS.toInt()
            GlobalScope.launch (Dispatchers.Main){
                try{
                    if((idS!=null||idS!="")&&(nombre!=null||nombre!="")&&(tamanioS!=null||tamanioS!="")){
                        val response=retrofit.getClasesById(id)
                        if(response.body()==null){
                            val clase= Clases(id, nombre, tamanio)
                            retrofit.addClases(clase)
                            Toast.makeText(requireContext(), "Clase creada con éxito.", Toast.LENGTH_SHORT).show()
                            sharedPreferences.edit().putInt("Cod Clase", id).apply()
                            it.findNavController().navigate(R.id.action_createClassFragment_to_classMenuFragment)
                        }else{
                            Toast.makeText(requireContext(), "Esta clase ya está registrada en la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }else if((idS==null||idS=="")&&(nombre!=null||nombre!="")&&(tamanioS!=null||tamanioS!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Id", Toast.LENGTH_SHORT).show()
                    }else if((idS!=null||idS!="")&&(nombre==null||nombre=="")&&(tamanioS!=null||tamanioS!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Nombre", Toast.LENGTH_SHORT).show()
                    }else if((idS!=null||idS!="")&&(nombre!=null||nombre!="")&&(tamanioS==null||tamanioS=="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Tamaño", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show()
                    }

                }catch(e:Exception){

                }
            }
        }
        return binding.root
    }

}