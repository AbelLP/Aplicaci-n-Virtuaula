package com.example.virtuula

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtuula.databinding.FragmentSessionsListBinding
import com.example.virtuula.decoration.EspacioCardView
import com.example.virtuula.model.Repositorio
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.model.Sesiones
import com.example.virtuula.view.SesionesRecyclerViewAdapter
import com.example.virtuula.viewmodel.SessionsListFragmentViewModel
import com.example.virtuula.viewmodel.SessionsListFragmentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SessionsListFragment : Fragment() {
    lateinit var binding:FragmentSessionsListBinding
    lateinit var viewModel:SessionsListFragmentViewModel
    val virtuaula= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repositorio=Repositorio(virtuaula, requireContext())
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_sessions_list, container, false)
        var viewModelFactory=SessionsListFragmentViewModelFactory(repositorio)
        viewModel=ViewModelProvider(this, viewModelFactory).get(SessionsListFragmentViewModel::class.java)
        binding.lifecycleOwner=this
        val distancia=resources.getDimensionPixelSize(R.dimen.distancia)
        binding.listaSesiones.addItemDecoration(EspacioCardView(distancia))
        inicializarRecyclerView()
        return binding.root
    }

    private fun inicializarRecyclerView(){
        binding.listaSesiones.layoutManager=LinearLayoutManager(requireContext())
        mostrarSesiones()
    }

    private fun mostrarSesiones(){
        val observer= Observer<List<Sesiones>?> { listaSesiones ->
            listaSesiones?.let {
                if(binding.listaSesiones.layoutManager!=null && listaSesiones.isNotEmpty()){
                    binding.listaSesiones.adapter =
                        SesionesRecyclerViewAdapter(listaSesiones) { item ->
                            listItemClicked(item)
                        }

                }else{
                    Toast.makeText(requireContext(), "NO CARGA LISTA", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.sesiones.observe(viewLifecycleOwner, observer)
    }

    private fun listItemClicked(item:Sesiones){
        GlobalScope.launch(Dispatchers.Main) {
            try{
                var idSesion=item.idSesion?.toLong()
                var builder=AlertDialog.Builder(requireContext())
                builder.setTitle("Borrar Sesión")
                .setMessage("¿Seguro que quieres borrar la sesión?").setPositiveButton("Aceptar"){dialog, _ ->
                    GlobalScope.launch(Dispatchers.Main) {
                        idSesion?.toInt()?.let { virtuaula.deleteSesiones(it) }
                        var builder=AlertDialog.Builder(requireContext())
                        builder.setTitle("Sesión eliminada con éxito.").setPositiveButton("Aceptar"){dialog, _ ->
                            dialog.dismiss()
                        }
                        val dialog=builder.create()
                        dialog.show()
                    }
                }
                    .setNegativeButton("Cancelar"){dialog, _ -> dialog.dismiss() }
                val dialog=builder.create()
                dialog.show()
            }catch(e:Exception){

            }
        }
    }


}