package com.example.virtuula.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.virtuula.R
import com.example.virtuula.databinding.RecyclerItemBinding
import com.example.virtuula.model.Reservas
import com.example.virtuula.model.Retrofit.ListaReservas
import java.text.SimpleDateFormat
import java.util.Locale


class ReservaRecyclerViewAdapter (val listaReservas: List<Reservas>, val clickFunction:(Reservas)->Unit):RecyclerView.Adapter<ReservasViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding:RecyclerItemBinding=DataBindingUtil.inflate(layoutInflater, R.layout.recycler_item, parent, false)
        return ReservasViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaReservas.count()
    }

    override fun onBindViewHolder(holder: ReservasViewHolder, position: Int) {
        holder.actualizaViewHolder(listaReservas[position],clickFunction)
    }


}

class ReservasViewHolder(var binding: RecyclerItemBinding):RecyclerView.ViewHolder(binding.root){
    private val formatoTiempo= SimpleDateFormat("HH:mm", Locale.getDefault())
    fun actualizaViewHolder(reserva: Reservas, clickFunction: (Reservas) -> Unit){
        val context=binding.root.context
        binding.tvId.text=reserva.idReserva.toString()
        val horaReserva=reserva.fechaHoraReserva?.let {
            formatoTiempo.format(SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it))
        } ?: "N/A"
        binding.tvHora.text=horaReserva
        binding.tvClave.text=reserva.clave.toString()
        binding.root.setOnClickListener(){
            clickFunction(reserva)
        }
    }
}