package com.example.virtuula

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtuula.databinding.FragmentAdministerBookingListBinding
import com.example.virtuula.decoration.EspacioCardView
import com.example.virtuula.model.Repositorio
import com.example.virtuula.model.Reservas
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.view.ReservasAdministradorViewHolder
import com.example.virtuula.view.ReservasAdminsitradorViewAdapter
import com.example.virtuula.viewmodel.AdministerBookingListFragmentViewModel
import com.example.virtuula.viewmodel.AdministerBookingListFragmentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdministerBookingListFragment : Fragment() {

    lateinit var viewModel: AdministerBookingListFragmentViewModel
    lateinit var binding: FragmentAdministerBookingListBinding
    val virtuaula = RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private var isSpinnerInitialLoad = true // Bandera para evitar llamadas innecesarias

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repositorio = Repositorio(virtuaula, requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_administer_booking_list, container, false)
        val viewModelFactory = AdministerBookingListFragmentViewModelFactory(repositorio)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AdministerBookingListFragmentViewModel::class.java)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding.lifecycleOwner = this
        val distancia=resources.getDimensionPixelSize(R.dimen.distancia)
        binding.listaReservasAdmin.addItemDecoration(EspacioCardView(distancia))
        iniciarSpinner()
        inicializarRecyclerView()
        cargarReservasHoy()
        return binding.root
    }

    private fun inicializarRecyclerView() {
        binding.listaReservasAdmin.layoutManager = LinearLayoutManager(requireContext())
        mostrarReservas()
    }

    private fun mostrarReservas() {
        val observer = Observer<List<Reservas>?> { listaReservas ->
            listaReservas?.let {
                Log.d("Fragment", "Updating RecyclerView with ${it.size} reservations")
                if (binding.listaReservasAdmin.layoutManager != null && listaReservas.isNotEmpty()) {
                    binding.listaReservasAdmin.adapter =
                        ReservasAdminsitradorViewAdapter(listaReservas) { item ->
                            listItemClicked(item)
                        }
                }
            }
        }
        viewModel.reservas.observe(viewLifecycleOwner, observer)
    }

    private fun listItemClicked(item: Reservas) {
        GlobalScope.launch(Dispatchers.Main) {
            try{
                var idReserva=item.idReserva?.toLong()
                var builder=AlertDialog.Builder(requireContext())
                builder.setTitle("Borrar Reserva")
                    .setMessage("¿Seguro que quieres borrar la reserva?").setPositiveButton("Aceptar"){dialog, _ ->
                        GlobalScope.launch(Dispatchers.Main) {
                            idReserva?.let { virtuaula.deleteReservas(it) }
                            var builder=AlertDialog.Builder(requireContext())
                            builder.setTitle("Reserva eliminada con éxito.").setPositiveButton("Aceptar"){dialog, _ ->
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

    private fun iniciarSpinner() {
        val opciones = arrayOf("Sin filtro", "Por clases", "Por hora")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnFiltro.adapter = adapter

        binding.spnFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialLoad) {
                    isSpinnerInitialLoad = false
                    return
                }
                when (opciones[position]) {
                    "Por clases" -> dialogoClase()
                    "Por hora" -> dialogoHora()
                    //"Sin filtro" -> cargarReservas()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    private fun dialogoClase() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val clases = virtuaula.getClases()
                val clasesNombres = clases.map { it.nombre }
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Selecciona una clase")
                builder.setItems(clasesNombres.toTypedArray()) { _, which ->
                    val claseSeleccionada = clasesNombres[which]
                    sharedPreferences.edit().putString("Nombre Clase", claseSeleccionada).apply()
                    cargarListaClases()
                }
                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
                builder.show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar las clases", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dialogoHora() {
        val currentTime = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            selectedTime.set(Calendar.SECOND, 0)
            val formattedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(selectedTime.time)
            saveSelectedTime(formattedTime)
            cargarListaHoras()
        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true).show()
    }

    private fun saveSelectedTime(time: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("Hora", time).apply()
    }

    private fun cargarListaClases() {
        Log.d("Fragment", "Loading class reservations")
        viewModel.cargarReservasClase()
    }

    private fun cargarListaHoras() {
        Log.d("Fragment", "Loading hour reservations")
        viewModel.cargarReservasHora()
    }

    /*private fun cargarReservas() {
        Log.d("Fragment", "Loading all reservations")
        viewModel.cargarReservas()
    }*/

    private fun cargarReservasHoy() {
        Log.d("Fragment", "Loading today's reservations")
        viewModel.cargarReservasHoy()
    }
}
