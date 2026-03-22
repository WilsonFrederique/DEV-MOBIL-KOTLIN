package com.example.insatlkotlinv1.pages

import android.content.Intent  // IMPORT MANQUANT AJOUTÉ
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
            title = "Modifier Appartement"
            populateFields()
        } else {
            title = "Ajouter Appartement"
        }

        setupListeners()
    }

    private fun initViews() {
        etNumApp = findViewById(R.id.etNumApp)
        etDesign = findViewById(R.id.etDesign)
        etLoyer = findViewById(R.id.etLoyer)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
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

                val resultIntent = Intent()  // Maintenant Intent est reconnu
                resultIntent.putExtra("appartement", appartement as Serializable)
                if (appartementToEdit != null) {
                    resultIntent.putExtra("isEdit", true)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun validateFields(): Boolean {
        if (etNumApp.text.toString().isEmpty()) {
            etNumApp.error = "Numéro requis"
            return false
        }
        if (etDesign.text.toString().isEmpty()) {
            etDesign.error = "Désignation requise"
            return false
        }
        if (etLoyer.text.toString().isEmpty()) {
            etLoyer.error = "Loyer requis"
            return false
        }
        try {
            etLoyer.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            etLoyer.error = "Loyer invalide"
            return false
        }
        return true
    }
}