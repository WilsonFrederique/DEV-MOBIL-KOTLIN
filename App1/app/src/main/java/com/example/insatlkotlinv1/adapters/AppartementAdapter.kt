package com.example.insatlkotlinv1.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.insatlkotlinv1.R
import com.example.insatlkotlinv1.models.Appartement

class AppartementAdapter(
    context: Context,
    private val appartements: MutableList<Appartement>,
    private val onEditClick: (Appartement) -> Unit,
    private val onDeleteClick: (Appartement) -> Unit
) : ArrayAdapter<Appartement>(context, 0, appartements) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_appartement, parent, false)

        val appartement = appartements[position]

        // Design
        view.findViewById<TextView>(R.id.tvDesign).text = appartement.design
        
        // Loyer
        view.findViewById<TextView>(R.id.tvLoyer).text = String.format("%.0f €", appartement.loyer)
        
        // Observation
        val tvObs = view.findViewById<TextView>(R.id.tvObs)
        tvObs.text = appartement.obs
        // Changer la couleur selon l'observation
        when (appartement.obs) {
            "Bas" -> tvObs.setBackgroundColor(context.getColor(android.R.color.holo_green_light))
            "Moyen" -> tvObs.setBackgroundColor(context.getColor(android.R.color.holo_orange_light))
            "Élevé" -> tvObs.setBackgroundColor(context.getColor(android.R.color.holo_red_light))
        }

        // Boutons d'action
        view.findViewById<ImageButton>(R.id.btnEdit).setOnClickListener {
            onEditClick(appartement)
        }

        view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
            onDeleteClick(appartement)
        }

        return view
    }
}