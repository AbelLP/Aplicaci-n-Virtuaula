package com.example.virtuula

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.fragment.findNavController
import com.example.virtuula.databinding.FragmentRegisterBinding
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.model.Usuarios
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit


class RegisterFragment : Fragment() {

    lateinit var binding:FragmentRegisterBinding
    val retrofit=RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private lateinit var opciones:Array<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        opciones=requireContext().resources.getStringArray(R.array.tiposUsuario)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var descripcion:String=""
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        val spinner: Spinner =binding.spnTipoUsuario
        ArrayAdapter.createFromResource(requireContext(), R.array.tiposUsuario, android.R.layout.simple_spinner_dropdown_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter=adapter
        }
        spinner.onItemSelectedListener =object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                var tipoUsuarioSeleccionado=opciones[position]
                if(tipoUsuarioSeleccionado=="Usuario"){
                    descripcion="usuario"
                }else if(tipoUsuarioSeleccionado=="Administrador"){
                    descripcion="administrador"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
            }
        }
        binding.btnAcep.setOnClickListener(){
            val dni=binding.etDni.text.toString()
            val nombre=binding.etNom.text.toString()
            val apellidos=binding.etApellidos.text.toString()
            val telefono=binding.etTlfno.text.toString()
            val email=binding.etEmail.text.toString()
            val clave=binding.etClave.text.toString()
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    if((dni!=null||dni!="")&&(nombre!=null||nombre!="")&&(apellidos!=null||apellidos!="")&&(telefono!=null||telefono!="")&&(email!=null||email!="")&&(clave!=null&&clave!="")){
                        val response: Response<Usuarios?> = retrofit.getUsuariosById(dni)
                        if (response.isSuccessful) {
                            if (response.body() == null) {
                                val usuario = Usuarios(dni, nombre, apellidos, telefono, email, clave, false, descripcion)
                                aniadirUsuario(usuario)
                                it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            } else {
                                Toast.makeText(requireContext(), "Este dni ya existe en la base de datos.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error en la respuesta del servidor.", Toast.LENGTH_SHORT).show()
                        }
                    }else if((dni==null||dni=="")&&(nombre!=null||nombre!="")&&(apellidos!=null||apellidos!="")&&(telefono!=null||telefono!="")&&(email!=null||email!="")&&(clave!=null&&clave!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Dni", Toast.LENGTH_SHORT).show()
                    }else if((dni!=null||dni!="")&&(nombre==null||nombre=="")&&(apellidos!=null||apellidos!="")&&(telefono!=null||telefono!="")&&(email!=null||email!="")&&(clave!=null&&clave!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Nombre", Toast.LENGTH_SHORT).show()
                    }else if((dni!=null||dni!="")&&(nombre!=null||nombre=="")&&(apellidos==null||apellidos=="")&&(telefono!=null||telefono!="")&&(email!=null||email!="")&&(clave!=null&&clave!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Apellidos.", Toast.LENGTH_SHORT).show()
                    }else if((dni!=null||dni!="")&&(nombre!=null||nombre=="")&&(apellidos!=null||apellidos!="")&&(telefono==null||telefono=="")&&(email!=null||email!="")&&(clave!=null&&clave!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Teléfono", Toast.LENGTH_SHORT).show()
                    }else if((dni!=null||dni!="")&&(nombre!=null||nombre=="")&&(apellidos!=null||apellidos!="")&&(telefono!=null||telefono!="")&&(email==null||email=="")&&(clave!=null||clave!="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Email", Toast.LENGTH_SHORT).show()
                    }else if((dni!=null||dni!="")&&(nombre!=null||nombre=="")&&(apellidos!=null||apellidos!="")&&(telefono!=null||telefono!="")&&(email!=null||email!="")&&(clave==null||clave=="")){
                        Toast.makeText(requireContext(), "Debe rellenar el campo Clave", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show()
                    }

                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), "Error al comprobar el dni: ${ex.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Error", ex.toString())
                }
            }
        }
        return binding.root
    }

    private fun aniadirUsuario(usuario: Usuarios) {
        CoroutineScope(Dispatchers.IO).launch {
            retrofit.addUsuarios(usuario)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController=findNavController();
        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id==R.id.loginFragment && navController.previousBackStackEntry?.destination?.id==R.id.registerFragment){
                Toast.makeText(requireContext(), "Usuario registrado. Ahora haga la operación de login con el usuario recién creado.", Toast.LENGTH_LONG).show()
            }
        }
    }

}