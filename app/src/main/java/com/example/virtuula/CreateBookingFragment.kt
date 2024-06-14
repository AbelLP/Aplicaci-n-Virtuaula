package com.example.virtuula

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.audiofx.DynamicsProcessing.Eq
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.virtuula.databinding.FragmentCreateBookingBinding
import com.example.virtuula.model.Equipos
import com.example.virtuula.model.Reservas
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.model.Usuarios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random


class CreateBookingFragment : Fragment() {

    lateinit var binding:FragmentCreateBookingBinding
    val format=SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private var fechaHoraElegida=Calendar.getInstance()
    private val random= Random()
    private lateinit var sharedPreferences: SharedPreferences
    private var equiposList: List<Equipos> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_create_booking, container, false)
        var cambiado=false
        var array:Array<String> = arrayOf()
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array)
        binding.spinner.adapter = spinnerAdapter
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        var codigoSeleccionado:Int?=0

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(cambiado==true){
                    val equipoSeleccionado = equiposList[position]
                    codigoSeleccionado = equipoSeleccionado.codEquipo
                }else{
                    Toast.makeText(requireContext(), "Selecciona una clase primero.", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Handle nothing selected
            }
        }

        binding.btnCambiarClaseReserva.setOnClickListener(){
            try{
                GlobalScope.launch (Dispatchers.Main){
                    val clases=retrofit.getClases()
                    if(clases!=null){
                        val nombreClases=clases?.map { it.nombre }?.toTypedArray()
                        nombreClases?.let{
                            val builder=AlertDialog.Builder(requireContext())
                            builder.setTitle("Seleccione una clase.").setItems(it){dialog, which ->
                                val claseSeleccionada=clases[which]
                                binding.tvClaseReserva.setText(claseSeleccionada.nombre)
                                cambiado=true
                            }.setNegativeButton("Cancelar") {dialog, _ ->
                                dialog.dismiss()
                            }
                            val dialog=builder.create()
                            dialog.show()
                        }
                    }else{
                        Toast.makeText(requireContext(), "No hay clases en la base de datos.", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){

            }
        }

        binding.tvClaseReserva.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loadEquipos(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etHoraReserva.setOnClickListener(){
            showDateTimePicker()
        }

        binding.btnAceptarReserva.setOnClickListener(){
            val idReservaS=binding.etReservaId.text.toString()
            val idReserva=idReservaS.toInt()
            val horaReserva=format.format(fechaHoraElegida.time)
            GlobalScope.launch(Dispatchers.Main) {
                if((idReservaS!=""||idReservaS!=null)&&(horaReserva!=""||horaReserva!=null)&&(codigoSeleccionado!=null&&codigoSeleccionado!=0)){
                    val usuarioResponse: Response<Usuarios?>? = sharedPreferences.getString("Dni","Dni no encontrado")?.let { retrofit.getUsuariosById(it) }
                    val usuario = usuarioResponse?.body()
                    val caducado="no"
                    val response=codigoSeleccionado?.let { it1 -> retrofit.getEquiposById(it1) }
                    val equipo= response?.body()
                    val clave=random.nextInt(300)+1
                    val reserva=Reservas(idReserva, horaReserva, caducado, clave, usuario, equipo)
                    retrofit.addReservas(reserva)
                    val builder=AlertDialog.Builder(requireContext())
                    builder.setTitle("Reserva Creada").setMessage("La reserva se ha creado con éxito. La clave de la reserva es: "+clave+".").setPositiveButton("Aceptar"){dialog, _ ->
                        dialog.dismiss()
                        it.findNavController().navigate(R.id.action_createBookingFragment_to_userFragment)
                    }
                    val dialog=builder.create()
                    dialog.show()
                }else if((idReservaS==""||idReservaS==null)&&(horaReserva!=""||horaReserva!=null)&&(codigoSeleccionado!=null||codigoSeleccionado!=0)){
                    Toast.makeText(requireContext(), "Debe rellenar el campo Id", Toast.LENGTH_SHORT).show()
                }else if((idReservaS!=""||idReservaS!=null)&&(horaReserva==""||horaReserva==null)&&(codigoSeleccionado!=null||codigoSeleccionado!=0)){
                    Toast.makeText(requireContext(), "Debe rellenar el campo Hora", Toast.LENGTH_SHORT).show()
                }else if((idReservaS!=""||idReservaS!=null)&&(horaReserva!=""||horaReserva!=null)&&(codigoSeleccionado==null||codigoSeleccionado!=0)){
                    Toast.makeText(requireContext(), "Debe rellenar el campo Equipos", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun loadEquipos(claseNombre: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                Toast.makeText(requireContext(), "Nombre clase: "+claseNombre, Toast.LENGTH_SHORT).show()
                val equiposResponse:List<Equipos> = withContext(Dispatchers.IO) {
                    retrofit.getEquiposByNombreClaseAndLibres(claseNombre)
                }
                if (equiposResponse != null) {
                    equiposList = equiposResponse
                    val equiposNombres = equiposResponse.map { it.nomEquipo }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, equiposNombres)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinner.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "No se encontraron equipos.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar los equipos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDateTimePicker(){
        var fechaActual=Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val fechaSeleccionada=Calendar.getInstance()
            fechaSeleccionada.set(year, month, dayOfMonth)
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                fechaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay)
                fechaSeleccionada.set(Calendar.MINUTE, minute)
                fechaHoraElegida=fechaSeleccionada
                binding.etHoraReserva.setText(format.format(fechaHoraElegida.time))
            }, fechaActual.get(Calendar.HOUR_OF_DAY), fechaActual.get(Calendar.MINUTE), true).show()
        }, fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH)).show()
    }

}