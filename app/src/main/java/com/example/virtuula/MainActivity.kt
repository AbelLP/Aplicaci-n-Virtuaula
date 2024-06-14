package com.example.virtuula

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val privacidad=sharedPreferences.getBoolean("PrivacyAccepted", false)

        if(privacidad==false){
            val privacidad=AlertDialog.Builder(this)
            privacidad.setTitle("POLÍTICAS DE PRIVACIDAD VIRTU@ULA").setMessage(getString(R.string.politica))
            privacidad.setPositiveButton("Aceptar"){ dialog, _ ->
                sharedPreferences.edit().putBoolean("PrivacyAccepted", true).apply()
            }
            privacidad.create()
            privacidad.show()
        }


    }
}