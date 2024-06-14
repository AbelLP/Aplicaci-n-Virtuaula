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
import com.example.virtuula.databinding.FragmentNewPasswordBinding
import com.example.virtuula.model.Retrofit.RetrofitInstance
import com.example.virtuula.model.Retrofit.VirtuaulaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewPasswordFragment : Fragment() {

    lateinit var binding:FragmentNewPasswordBinding
    val retrofit= RetrofitInstance.getRetrofitInstance().create(VirtuaulaService::class.java)
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_new_password, container, false)
        sharedPreferences=requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding.btnAceptarContrasenia.setOnClickListener(){
            val dni=binding.etDniContrasenia.text.toString()
            GlobalScope.launch (Dispatchers.Main) {
                val usuario=retrofit.getUsuariosById(dni)
                if(usuario.body()!=null){
                    retrofit.cambiarClaveATrue(dni)
                    sharedPreferences.edit().putString("Dni Cambio clave", usuario.body()!!.dni).apply()
                    val builder=AlertDialog.Builder(requireContext())
                    builder.setTitle("Usuario encontrado").setMessage("Ahora en el login pon tu dni de usuario y la nueva contraseña para cambiarla").setPositiveButton("Aceptar"){ dialog,_ ->
                        dialog.dismiss()
                        view?.findNavController()?.navigate(R.id.action_newPasswordFragment_to_loginFragment)
                    }
                    val dialog=builder.create()
                    dialog.show()
                }else{
                    Toast.makeText(requireContext(), "El dni insertado no existe.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }


}