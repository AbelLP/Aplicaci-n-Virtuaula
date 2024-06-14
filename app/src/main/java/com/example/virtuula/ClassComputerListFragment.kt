package com.example.virtuula

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtuula.databinding.FragmentClassComputerListBinding
import com.example.virtuula.decoration.EspacioCardView
import com.example.virtuula.model.Equipos
import com.example.virtuula.model.Repositorio
import com.example.virtuula.model.Reservas
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.RetrofitInstance.Companion.gson
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.model.Sesiones
import com.example.virtuula.model.Usuarios
import com.example.virtuula.view.EquiposClaseRecyclerViewAdapter
import com.example.virtuula.viewmodel.ClassComputerListFragmentViewModel
import com.example.virtuula.viewmodel.ClassComputerListFragmentViewModelFactory
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.random.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClassComputerListFragment : Fragment() {

    lateinit var binding:FragmentClassComputerListBinding
    lateinit var viewModel: ClassComputerListFragmentViewModel
    private lateinit var sharedPreferences: SharedPreferences
    val virtuaula= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repositorio=Repositorio(virtuaula, requireContext())
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_class_computer_list, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var viewModelFactory=ClassComputerListFragmentViewModelFactory(repositorio)
        viewModel=ViewModelProvider(this, viewModelFactory).get(ClassComputerListFragmentViewModel::class.java)
        binding.lifecycleOwner=this
        val distancia=resources.getDimensionPixelSize(R.dimen.distancia)
        binding.rvListaEquiposClase.addItemDecoration(EspacioCardView(distancia))
        inicializarRecyclerView()
        return binding.root
    }

    private fun inicializarRecyclerView(){
        binding.rvListaEquiposClase.layoutManager=LinearLayoutManager(requireContext())
        mostrarEquipos()
    }

    private fun mostrarEquipos(){
        val observer=Observer<List<Equipos>?>{ listaEquipos ->
            listaEquipos?.let {
                if(binding.rvListaEquiposClase.layoutManager!=null && listaEquipos.isNotEmpty()){
                    binding.rvListaEquiposClase.adapter=
                        EquiposClaseRecyclerViewAdapter(listaEquipos) { item ->
                            listItemClicked(item)
                        }
                }else{
                    Toast.makeText(requireContext(), "No hay equipos en esta clase", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.equiposClase.observe(viewLifecycleOwner, observer)
    }

    private fun listItemClicked(item:Equipos){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response=item.codEquipo?.let { virtuaula.getEquiposById(it) }
                val equipo = response?.body()
                if (equipo != null) {
                    if (equipo.estado == "Libre") {
                        var response = (equipo.codEquipo?.let { virtuaula.getReservasByCodEquipo(it) })?.body()
                        if (response != null) {
                            val builder = AlertDialog.Builder(requireContext())
                            val id = response?.idReserva
                            builder.setTitle("Inserte la clave de su reserva")
                            val input = EditText(requireContext())
                            builder.setView(input)
                            builder.setPositiveButton("Aceptar") { dialog, _ ->
                                val clave = (input.text.toString()).toInt()
                                if (response != null) {
                                    if ((clave == response.clave) && (item.codEquipo == response.equipo?.codEquipo)) {
                                        val idSesion = Math.abs(Random.nextInt())
                                        val fechaHoraActual = LocalDateTime.now()
                                        val formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                                        val fechaHora = fechaHoraActual.format(formateador)
                                        val equipo = response.equipo
                                        val sesion = Sesiones(idSesion, fechaHora, null, response.usuario, equipo)
                                        GlobalScope.launch(Dispatchers.Main) {
                                            virtuaula.addSesiones(sesion)
                                            item.nomEquipo?.let { virtuaula.modifyEstadoAOcupado(it) }
                                            response?.idReserva?.let { sharedPreferences.edit().putInt("Cod Reserva", it).apply() }
                                            val builder = AlertDialog.Builder(requireContext())
                                            builder.setTitle("Inicio de sesión correcto. Bienvenido " + response.usuario?.nombre).setPositiveButton("Aceptar") { dialog, _ ->
                                                dialog.dismiss()
                                                sharedPreferences.edit().putInt("Id sesion", idSesion).apply()
                                                sharedPreferences.edit().putString("Nom Equipo", equipo?.nomEquipo).apply()
                                                sharedPreferences.edit().putBoolean("Sesión iniciada", true).apply()
                                                view?.findNavController()?.navigate(R.id.action_classComputerListFragment_to_classMenuFragment)
                                            }
                                            val dialog = builder.create()
                                            dialog.show()
                                        }
                                    } else if (item.codEquipo != response.equipo?.codEquipo) {
                                        val builder = AlertDialog.Builder(requireContext())
                                        builder.setTitle("La reserva insertada es para otro equipo.")
                                            .setNegativeButton("Aceptar") { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                        val dialog = builder.create()
                                        dialog.show()
                                    } else {
                                        val builder = AlertDialog.Builder(requireContext())
                                        builder.setTitle("La reserva insertada no existe.")
                                            .setNegativeButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                                        val dialog = builder.create()
                                        dialog.show()
                                    }
                                }
                            }.setNegativeButton("Cancelar") { dialog, _ ->
                                dialog.dismiss()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        } else {
                            var builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("Inserte nombre y contraseña de la aplicaciónVirtu@ula.")
                            val inputNombre = EditText(requireContext()).apply {
                                hint = "Dni"
                            }
                            val inputClave = EditText(requireContext()).apply {
                                hint = "Clave"
                                inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            }
                            val layout = LinearLayout(requireContext()).apply {
                                orientation = LinearLayout.VERTICAL
                                setPadding(50, 40, 50, 10) // Ajustar el padding según sea necesario
                            }
                            layout.addView(inputNombre)
                            layout.addView(inputClave)
                            builder.setView(layout)
                            builder.setPositiveButton("Aceptar") { dialog, _ ->
                                GlobalScope.launch(Dispatchers.Main) {
                                    val dni = inputNombre.text.toString()
                                    val clave = inputClave.text.toString()
                                    try {
                                        val usuario = virtuaula.getUsuarioByDniAndClave(dni, clave)
                                        val idSesion = Random.nextInt()
                                        val fechaHoraActual = LocalDateTime.now()
                                        val formateador =
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                                        val fechaHora = fechaHoraActual.format(formateador)
                                        val sesion =
                                            Sesiones(idSesion, fechaHora, null, usuario, item)
                                        virtuaula.addSesiones(sesion)
                                        item.nomEquipo?.let { virtuaula.modifyEstadoAOcupado(it) }
                                        sharedPreferences.edit()
                                            .putString("Nom Equipo", item.nomEquipo).apply()
                                        val builder = AlertDialog.Builder(requireContext())
                                        builder.setTitle("Inicio de sesión correcto. Bienvenido " + usuario.nombre)
                                            .setPositiveButton("Aceptar") { dialog, _ ->
                                                dialog.dismiss()
                                                sharedPreferences.edit().putInt("Id sesion", idSesion).apply()
                                                sharedPreferences.edit().putString("Nom equipo", equipo.nomEquipo).apply()
                                                sharedPreferences.edit().putBoolean("Sesión iniciada", true).apply()
                                                view?.findNavController()
                                                    ?.navigate(R.id.action_classComputerListFragment_to_classMenuFragment)
                                            }
                                        val dialog = builder.create()
                                        dialog.show()
                                    } catch (e: KotlinNullPointerException) {
                                        var builder = AlertDialog.Builder(requireContext())
                                        builder.setTitle("Inicio de sesión fallido. Compruebe sus credenciales")
                                            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                                        val dialog = builder.create()
                                        dialog.show()

                                    }
                                }
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }
                    } else if (equipo.estado == "Ocupado") {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Este equipo está ocupado.")
                            .setNegativeButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                        val dialog = builder.create()
                        dialog.show()
                    } else if (equipo.estado == "Roto") {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Este equipo está roto o en mantenimiento.")
                            .setNegativeButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
                Log.d("Error", "Error en "+e.message)
            }
        }
    }

}