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
import com.example.virtuula.databinding.FragmentCreateComputerBinding
import com.example.virtuula.model.Clases
import com.example.virtuula.model.Equipos
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CreateComputerFragment : Fragment() {

    lateinit var binding:FragmentCreateComputerBinding
    private lateinit var sharedPreferences: SharedPreferences
    val format=SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private var fechaHoraElegida=Calendar.getInstance()
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
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_create_computer, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var estado:String=""
        //var codigoSeleccionado:Int?=0
        //cargarClases()
        val spinner:Spinner=binding.spnEstado
        ArrayAdapter.createFromResource(requireContext(), R.array.estadoEquipo, android.R.layout.simple_spinner_dropdown_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter=adapter
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



        binding.btnAcepEquipos.setOnClickListener(){
            val idEqS=binding.etIdEquipo.text.toString()
            val idEq=idEqS.toInt()
            val nombre=binding.etNomEquipo.text.toString()
            val fechaAlta=format.format(fechaHoraElegida.time)
            val especificaciones=binding.etEspecificaciones.text.toString()
            GlobalScope.launch (Dispatchers.Main){
                try{
                    if((idEqS!=null||idEqS!="")&&(nombre!=null||nombre!="")&&(fechaAlta!=null||fechaAlta!="")&&(especificaciones!=null||especificaciones!="")&&(estado!=null||estado!="")){
                        val codClase=sharedPreferences.getInt("Cod Clase", 0)
                        val responseClase=retrofit.getClasesById(codClase)
                        val clase=responseClase.body()
                        val cuenta= clase?.codClase?.let { it1 -> retrofit.getCountEquiposClase(it1) }
                        val response=retrofit.getEquiposById(idEq)
                        if(response.isSuccessful){
                            if(response.body()==null && cuenta!! <clase.tamanio!!){
                                val equipo = Equipos(idEq, nombre, estado, fechaAlta, especificaciones, clase)
                                retrofit.addEquipos(equipo)
                                Toast.makeText(requireContext(), "Equipo añadido con éxito", Toast.LENGTH_SHORT).show()
                                it.findNavController().navigate(R.id.action_createComputerFragment_to_classMenuFragment)
                            }else if(cuenta!!>=clase.tamanio!!){
                                Toast.makeText(requireContext(), "En esta clase ya no caben mas equipos. Selecciona otra", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else if((idEqS==null||idEqS=="")&&(nombre!=null||nombre!="")&&(fechaAlta!=null||fechaAlta!="")&&(especificaciones!=null||especificaciones!="")&&(estado!=null||estado!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Id", Toast.LENGTH_SHORT).show()
                    }else if((idEqS!=null||idEqS!="")&&(nombre==null||nombre=="")&&(fechaAlta!=null||fechaAlta!="")&&(especificaciones!=null&&especificaciones!="")&&(estado!=null||estado!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Nombre", Toast.LENGTH_SHORT).show()
                    }else if((idEqS!=null||idEqS!="")&&(nombre!=null||nombre!="")&&(fechaAlta==null||fechaAlta=="")&&(especificaciones!=null||especificaciones!="")&&(estado!=null||estado!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Fecha Alta", Toast.LENGTH_SHORT).show()
                    }else if((idEqS!=null||idEqS!="")&&(nombre!=null||nombre!="")&&(fechaAlta!=null&&fechaAlta!="")&&(especificaciones==null&&especificaciones=="")&&(estado!=null||estado!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Especificaciones", Toast.LENGTH_SHORT).show()
                    }else if((idEqS!=null||idEqS!="")&&(nombre!=null||nombre!="")&&(fechaAlta!=null&&fechaAlta!="")&&(especificaciones==null&&especificaciones=="")&&(estado==null&&estado=="")){
                        Toast.makeText(requireContext(), "Debe seleccionar un estado.", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show()
                    }

                } catch(e: KotlinNullPointerException){
                    Toast.makeText(requireContext(), "Error al añadir equipo ${e.message}.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.etFecha.setOnClickListener(){
            showDateTimePicker()
        }
        return binding.root
    }

    private fun showDateTimePicker(){
        val fechaActual= Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val fechaSeleccionada=Calendar.getInstance()
            fechaSeleccionada.set(year, month, dayOfMonth)
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                fechaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay)
                fechaSeleccionada.set(Calendar.MINUTE, minute)
                fechaHoraElegida = fechaSeleccionada
                binding.etFecha.setText(format.format(fechaHoraElegida.time))
            }, fechaActual.get(Calendar.HOUR_OF_DAY), fechaActual.get(Calendar.MINUTE), true).show()
        }, fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH)).show()
    }
}