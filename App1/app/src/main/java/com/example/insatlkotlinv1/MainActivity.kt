package com.example.insatlkotlinv1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.insatlkotlinv1.models.Appartement
import com.example.insatlkotlinv1.network.ApiService
import com.example.insatlkotlinv1.pages.ListAppartementActivity
import com.example.insatlkotlinv1.pages.StatsActivity

class MainActivity : AppCompatActivity() {

    private val apiService = ApiService()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var appartements = listOf<Appartement>()
    
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTotal: TextView
    private lateinit var tvTotalBas: TextView
    private lateinit var tvTotalMoyen: TextView
    private lateinit var tvTotalEleve: TextView
    private lateinit var cardListe: CardView
    private lateinit var cardStats: CardView

    companion object {
        private const val REQUEST_CODE_LIST = 100
        private const val REQUEST_CODE_STATS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        loadRealData()
    }

    override fun onResume() {
        super.onResume()
        // Recharger les données quand on revient à l'accueil
        loadRealData()
    }

    private fun initViews() {
        tvTotal = findViewById(R.id.totalAppartements)
        tvTotalBas = findViewById(R.id.totalBas)
        tvTotalMoyen = findViewById(R.id.totalMoyen)
        tvTotalEleve = findViewById(R.id.totalEleve)
        progressBar = findViewById(R.id.progressBar)
        cardListe = findViewById(R.id.cardListe)
        cardStats = findViewById(R.id.cardStats)
        
        showLoadingState()
    }

    private fun setupClickListeners() {
        // Navigation vers la liste des appartements
        cardListe.setOnClickListener {
            val intent = Intent(this, ListAppartementActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_LIST)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Navigation vers les statistiques
        cardStats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_STATS)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        tvTotal.text = "..."
        tvTotalBas.text = "..."
        tvTotalMoyen.text = "..."
        tvTotalEleve.text = "..."
    }

    private fun loadRealData() {
        apiService.getAllAppartements(object : ApiService.ApiCallback<List<Appartement>> {
            override fun onSuccess(result: List<Appartement>) {
                mainHandler.post {
                    appartements = result
                    updateStats()
                    progressBar.visibility = View.GONE
                    
                    // Afficher un message de confirmation seulement au premier chargement
                    if (result.isNotEmpty()) {
                        // Pas de toast pour éviter de spammer
                    }
                }
            }

            override fun onError(error: String) {
                mainHandler.post {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity, 
                        "❌ Erreur de connexion: $error\nUtilisation des données de démonstration", 
                        Toast.LENGTH_LONG
                    ).show()
                    useFallbackData()
                }
            }
        })
    }

    private fun updateStats() {
        if (appartements.isEmpty()) {
            tvTotal.text = "0"
            tvTotalBas.text = "0"
            tvTotalMoyen.text = "0"
            tvTotalEleve.text = "0"
            return
        }

        // Calcul des statistiques
        val total = appartements.size
        val bas = appartements.count { it.loyer < 1000 }
        val moyen = appartements.count { it.loyer in 1000.0..5000.0 }
        val eleve = appartements.count { it.loyer > 5000 }

        // Mise à jour des TextViews
        tvTotal.text = total.toString()
        tvTotalBas.text = bas.toString()
        tvTotalMoyen.text = moyen.toString()
        tvTotalEleve.text = eleve.toString()
        
        // Animation subtile pour les changements
        animateTextView(tvTotal)
        animateTextView(tvTotalBas)
        animateTextView(tvTotalMoyen)
        animateTextView(tvTotalEleve)
    }

    private fun animateTextView(textView: TextView) {
        textView.alpha = 0.5f
        textView.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    private fun useFallbackData() {
        // Données de secours en cas d'erreur réseau
        val fallbackData = listOf(
            Appartement(1, "Studio", 800.0),
            Appartement(2, "T2", 1200.0),
            Appartement(3, "T3", 2500.0),
            Appartement(4, "Duplex", 5500.0),
            Appartement(5, "Penthouse", 7500.0)
        )
        
        appartements = fallbackData
        updateStats()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // Rafraîchir les données quand on revient d'une activité
        when (requestCode) {
            REQUEST_CODE_LIST, REQUEST_CODE_STATS -> {
                if (resultCode == RESULT_OK) {
                    loadRealData()
                    Toast.makeText(this, "✓ Données mises à jour", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        // Quitter l'application avec confirmation
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Quitter")
        builder.setMessage("Voulez-vous vraiment quitter l'application ?")
        builder.setPositiveButton("Oui") { _, _ ->
            finish()
        }
        builder.setNegativeButton("Non", null)
        builder.show()
    }
}