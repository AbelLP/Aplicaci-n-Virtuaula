package com.example.virtuula

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.virtuula.databinding.FragmentAdministerBinding
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AdministerFragment : Fragment() {
    lateinit var binding:FragmentAdministerBinding
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_administer, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding.btnCrearClases.setOnClickListener(){
            it.findNavController().navigate(R.id.action_administerFragment_to_createClassFragment)
        }
        binding.btnAccederClase.setOnClickListener(){
            try{
                GlobalScope.launch(Dispatchers.Main) {
                    val clases=retrofit.getClases()
                    if(clases!=null){
                        val nombreClases=clases?.map {it.nombre}?.toTypedArray()
                        nombreClases?.let {
                            val builder=AlertDialog.Builder(requireContext())
                            builder.setTitle("Seleccione una clase").setItems(it){ dialog, which ->
                                val claseSeleccionada=clases[which]
                                val codSeleccionado=claseSeleccionada.codClase
                                if (codSeleccionado != null) {
                                    sharedPreferences.edit().putInt("Id Clase", codSeleccionado).apply()
                                }
                                findNavController().navigate(R.id.action_administerFragment_to_classMenuFragment)
                            }.setNegativeButton("Cancelar") {dialog, _ ->
                                dialog.dismiss()
                            }
                            val dialog=builder.create()
                            dialog.show()
                        }

                    }else{
                        Toast.makeText(requireContext(), "No hay clases en la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(), "Algo ha salido mal", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnModificarClases.setOnClickListener(){
            try{
                GlobalScope.launch(Dispatchers.Main) {
                    val clases=retrofit.getClases()
                    if(clases!=null){
                        val nombreClases=clases?.map {it.nombre}?.toTypedArray()
                        nombreClases?.let {
                            val builder=AlertDialog.Builder(requireContext())
                            builder.setTitle("Seleccione una clase").setItems(it){ dialog, which ->
                                val claseSeleccionada=clases[which]
                                val codSeleccionado=claseSeleccionada.codClase
                                if (codSeleccionado != null) {
                                    sharedPreferences.edit().putInt("Cod Clase", codSeleccionado).apply()
                                }
                                findNavController().navigate(R.id.action_administerFragment_to_updateClassFragment)
                            }.setNegativeButton("Cancelar") {dialog, _ ->
                                dialog.dismiss()
                            }
                            val dialog=builder.create()
                            dialog.show()
                        }

                    }else{
                        Toast.makeText(requireContext(), "No hay clases en la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(), "Algo ha salido mal", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSesiones.setOnClickListener(){
            it.findNavController().navigate(R.id.action_administerFragment_to_sessionsListFragment)
        }

        binding.btnReservasAdmin.setOnClickListener(){
            it.findNavController().navigate(R.id.action_administerFragment_to_administerBookingListFragment)
        }

        return binding.root
    }


}