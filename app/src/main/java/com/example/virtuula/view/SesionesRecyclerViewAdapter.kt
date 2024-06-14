package com.example.virtuula.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.virtuula.R
import com.example.virtuula.databinding.RecyclerItem2Binding
import com.example.virtuula.model.Sesiones
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class SesionesRecyclerViewAdapter(val listaSesiones: List<Sesiones>, val clickFunction: (Sesiones) -> Unit) : RecyclerView.Adapter<SesionesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SesionesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RecyclerItem2Binding = DataBindingUtil.inflate(layoutInflater, R.layout.recycler_item2, parent, false)
        return SesionesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaSesiones.count()
    }

    override fun onBindViewHolder(holder: SesionesViewHolder, position: Int) {
        holder.actualizaViewHolder(listaSesiones[position], clickFunction)
    }
}

class SesionesViewHolder(var binding: RecyclerItem2Binding) : RecyclerView.ViewHolder(binding.root) {
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val formatoTiempo = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun actualizaViewHolder(sesiones: Sesiones, clickFunction: (Sesiones) -> Unit) {
        val context = binding.root.context
        binding.tvUsuario.text = sesiones.usuarios?.nombre.toString()
        binding.tvEquipo.text = sesiones.equipos?.nomEquipo.toString()

        val horaComienzo = sesiones.fechaHoraComienzo?.let {
            formatoTiempo.format(dateTimeFormat.parse(it))
        } ?: "N/A"
        binding.tvLogin.text = horaComienzo

        val horaLogout = sesiones.fechaHoraFin?.let {
            formatoTiempo.format(dateTimeFormat.parse(it))
        } ?: "N/A"
        binding.tvLogout.text = horaLogout

        val tiempoConectado = if (sesiones.fechaHoraFin != null) {
            val inicio = dateTimeFormat.parse(sesiones.fechaHoraComienzo)
            val fin = dateTimeFormat.parse(sesiones.fechaHoraFin)
            calcularTiempoConectado(inicio, fin)
        } else {
            "Sigue conectado"
        }
        binding.tvConectado.text = tiempoConectado

        binding.root.setOnClickListener(){
            clickFunction(sesiones)
        }
    }

    private fun calcularTiempoConectado(inicio: Date, fin: Date): String {
        val diffInMillis = fin.time - inicio.time
        val horas = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val minutos = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60
        return String.format("%02d:%02d", horas, minutos)
    }
}
