package com.example.insatlkotlinv1.pages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.insatlkotlinv1.R
import com.example.insatlkotlinv1.models.Appartement
import java.io.Serializable

class AddEditAppartementActivity : AppCompatActivity() {

    private lateinit var etNumApp: EditText
    private lateinit var etDesign: EditText
    private lateinit var etLoyer: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var appartementToEdit: Appartement? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_appartement)

        initViews()
        
        // Vérifier si on est en mode édition
        if (intent.hasExtra("appartement")) {
            appartementToEdit = intent.getSerializableExtra("appartement") as Appartement
            supportActionBar?.title = "Modifier Appartement"
            populateFields()
        } else {
            supportActionBar?.title = "Ajouter Appartement"
        }

        setupListeners()
    }

    private fun initViews() {
        etNumApp = findViewById(R.id.etNumApp)
        etDesign = findViewById(R.id.etDesign)
        etLoyer = findViewById(R.id.etLoyer)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun populateFields() {
        appartementToEdit?.let {
            etNumApp.setText(it.numApp.toString())
            etDesign.setText(it.design)
            etLoyer.setText(it.loyer.toString())
            etNumApp.isEnabled = false // Ne pas permettre la modification du numéro
        }
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            if (validateFields()) {
                val numApp = etNumApp.text.toString().toInt()
                val design = etDesign.text.toString()
                val loyer = etLoyer.text.toString().toDouble()

                val appartement = Appartement(numApp, design, loyer)

                val resultIntent = Intent()
                resultIntent.putExtra("appartement", appartement as Serializable)
                setResult(RESULT_OK, resultIntent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun validateFields(): Boolean {
        if (etNumApp.text.toString().isEmpty()) {
            etNumApp.error = "Numéro requis"
            etNumApp.requestFocus()
            return false
        }
        
        if (etDesign.text.toString().isEmpty()) {
            etDesign.error = "Désignation requise"
            etDesign.requestFocus()
            return false
        }
        
        if (etLoyer.text.toString().isEmpty()) {
            etLoyer.error = "Loyer requis"
            etLoyer.requestFocus()
            return false
        }
        
        try {
            val loyer = etLoyer.text.toString().toDouble()
            if (loyer < 0) {
                etLoyer.error = "Le loyer doit être positif"
                etLoyer.requestFocus()
                return false
            }
        } catch (e: NumberFormatException) {
            etLoyer.error = "Loyer invalide"
            etLoyer.requestFocus()
            return false
        }
        
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}