package com.example.racex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize GameView
        gameView = findViewById(R.id.gameView)
    }

    override fun onResume() {
        super.onResume()
        // Start the game when the activity is resumed
        gameView.startGame()
    }

    override fun onPause() {
        super.onPause()
        // Stop the game when the activity is paused
        gameView.stopGame()
    }

    // This method will be called when the game is over
    fun onGameOver(score: Int) {
        runOnUiThread {
            showGameOverDialog(score)
            gameView.backgroundMusic.stop()
        }
    }

    // Method to show the Game Over dialog
    private fun showGameOverDialog(score: Int) {
        // Inflate the custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.custom_game_over_dialog, null)

        // Set the score dynamically in the message TextView
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        dialogMessage.text = "Your score is: $score"

        // Create the dialog using the custom view
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)  // Set the custom view for the dialog
            .create()

        // Set up the "Retry" button behavior
        val retryButton = dialogView.findViewById<Button>(R.id.btnRetry)
        retryButton.setOnClickListener {
            // Retry: Reset the game and restart it
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up the "Back to Start" button behavior
        val backButton = dialogView.findViewById<Button>(R.id.btnBackToStart)
        backButton.setOnClickListener {
            // Back to Start: Navigate back to the Start Game screen
            val intent = Intent(this, StartGame::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }

        // Show the dialog
        dialog.show()
    }


}
