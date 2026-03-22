package com.example.insatlkotlinv1

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.insatlkotlinv1.models.Appartement
import com.example.insatlkotlinv1.pages.ListAppartementActivity
import com.example.insatlkotlinv1.pages.StatsActivity

class MainActivity : AppCompatActivity() {

    // Spécifier explicitement le type List<Appartement>
    private val sampleData: List<Appartement> = listOf(
        Appartement(1, "Studio", 800.0),
        Appartement(2, "T2", 1200.0),
        Appartement(3, "T3", 2500.0),
        Appartement(4, "Duplex", 5500.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Afficher les statistiques rapides
        val total = sampleData.size
        // Spécifier le type pour average()
        val moyenne = sampleData.map { it.loyer }.average()

        findViewById<TextView>(R.id.totalAppartements).text = total.toString()
        findViewById<TextView>(R.id.loyerMoyen).text = String.format("%.0f DT", moyenne)

        // Navigation
        findViewById<CardView>(R.id.cardListe).setOnClickListener {
            startActivity(Intent(this, ListAppartementActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        findViewById<CardView>(R.id.cardStats).setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}