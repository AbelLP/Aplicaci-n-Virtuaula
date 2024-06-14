package com.example.virtuula.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.virtuula.R
import com.example.virtuula.databinding.RecyclerItem2Binding
import com.example.virtuula.databinding.RecyclerItem3Binding
import com.example.virtuula.model.Reservas
import com.example.virtuula.viewmodel.AdministerBookingListFragmentViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ReservasAdminsitradorViewAdapter(val listaReservas:List<Reservas>, val clickFunction: (Reservas)->Unit): RecyclerView.Adapter<ReservasAdministradorViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservasAdministradorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RecyclerItem3Binding = DataBindingUtil.inflate(layoutInflater, R.layout.recycler_item3, parent, false)
        return ReservasAdministradorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservasAdministradorViewHolder, position: Int) {
        holder.actualizaViewHolder(listaReservas[position],clickFunction)
    }

    override fun getItemCount(): Int {
        return listaReservas.count()
    }

}

class ReservasAdministradorViewHolder(var binding: RecyclerItem3Binding):RecyclerView.ViewHolder(binding.root){
    private val formatoTiempo=SimpleDateFormat("HH:mm", Locale.getDefault())
    fun actualizaViewHolder(reserva: Reservas, clickFunction: (Reservas) -> Unit){
        binding.tvId.text=reserva.idReserva.toString()
        val horaReserva=reserva.fechaHoraReserva?.let {
            formatoTiempo.format(SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss", Locale.getDefault()).parse(it))
        } ?: "N/A"
        binding.tvHora.text=horaReserva
        binding.tvUsuarioBooking.text= reserva.usuario?.nombre.toString()
        binding.tvEquipoBooking.text=reserva.equipo?.nomEquipo.toString()
        binding.tvClaseBooking.text=reserva.equipo?.clase?.nombre.toString()
        binding.root.setOnClickListener(){
            clickFunction(reserva)
        }
    }
}