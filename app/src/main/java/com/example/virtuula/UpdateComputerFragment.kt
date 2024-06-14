package com.example.virtuula

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.virtuula.databinding.FragmentUpdateComputerBinding
import com.example.virtuula.model.Clases
import com.example.virtuula.model.Equipos
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateComputerFragment : Fragment() {
    private var fechaHoraElegida= Calendar.getInstance()
    val format=SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    lateinit var binding:FragmentUpdateComputerBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var opciones:Array<String>
    private var listaClases:List<Clases> = listOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        opciones=requireContext().resources.getStringArray(R.array.estadoEquipo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_update_computer, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var estado:String=""
        var codigoSeleccionado:Int?=0
        var spinner:Spinner=binding.spnEstado2
        var equipoRecogido:Equipos
        val cod=sharedPreferences.getInt("Cod Equipo", 0)
        ArrayAdapter.createFromResource(requireContext(), R.array.estadoEquipo, android.R.layout.simple_spinner_dropdown_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter=adapter
        }

        binding.spnIdClaseEquipo2.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val claseSeleccionada=listaClases[position]
                codigoSeleccionado=claseSeleccionada.codClase
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        spinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var estadoSeleccionado=opciones[position]
                if(estadoSeleccionado=="Libre"){
                    estado="Libre"
                }else if(estadoSeleccionado=="Ocupado"){
                    estado="Ocupado"
                }else if(estadoSeleccionado=="Roto"){
                    estado="Roto"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
            }
        }
        GlobalScope.launch (Dispatchers.Main){
            var response=retrofit.getEquiposById(cod)
            equipoRecogido= response?.body()!!
            var idEq:String?=equipoRecogido.codEquipo.toString()
            var nomEq:String?=equipoRecogido.nomEquipo.toString()
            estado= equipoRecogido.estado.toString()
            var fecEq:String?=equipoRecogido.fechaAlta.toString()
            var espEq:String?=equipoRecogido.especificaciones.toString()
            var idClaseEq= equipoRecogido.clase?.codClase.toString()
            binding.etIdEquipo2.setText(idEq)
            binding.etNomEquipo2.setText(nomEq)
            val indice=opciones.indexOf(equipoRecogido.estado)
            if(indice>=0){
                binding.spnEstado2.setSelection(indice)
            }
            binding.etFecha2.setText(fecEq)
            binding.etEspecificaciones2.setText(espEq)
            cargarClases {
                val indiceClase=listaClases.indexOfFirst { it.codClase==equipoRecogido.clase?.codClase }
                if(indiceClase>=0){
                    binding.spnIdClaseEquipo2.setSelection(indiceClase)
                }
            }

        }

        binding.etFecha2.setOnClickListener(){
            showDateTimePicker()
        }

        binding.btnAcepEquipos2.setOnClickListener(){
            val idEqS=binding.etIdEquipo2.text.toString()
            val idEq=idEqS.toInt()
            val nombre=binding.etNomEquipo2.text.toString()
            val fechaAlta=format.format(fechaHoraElegida.time)
            val especificaciones=binding.etEspecificaciones2.text.toString()
            GlobalScope.launch (Dispatchers.Main){
                try{
                    val response= codigoSeleccionado?.let { it1 -> retrofit.getClasesById(it1) }
                    val clase= response?.body()
                    val equipos = Equipos(idEq, nombre, estado, fechaAlta, especificaciones, clase)
                    retrofit.updateEquipos(equipos)
                    Toast.makeText(requireContext(), "Equipo actualizado con éxito", Toast.LENGTH_SHORT).show()
                    sharedPreferences.edit().remove("Cod Equipo").apply()
                    it.findNavController().navigate(R.id.action_updateComputerFragment_to_classMenuFragment)
                } catch(e: KotlinNullPointerException){
                    Toast.makeText(requireContext(), "La clase indicada no existe ${e.message}.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnBorrarEquipos.setOnClickListener(){
            GlobalScope.launch (Dispatchers.Main){
                val mensaje=retrofit.deleteEquipos(sharedPreferences.getInt("Cod Equipo",0))
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().remove("Cod Equipo").apply()
                it.findNavController().navigate(R.id.action_updateComputerFragment_to_classMenuFragment)
            }
        }
        return binding.root
    }

    private fun showDateTimePicker(){
        var fechaHoraActual=Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val fechaSeleccionada=Calendar.getInstance()
            fechaSeleccionada.set(year, month, dayOfMonth)
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                fechaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay)
                fechaSeleccionada.set(Calendar.MINUTE, minute)
                fechaHoraElegida=fechaSeleccionada
                binding.etFecha2.setText(format.format(fechaHoraElegida.time))
            }, fechaHoraActual.get(Calendar.HOUR_OF_DAY), fechaHoraActual.get(Calendar.MINUTE), true).show()
        }, fechaHoraActual.get(Calendar.YEAR), fechaHoraActual.get(Calendar.MONTH), fechaHoraActual.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun cargarClases(onLoaded: () ->Unit){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val respuesta=retrofit.getClases()
                if(respuesta!=null){
                    listaClases=respuesta
                    val clasesNombre=respuesta.map { it.nombre }
                    val adapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, clasesNombre)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spnIdClaseEquipo2.adapter=adapter
                    onLoaded()
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(), "No cargan las clases", Toast.LENGTH_SHORT).show()
            }
        }
    }



}