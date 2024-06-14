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
import com.example.virtuula.databinding.FragmentClassMenuBinding
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.model.Usuarios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClassMenuFragment : Fragment() {
    lateinit var binding:FragmentClassMenuBinding
    private lateinit var sharedPreferences: SharedPreferences
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_class_menu, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        GlobalScope.launch(Dispatchers.Main) {
            val cod=sharedPreferences.getInt("Cod Clase", 0)
            val response=retrofit.getClasesById(cod)
            val clase=response.body()
            binding.tvAula.text=clase?.nombre
        }

        binding.btnCrearEquipos.setOnClickListener(){
            val dni=sharedPreferences.getString("Dni", "No encontrado")
            GlobalScope.launch(Dispatchers.Main) {
                val response= dni?.let { it1 -> retrofit.getUsuariosById(it1) }
                val tipoUsuario:String?= response?.body()?.descripcion
                if(tipoUsuario=="administrador"){
                    it.findNavController().navigate(R.id.action_classMenuFragment_to_createComputerFragment)
                }else if(tipoUsuario=="usuario"){
                    Toast.makeText(requireContext(), "Sólo los administradores pueden crear equipos.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnModificarEquipos.setOnClickListener(){
            val dni=sharedPreferences.getString("Dni", "No encontrado")
            GlobalScope.launch(Dispatchers.Main) {
                val response=dni?.let { it1 -> retrofit.getUsuariosById(it1) }
                val tipoUsuarios:String?=response?.body()?.descripcion
                if(tipoUsuarios=="usuario"){
                    Toast.makeText(requireContext(), "Solo los administradores pueden actualizar equipos.", Toast.LENGTH_SHORT).show()
                }else if(tipoUsuarios=="administrador"){
                    try{
                        GlobalScope.launch(Dispatchers.Main) {
                            val cod=sharedPreferences.getInt("Cod Clase", 0)
                            val response=retrofit.getClasesById(cod)
                            val clase=response.body()
                            val equipos= clase?.nombre?.let { it1 -> retrofit.getEquiposByNombreClase(it1) }
                            if(equipos!=null){
                                val nombreEquipos=equipos?.map {it.nomEquipo}?.toTypedArray()
                                nombreEquipos?.let {
                                    val builder= AlertDialog.Builder(requireContext())
                                    builder.setTitle("Seleccione un equipo").setItems(it){ dialog, which ->
                                        val equipoSeleccionado=equipos[which]
                                        val codSeleccionado=equipoSeleccionado.codEquipo
                                        if (codSeleccionado != null) {
                                            sharedPreferences.edit().putInt("Cod Equipo", codSeleccionado).apply()
                                        }
                                        findNavController().navigate(R.id.action_classMenuFragment_to_updateComputerFragment)
                                    }.setNegativeButton("Cancelar") {dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    val dialog=builder.create()
                                    dialog.show()
                                }
                            }else{
                                Toast.makeText(requireContext(), "No hay equipos en la base de datos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }catch (e: Exception){
                        Toast.makeText(requireContext(), "Algo ha salido mal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnUsarEquipo.setOnClickListener(){
            val sesionIniciada=sharedPreferences.getBoolean("Sesión iniciada", false)
            if(sesionIniciada==false){
                it.findNavController().navigate(R.id.action_classMenuFragment_to_classComputerListFragment)
            }else{
                val builder=AlertDialog.Builder(requireContext())
                builder.setTitle("Ya tiene una sesión iniciada. Para volver a esta página, tiene que cerrar la sesión actual. ¿Cerrar la sesión actual?").setPositiveButton("Aceptar"){ dialog,_ ->
                    val idSesion=sharedPreferences.getInt("Id sesion", 0)
                    val fechaHoraActual= LocalDateTime.now()
                    val formateador= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    val fechaHora=fechaHoraActual.format(formateador)
                    GlobalScope.launch(Dispatchers.Main) {
                        retrofit.aniadirHoraFin(idSesion, fechaHora)
                        val nom=sharedPreferences.getString("Nom Equipo", "No encontrado")
                        if (nom != null) {
                            retrofit.modifyEstadoALibre(nom)
                        }
                        sharedPreferences.edit().remove("Nom Equipo").apply()
                        val idReserva=sharedPreferences.getInt("Cod Reserva", 0)
                        if(idReserva!=0 && idReserva!=null){
                            retrofit.deleteReservas(idReserva.toLong())
                            sharedPreferences.edit().remove("Cod Reserva")
                        }
                        val builder=AlertDialog.Builder(requireContext())
                        builder.setTitle("La sesión ha sido cerrada con éxito.").setPositiveButton("Aceptar"){dialog,_ -> dialog.dismiss()}
                        sharedPreferences.edit().remove("Sesión iniciada").apply()
                        sharedPreferences.edit().remove("Nom Equipo").apply()
                        val dialog=builder.create()
                        dialog.show()
                    }
                }.setNegativeButton("Cancelar"){dialog,_ -> dialog.dismiss()}
                val dialog=builder.create()
                dialog.show()
            }
        }
        return binding.root
    }


}