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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtuula.databinding.FragmentUserBinding
import com.example.virtuula.decoration.EspacioCardView
import com.example.virtuula.model.Repositorio
import com.example.virtuula.model.Reservas
import com.example.virtuula.model.Retrofit.Albums
import com.example.virtuula.model.Retrofit.ListaReservas
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.view.ReservaRecyclerViewAdapter
import com.example.virtuula.viewmodel.UserFragmentViewModel
import com.example.virtuula.viewmodel.UserFragmentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.create
import java.lang.Exception

class UserFragment : Fragment() {

    lateinit var binding:FragmentUserBinding
    lateinit var viewModel: UserFragmentViewModel
    private lateinit var sharedPreferences: SharedPreferences
    val virtuaula= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    val service=RetrofitInstance.getRetrofitInstance2().create(VirtuaulaService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repositorio= Repositorio(virtuaula, requireContext())
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_user, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var viewModelFactory=UserFragmentViewModelFactory(repositorio)
        viewModel=ViewModelProvider(this,viewModelFactory).get(UserFragmentViewModel::class.java)
        binding.lifecycleOwner=this
        val distancia=resources.getDimensionPixelSize(R.dimen.distancia)
        binding.listaReservas.addItemDecoration(EspacioCardView(distancia))
        binding.fabAniadir.setOnClickListener(){
            it.findNavController().navigate(R.id.action_userFragment_to_createBookingFragment)
        }
        binding.fabAula.setOnClickListener(){
            try{
                GlobalScope.launch(Dispatchers.Main) {
                    val clases=virtuaula.getClases()
                    if(clases!=null){
                        val nombreClases=clases?.map {it.nombre}?.toTypedArray()
                        nombreClases?.let {
                            val builder= AlertDialog.Builder(requireContext())
                            builder.setTitle("Seleccione una clase").setItems(it){ dialog, which ->
                                val claseSeleccionada=clases[which]
                                val codSeleccionado=claseSeleccionada.codClase
                                if (codSeleccionado != null) {
                                    sharedPreferences.edit().putInt("Id Clase", codSeleccionado).apply()
                                }
                                findNavController().navigate(R.id.action_userFragment_to_classMenuFragment)
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
            }catch (e: Exception){
                Toast.makeText(requireContext(), "Algo ha salido mal", Toast.LENGTH_SHORT).show()
            }
        }
        inicializarRecyclerView()
        return binding.root
    }

    private fun inicializarRecyclerView(){
        binding.listaReservas.layoutManager=LinearLayoutManager(requireContext())
        mostrarReservas()
    }

    private fun mostrarReservas(){
        val observer = Observer<List<Reservas>?> { listaReservas ->
            listaReservas?.let {
                if (binding.listaReservas.layoutManager != null && listaReservas.isNotEmpty()) {
                    binding.listaReservas.adapter =
                        ReservaRecyclerViewAdapter(listaReservas) { item ->
                            listItemClicked(item)
                        }
                }else{
                    Toast.makeText(requireContext(), "No tienes reservas hechas", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.reservas.observe(viewLifecycleOwner, observer)
    }

    private fun listItemClicked(item: Reservas) {
        GlobalScope.launch (Dispatchers.Main){
            item.equipo?.clase?.codClase?.let { sharedPreferences.edit().putInt("Cod Clase", it) }
            findNavController().navigate(R.id.action_userFragment_to_classComputerListFragment)
        }
    }


}
