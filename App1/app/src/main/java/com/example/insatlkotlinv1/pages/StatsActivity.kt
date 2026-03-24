package com.example.insatlkotlinv1.pages

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.insatlkotlinv1.R
import com.example.insatlkotlinv1.models.Appartement
import com.example.insatlkotlinv1.network.ApiService

class StatsActivity : AppCompatActivity() {

    private val apiService = ApiService()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var appartements = listOf<Appartement>()
    
    private lateinit var progressBar: ProgressBar
    private lateinit var cardStatsContainer: View
    private lateinit var cardHistogramContainer: View
    private lateinit var cardLegendContainer: View
    private lateinit var tvStatsTotal: TextView
    private lateinit var tvStatsMin: TextView
    private lateinit var tvStatsMax: TextView
    private lateinit var tvStatsMoyenne: TextView
    private lateinit var tvStatsCount: TextView
    private lateinit var tvMaxValue: TextView
    private lateinit var histogramLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        initViews()
        loadRealData()
    }

    private fun initViews() {
        try {
            progressBar = findViewById(R.id.progressBarStats)
            cardStatsContainer = findViewById(R.id.cardStatsContainer)
            cardHistogramContainer = findViewById(R.id.cardHistogramContainer)
            cardLegendContainer = findViewById(R.id.cardLegendContainer)
            tvStatsTotal = findViewById(R.id.tvStatsTotal)
            tvStatsMin = findViewById(R.id.tvStatsMin)
            tvStatsMax = findViewById(R.id.tvStatsMax)
            tvStatsMoyenne = findViewById(R.id.tvStatsMoyenne)
            tvStatsCount = findViewById(R.id.tvStatsCount)
            tvMaxValue = findViewById(R.id.tvMaxValue)
            histogramLayout = findViewById(R.id.histogramLayout)
            
            Log.d("StatsActivity", "Views initialized successfully")
            
        } catch (e: Exception) {
            Log.e("StatsActivity", "Error initializing views: ${e.message}")
            Toast.makeText(this, "Erreur d'initialisation: ${e.message}", Toast.LENGTH_LONG).show()
        }
        
        cardStatsContainer.visibility = View.GONE
        cardHistogramContainer.visibility = View.GONE
        cardLegendContainer.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        
        tvStatsTotal.text = "0 €"
        tvStatsMin.text = "0 €"
        tvStatsMax.text = "0 €"
        tvStatsMoyenne.text = "0 €"
        tvStatsCount.text = "0"
        tvMaxValue.text = "Chargement..."
    }

    private fun loadRealData() {
        apiService.getAllAppartements(object : ApiService.ApiCallback<List<Appartement>> {
            override fun onSuccess(result: List<Appartement>) {
                mainHandler.post {
                    Log.d("StatsActivity", "Data loaded: ${result.size} items")
                    appartements = result
                    displayStats()
                    drawModernHistogram()
                    
                    progressBar.visibility = View.GONE
                    cardStatsContainer.visibility = View.VISIBLE
                    cardHistogramContainer.visibility = View.VISIBLE
                    cardLegendContainer.visibility = View.VISIBLE
                    
                    Log.d("StatsActivity", "Containers are now visible")
                    
                    Toast.makeText(this@StatsActivity, 
                        "✓ ${result.size} appartement(s) chargé(s)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(error: String) {
                mainHandler.post {
                    Log.e("StatsActivity", "Error loading data: $error")
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@StatsActivity, 
                        "❌ Erreur de connexion: $error\nUtilisation des données de démonstration", Toast.LENGTH_LONG).show()
                    useFallbackData()
                }
            }
        })
    }

    private fun displayStats() {
        if (appartements.isEmpty()) {
            tvStatsTotal.text = "0 €"
            tvStatsMin.text = "0 €"
            tvStatsMax.text = "0 €"
            tvStatsMoyenne.text = "0 €"
            tvStatsCount.text = "0"
            tvMaxValue.text = "Aucune donnée"
            return
        }

        val total = appartements.sumOf { it.loyer }
        val min = appartements.minOf { it.loyer }
        val max = appartements.maxOf { it.loyer }
        val moyenne = appartements.map { it.loyer }.average()

        tvStatsTotal.text = String.format("%.0f €", total)
        tvStatsMin.text = String.format("%.0f €", min)
        tvStatsMax.text = String.format("%.0f €", max)
        tvStatsMoyenne.text = String.format("%.0f €", moyenne)
        tvStatsCount.text = appartements.size.toString()
        tvMaxValue.text = String.format("Max: %.0f €", max)
        
        Log.d("StatsActivity", "Stats displayed - Total: $total, Min: $min, Max: $max, Count: ${appartements.size}")
    }

    private fun drawModernHistogram() {
        try {
            Log.d("StatsActivity", "Starting to draw histogram")
            histogramLayout.removeAllViews()
            
            if (appartements.isEmpty()) {
                Log.d("StatsActivity", "No data to display")
                val emptyView = TextView(this)
                emptyView.text = "Aucune donnée à afficher"
                emptyView.textSize = 16f
                emptyView.gravity = android.view.Gravity.CENTER
                emptyView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
                emptyView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                histogramLayout.addView(emptyView)
                return
            }

            val maxLoyer = appartements.maxOf { it.loyer }
            val maxHeight = 280
            val minHeight = 30
            
            Log.d("StatsActivity", "Max loyer: $maxLoyer, Max height: $maxHeight")
            
            // Trier par loyer pour un meilleur affichage
            val sortedAppartements = appartements.sortedBy { it.loyer }
            
            Log.d("StatsActivity", "Drawing ${sortedAppartements.size} bars")
            
            for ((index, appartement) in sortedAppartements.withIndex()) {
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

                // Calculer la hauteur de la barre
                var barHeight: Int
                if (maxLoyer <= 10000) {
                    // Échelle linéaire
                    barHeight = (appartement.loyer / maxLoyer * maxHeight).toInt()
                } else {
                    // Échelle logarithmique
                    val logLoyer = Math.log10(appartement.loyer)
                    val logMax = Math.log10(maxLoyer)
                    barHeight = (logLoyer / logMax * maxHeight).toInt()
                }
                barHeight = barHeight.coerceAtLeast(minHeight)
                
                Log.d("StatsActivity", "Bar ${index+1}: ${appartement.design} - Loyer: ${appartement.loyer} - Height: ${barHeight}px")
                
                val barView = View(this)
                barView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    barHeight
                )
                
                // Couleur avec dégradé selon la catégorie
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
            
            // Forcer le rafraîchissement du layout
            histogramLayout.invalidate()
            histogramLayout.requestLayout()
            
            Log.d("StatsActivity", "Histogram drawing completed")
            
        } catch (e: Exception) {
            Log.e("StatsActivity", "Error drawing histogram: ${e.message}")
            Toast.makeText(this, "Erreur d'affichage: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun useFallbackData() {
        val fallbackData = listOf(
            Appartement(1, "Studio", 800.0),
            Appartement(2, "T2", 1200.0),
            Appartement(3, "T3", 2500.0),
            Appartement(4, "Duplex", 5500.0),
            Appartement(5, "Penthouse", 7500.0)
        )
        
        appartements = fallbackData
        displayStats()
        drawModernHistogram()
        
        progressBar.visibility = View.GONE
        cardStatsContainer.visibility = View.VISIBLE
        cardHistogramContainer.visibility = View.VISIBLE
        cardLegendContainer.visibility = View.VISIBLE
        
        Log.d("StatsActivity", "Fallback data displayed")
    }

    private fun darkenColor(color: Int): Int {
        val r = (color shr 16 and 0xFF) * 0.7
        val g = (color shr 8 and 0xFF) * 0.7
        val b = (color and 0xFF) * 0.7
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    override fun onResume() {
        super.onResume()
        loadRealData()
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}