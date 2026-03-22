package com.example.insatlkotlinv1.pages

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.insatlkotlinv1.R
import com.example.insatlkotlinv1.adapters.AppartementAdapter
import com.example.insatlkotlinv1.models.Appartement
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListAppartementActivity : AppCompatActivity() {

    private val appartements = mutableListOf<Appartement>()
    private val filteredAppartements = mutableListOf<Appartement>() // Pour la recherche
    private lateinit var adapter: AppartementAdapter
    private lateinit var listView: ListView
    private lateinit var tvTotal: TextView
    private lateinit var tvMin: TextView
    private lateinit var tvMax: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var btnSearch: ImageView
    private lateinit var searchCard: CardView
    private lateinit var searchEditText: EditText
    private lateinit var btnCloseSearch: ImageView

    private var isSearchVisible = false

    companion object {
        private const val ADD_APPARTEMENT_REQUEST = 1
        private const val EDIT_APPARTEMENT_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_appartement)

        initViews()
        setupToolbar()
        loadSampleData()
        setupAdapter()
        setupListeners()
        setupSearch()
        updateStats()
    }

    private fun initViews() {
        listView = findViewById(R.id.listViewAppartements)
        tvTotal = findViewById(R.id.tvTotal)
        tvMin = findViewById(R.id.tvMin)
        tvMax = findViewById(R.id.tvMax)
        fabAdd = findViewById(R.id.btnAdd)
        toolbar = findViewById(R.id.toolbar)
        btnSearch = findViewById(R.id.btnSearch)
        searchCard = findViewById(R.id.searchCard)
        searchEditText = findViewById(R.id.searchEditText)
        btnCloseSearch = findViewById(R.id.btnCloseSearch)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun loadSampleData() {
        // Données d'exemple
        appartements.add(Appartement(1, "Studio", 800.0))
        appartements.add(Appartement(2, "T2", 1200.0))
        appartements.add(Appartement(3, "T3", 2500.0))
        appartements.add(Appartement(4, "Duplex", 5500.0))
        appartements.add(Appartement(5, "Penthouse", 7500.0))
        appartements.add(Appartement(6, "Studio Luxe", 950.0))
        appartements.add(Appartement(7, "T4 Familial", 3200.0))
        
        // Copier toutes les données dans la liste filtrée
        filteredAppartements.clear()
        filteredAppartements.addAll(appartements)
    }

    private fun setupAdapter() {
        adapter = AppartementAdapter(
            this,
            filteredAppartements, // Utiliser la liste filtrée
            { appartement -> editAppartement(appartement) },
            { appartement -> showDeleteConfirmation(appartement) }
        )
        listView.adapter = adapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditAppartementActivity::class.java)
            startActivityForResult(intent, ADD_APPARTEMENT_REQUEST)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnSearch.setOnClickListener {
            toggleSearchVisibility()
        }

        btnCloseSearch.setOnClickListener {
            closeSearch()
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAppartements(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterAppartements(query: String) {
        filteredAppartements.clear()
        
        if (query.isEmpty()) {
            filteredAppartements.addAll(appartements)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredAppartements.addAll(
                appartements.filter {
                    it.design.lowercase().contains(lowerCaseQuery) ||
                    it.numApp.toString().contains(lowerCaseQuery) ||
                    it.obs.lowercase().contains(lowerCaseQuery)
                }
            )
        }
        
        adapter.notifyDataSetChanged()
        updateStats() // Mettre à jour les stats avec les résultats filtrés
    }

    private fun toggleSearchVisibility() {
        isSearchVisible = !isSearchVisible
        searchCard.visibility = if (isSearchVisible) View.VISIBLE else View.GONE
        
        if (isSearchVisible) {
            searchEditText.requestFocus()
            // Ouvrir le clavier
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(searchEditText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        } else {
            closeSearch()
        }
    }

    private fun closeSearch() {
        isSearchVisible = false
        searchCard.visibility = View.GONE
        searchEditText.setText("")
        filterAppartements("")
        
        // Cacher le clavier
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun editAppartement(appartement: Appartement) {
        val intent = Intent(this, AddEditAppartementActivity::class.java)
        intent.putExtra("appartement", appartement)
        startActivityForResult(intent, EDIT_APPARTEMENT_REQUEST)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showDeleteConfirmation(appartement: Appartement) {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Voulez-vous vraiment supprimer l'appartement ${appartement.design} ?")
            .setPositiveButton("Oui") { _, _ ->
                // Supprimer de la liste principale
                val indexInMain = appartements.indexOfFirst { it.numApp == appartement.numApp }
                if (indexInMain != -1) {
                    appartements.removeAt(indexInMain)
                }
                
                // Re-filtrer après suppression
                filterAppartements(searchEditText.text.toString())
                
                Toast.makeText(this, "Appartement supprimé", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun updateStats() {
        if (filteredAppartements.isEmpty()) {
            tvTotal.text = "0.00 DT"
            tvMin.text = "0.00 DT"
            tvMax.text = "0.00 DT"
            return
        }

        val total = filteredAppartements.sumOf { it.loyer }
        val min = filteredAppartements.minOf { it.loyer }
        val max = filteredAppartements.maxOf { it.loyer }

        tvTotal.text = String.format("%.2f DT", total)
        tvMin.text = String.format("%.2f DT", min)
        tvMax.text = String.format("%.2f DT", max)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val appartement = data.getSerializableExtra("appartement") as Appartement

            when (requestCode) {
                ADD_APPARTEMENT_REQUEST -> {
                    if (appartements.any { it.numApp == appartement.numApp }) {
                        Toast.makeText(this, "Ce numéro existe déjà", Toast.LENGTH_SHORT).show()
                    } else {
                        appartements.add(appartement)
                        filterAppartements(searchEditText.text.toString())
                        Toast.makeText(this, "Appartement ajouté", Toast.LENGTH_SHORT).show()
                    }
                }
                EDIT_APPARTEMENT_REQUEST -> {
                    val index = appartements.indexOfFirst { it.numApp == appartement.numApp }
                    if (index != -1) {
                        appartements[index] = appartement
                        filterAppartements(searchEditText.text.toString())
                        Toast.makeText(this, "Appartement modifié", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (isSearchVisible) {
            closeSearch()
        } else {
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}