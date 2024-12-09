package com.example.vamosracharkotlin

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var textToSpeech: TextToSpeech
    lateinit var valueInput: EditText
    lateinit var peopleQuantity: EditText
    lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textToSpeech = TextToSpeech(this, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = textToSpeech.setLanguage(Locale("pt", "BR"))
                if (languageResult == TextToSpeech.LANG_MISSING_DATA
                    || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Linguagem não suportada ou faltando dados")
                }
            } else {
                Log.e("TTS", "Falha na inicialização do TTS")
            }
        })

        valueInput = findViewById(R.id.editTextValue)
        peopleQuantity = findViewById(R.id.editTextPeopleQuantity)
        resultTextView = findViewById(R.id.textViewResult)

        valueInput.addTextChangedListener(inputWatcher)
        peopleQuantity.addTextChangedListener(inputWatcher)
    }

    fun SpeakResultText(view: View?) {
        val textToRead = resultTextView.text.toString()
        if (textToRead.isNotEmpty()) {
            textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.e("TTS", "Texto vazio para falar")
        }
    }

    fun ShareResultText(view: View?) {
        val textToShare = resultTextView.text.toString()
        if (textToShare.isNotEmpty()) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Compartilhar com..."))
        } else {
            Log.e("Share", "Texto vazio para compartilhar")
        }
    }

    private val inputWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val value = valueInput.text.toString().toDoubleOrNull()
            val quantity = peopleQuantity.text.toString().toDoubleOrNull()

            if (value != null && quantity != null && quantity != 0.0) {
                val result = value / quantity
                resultTextView.text = String.format("%.2f", result)
            } else {
                resultTextView.text = "0.00"
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}