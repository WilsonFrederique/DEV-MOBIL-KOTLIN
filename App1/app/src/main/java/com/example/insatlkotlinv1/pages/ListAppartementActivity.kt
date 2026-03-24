package com.example.insatlkotlinv1.pages

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.insatlkotlinv1.R
import com.example.insatlkotlinv1.adapters.AppartementAdapter
import com.example.insatlkotlinv1.models.Appartement
import com.example.insatlkotlinv1.network.ApiService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListAppartementActivity : AppCompatActivity() {

    private val appartements = mutableListOf<Appartement>()
    private val filteredAppartements = mutableListOf<Appartement>()
    private lateinit var adapter: AppartementAdapter
    private lateinit var apiService: ApiService
    private val mainHandler = Handler(Looper.getMainLooper())
    
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
        
        apiService = ApiService()
        
        initViews()
        setupToolbar()
        setupAdapter()
        setupListeners()
        setupSearch()
        
        loadAppartements()
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
        supportActionBar?.title = "Liste des Appartements"
    }

    private fun setupAdapter() {
        adapter = AppartementAdapter(
            this,
            filteredAppartements,
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

    private fun loadAppartements() {
        apiService.getAllAppartements(object : ApiService.ApiCallback<List<Appartement>> {
            override fun onSuccess(result: List<Appartement>) {
                mainHandler.post {
                    appartements.clear()
                    appartements.addAll(result)
                    
                    filteredAppartements.clear()
                    filteredAppartements.addAll(appartements)
                    
                    adapter.notifyDataSetChanged()
                    updateStats()
                }
            }

            override fun onError(error: String) {
                mainHandler.post {
                    Toast.makeText(this@ListAppartementActivity, 
                        "❌ Erreur: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun addAppartement(appartement: Appartement) {
        apiService.addAppartement(appartement, object : ApiService.ApiCallback<Map<String, Any>> {
            override fun onSuccess(result: Map<String, Any>) {
                mainHandler.post {
                    Toast.makeText(this@ListAppartementActivity, 
                        "✓ Appartement ajouté", Toast.LENGTH_SHORT).show()
                    loadAppartements()
                    // Retourner à l'accueil avec succès
                    setResult(RESULT_OK)
                }
            }

            override fun onError(error: String) {
                mainHandler.post {
                    Toast.makeText(this@ListAppartementActivity, 
                        "❌ Erreur: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun updateAppartement(appartement: Appartement) {
        apiService.updateAppartement(appartement, object : ApiService.ApiCallback<Map<String, Any>> {
            override fun onSuccess(result: Map<String, Any>) {
                mainHandler.post {
                    Toast.makeText(this@ListAppartementActivity, 
                        "✓ Appartement modifié", Toast.LENGTH_SHORT).show()
                    loadAppartements()
                    // Retourner à l'accueil avec succès
                    setResult(RESULT_OK)
                }
            }

            override fun onError(error: String) {
                mainHandler.post {
                    Toast.makeText(this@ListAppartementActivity, 
                        "❌ Erreur: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun deleteAppartement(appartement: Appartement) {
        apiService.deleteAppartement(appartement.numApp, object : ApiService.ApiCallback<Map<String, Any>> {
            override fun onSuccess(result: Map<String, Any>) {
                mainHandler.post {
                    Toast.makeText(this@ListAppartementActivity, 
                        "✓ Appartement supprimé", Toast.LENGTH_SHORT).show()
                    loadAppartements()
                    // Retourner à l'accueil avec succès
                    setResult(RESULT_OK)
                }
            }

            override fun onError(error: String) {
                mainHandler.post {
                    Toast.makeText(this@ListAppartementActivity, 
                        "❌ Erreur: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
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
            .setMessage("Supprimer ${appartement.design} ?")
            .setPositiveButton("Oui") { _, _ ->
                deleteAppartement(appartement)
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
        updateStats()
    }

    private fun toggleSearchVisibility() {
        isSearchVisible = !isSearchVisible
        searchCard.visibility = if (isSearchVisible) View.VISIBLE else View.GONE
        
        if (isSearchVisible) {
            searchEditText.requestFocus()
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
        
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == RESULT_OK && data != null) {
            val appartement = data.getSerializableExtra("appartement") as Appartement
            
            when (requestCode) {
                ADD_APPARTEMENT_REQUEST -> addAppartement(appartement)
                EDIT_APPARTEMENT_REQUEST -> updateAppartement(appartement)
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
            // Retourner à l'accueil avec succès pour rafraîchir
            setResult(RESULT_OK)
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}