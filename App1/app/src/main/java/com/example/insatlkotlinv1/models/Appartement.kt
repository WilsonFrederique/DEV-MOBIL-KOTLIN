package com.example.insatlkotlinv1.models

import java.io.Serializable

data class Appartement(
    val numApp: Int,
    val design: String,
    val loyer: Double 
) : Serializable {
    val obs: String
        get() = when {
            loyer < 1000 -> "Bas"
            loyer in 1000.0..5000.0 -> "Moyen"
            else -> "Élevé"
        }
}