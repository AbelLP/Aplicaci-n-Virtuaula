package com.example.virtuula.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.virtuula.databinding.RecyclerItem4Binding

class EspacioCardView(private val distancia:Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            // Aplica espacio en la parte inferior de cada elemento excepto el último
            if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
                bottom = distancia
            }
        }
    }
}