package com.example.insatlkotlinv1.pages

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.insatlkotlinv1.R
import com.example.insatlkotlinv1.models.Appartement

class StatsActivity : AppCompatActivity() {

    private val appartements = mutableListOf<Appartement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        loadSampleData()
        displayStats()
        drawModernHistogram()
    }

    private fun loadSampleData() {
        appartements.add(Appartement(1, "Studio", 800.0))
        appartements.add(Appartement(2, "T2", 1200.0))
        appartements.add(Appartement(3, "T3", 2500.0))
        appartements.add(Appartement(4, "Duplex", 5500.0))
        appartements.add(Appartement(5, "Penthouse", 7500.0))
    }

    private fun displayStats() {
        if (appartements.isEmpty()) return

        val total = appartements.sumOf { it.loyer }
        val min = appartements.minOf { it.loyer }
        val max = appartements.maxOf { it.loyer }
        val moyenne = appartements.map { it.loyer }.average()

        findViewById<TextView>(R.id.tvStatsTotal).text = String.format("%.2f DT", total)
        findViewById<TextView>(R.id.tvStatsMin).text = String.format("%.2f DT", min)
        findViewById<TextView>(R.id.tvStatsMax).text = String.format("%.2f DT", max)
        findViewById<TextView>(R.id.tvStatsMoyenne).text = String.format("%.2f DT", moyenne)
        findViewById<TextView>(R.id.tvStatsCount).text = appartements.size.toString()
        findViewById<TextView>(R.id.tvMaxValue).text = String.format("Max: %.0f DT", max)
    }

    private fun drawModernHistogram() {
        val histogramLayout = findViewById<LinearLayout>(R.id.histogramLayout)
        histogramLayout.removeAllViews()

        if (appartements.isEmpty()) return

        val maxLoyer = appartements.maxOf { it.loyer }
        val maxHeight = 280

        // Trier pour un meilleur affichage
        val sortedAppartements = appartements.sortedBy { it.loyer }

        for (appartement in sortedAppartements) {
            // Conteneur de la barre
            val barContainer = LinearLayout(this)
            barContainer.orientation = LinearLayout.VERTICAL
            barContainer.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
            barContainer.setPadding(6, 0, 6, 0)
            barContainer.gravity = android.view.Gravity.BOTTOM

            // Valeur au-dessus de la barre
            val valueView = TextView(this)
            valueView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            valueView.text = String.format("%.0f", appartement.loyer)
            valueView.textSize = 11f
            valueView.gravity = android.view.Gravity.CENTER
            valueView.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            valueView.setPadding(0, 0, 0, 4)

            // Barre avec dégradé
            val barHeight = (appartement.loyer / maxLoyer * maxHeight).toInt().coerceAtLeast(30)
            val barView = View(this)
            barView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                barHeight
            )
            
            // Couleur avec dégradé
            val baseColor = when {
                appartement.loyer < 1000 -> Color.parseColor("#4CAF50")
                appartement.loyer <= 5000 -> Color.parseColor("#FF9800")
                else -> Color.parseColor("#F44336")
            }
            
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(baseColor, darkenColor(baseColor))
            )
            gradient.cornerRadius = 12f
            gradient.setStroke(2, Color.WHITE)
            barView.background = gradient

            // Label
            val labelView = TextView(this)
            labelView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            labelView.text = appartement.design
            labelView.textSize = 12f
            labelView.gravity = android.view.Gravity.CENTER
            labelView.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            labelView.setPadding(0, 8, 0, 0)

            barContainer.addView(valueView)
            barContainer.addView(barView)
            barContainer.addView(labelView)
            
            histogramLayout.addView(barContainer)
        }
    }

    private fun darkenColor(color: Int): Int {
        val r = (color shr 16 and 0xFF) * 0.7
        val g = (color shr 8 and 0xFF) * 0.7
        val b = (color and 0xFF) * 0.7
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }
}