package com.example.virtuula

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.virtuula.databinding.FragmentLoginBinding
import com.example.virtuula.logout.SessionManager
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import com.example.virtuula.model.Usuarios
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class LoginFragment : Fragment(), SessionManager.LogoutListener {

    lateinit var binding: FragmentLoginBinding
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.btnAcpt.setOnClickListener(){
            val dni=binding.etNomb.text.toString()
            val contrasenia=binding.etCla.text.toString()
            GlobalScope.launch (Dispatchers.Main){
                try {
                    if(dni!="" && contrasenia!=""){
                        val dniCambioClave=sharedPreferences.getString("Dni Cambio clave", "NO encontrado")
                        if(dniCambioClave!=dni){
                            val usuario=retrofit.getUsuarioByDniAndClave(dni, contrasenia)
                            Toast.makeText(activity, "Bienvenido "+usuario.nombre, Toast.LENGTH_SHORT).show()
                            sharedPreferences.edit().putString("Dni", usuario.dni).apply()
                            if(usuario.descripcion=="usuario"){
                                it.findNavController().navigate(R.id.action_loginFragment_to_userFragment)
                            }else if(usuario.descripcion=="administrador"){
                                it.findNavController().navigate(R.id.action_loginFragment_to_administerFragment)
                            }
                        }else{
                            val usuario=retrofit.getUsuariosById(dni)
                            if(usuario.body()!=null){
                                retrofit.cambiarClave(dni, contrasenia)
                                Toast.makeText(requireContext(), "Contraseña cambiada con éxito.", Toast.LENGTH_SHORT).show()
                                sharedPreferences.edit().remove("Dni Cambio clave").apply()
                                sharedPreferences.edit().putString("Dni", usuario.body()!!.dni).apply()
                                if(usuario.body()!!.descripcion=="usuario"){
                                    it.findNavController().navigate(R.id.action_loginFragment_to_userFragment)
                                }else if(usuario.body()!!.descripcion=="administrador"){
                                    it.findNavController().navigate(R.id.action_loginFragment_to_administerFragment)
                                }
                            }
                        }
                    }else if(dni=="" && contrasenia!=""){
                        Toast.makeText(requireContext(), "Debe rellenar el campo dni.", Toast.LENGTH_SHORT).show()
                    }else if(contrasenia==""&&dni!=""){
                        Toast.makeText(requireContext(), "Debe rellenar el campo clave", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Debe rellenar los campos para iniciar sesión", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: KotlinNullPointerException) {
                    Toast.makeText(activity, "Error en el login. Compruebe sus credenciales.", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.btnRecuperarContrasenia.setOnClickListener(){
            it.findNavController().navigate(R.id.action_loginFragment_to_newPasswordFragment)
        }

        return binding.root
    }

    fun onUserInteraction(){
        sessionManager.onUserAction()
    }

    override fun onSessionTimeout() {
        findNavController().navigate(R.id.homeFragment)
        Toast.makeText(requireContext(), "Tiempo de inactividad alcanzado. Vuelva a iniciar sesión.", Toast.LENGTH_SHORT).show()
    }


}