package com.example.virtuula.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.virtuula.R
import com.example.virtuula.databinding.RecyclerItem4Binding
import com.example.virtuula.model.Equipos

class EquiposClaseRecyclerViewAdapter(val listaEquipos: List<Equipos>, val clickFunction: (Equipos)->Unit):RecyclerView.Adapter<EquiposClaseViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquiposClaseViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding:RecyclerItem4Binding=DataBindingUtil.inflate(layoutInflater, R.layout.recycler_item4, parent, false)
        return EquiposClaseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaEquipos.count()
    }

    override fun onBindViewHolder(holder: EquiposClaseViewHolder, position: Int) {
        holder.actualizaViewHolder(listaEquipos[position], clickFunction)
    }
}

class EquiposClaseViewHolder(var binding:RecyclerItem4Binding):RecyclerView.ViewHolder(binding.root){
    fun actualizaViewHolder(equipos: Equipos, clickFunction: (Equipos) -> Unit){
        binding.tvEquipoNombre.text=equipos.nomEquipo
        val estado=equipos.estado
        if(estado=="Libre"){
            binding.imVEstado.setImageResource(R.drawable.libre)
        }else if(estado=="Ocupado"){
            binding.imVEstado.setImageResource(R.drawable.ocupado)
        }else if(estado=="Roto"){
            binding.imVEstado.setImageResource(R.drawable.roto)
        }

        binding.root.setOnClickListener(){
            clickFunction(equipos)
        }

    }
}