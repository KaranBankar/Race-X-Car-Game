package com.example.racex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class StartGame : AppCompatActivity() {

    lateinit var start:CardView
    lateinit var linkdin:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        start=findViewById(R.id.start_button)
        linkdin=findViewById(R.id.dev_karan)

        start.setOnClickListener{
            var i= Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }

        linkdin.setOnClickListener{
            openLinkedIn()
        }

    }

    private fun openLinkedIn() {
        val linkedInUrl = "https://www.linkedin.com/in/karan-bankar-453b57252"
        val intent = packageManager.getLaunchIntentForPackage("com.linkedin.android")

        if (intent != null) {
            // If the LinkedIn app is installed, open the app
            intent.data = Uri.parse(linkedInUrl)
            startActivity(intent)
        } else {
            // If the app is not installed, open the URL in a browser
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl))
            startActivity(browserIntent)
        }
    }
}