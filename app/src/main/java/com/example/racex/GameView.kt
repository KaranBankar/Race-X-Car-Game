package com.example.racex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView

class GameView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs), Runnable {

    private var thread: Thread? = null
    private var isPlaying = false
    private val paint = Paint()

    private var playerCar: Bitmap
    private var obstacleCar: Bitmap
    private var powerUp: Bitmap
    private var roadBackground: Bitmap

    private var playerX = 0f
    private var playerY = 0f
    private var obstacleX = 0f
    private var obstacleY = 0f
    private var powerUpX = 0f
    private var powerUpY = 0f
    private var backgroundY1 = 0f
    private var backgroundY2 = 0f

    private var screenWidth = 0
    private var screenHeight = 0

    private var score = 0
    private var highScore = 0
    private var sharedPreferences = context.getSharedPreferences("HighScores", Context.MODE_PRIVATE)

    public lateinit var collisionSound: MediaPlayer
    public lateinit var backgroundMusic: MediaPlayer

    private var isGameOver = false
    private var powerUpActive = false
    private var powerUpTimer = 0




    init {
        // Load assets
        playerCar = BitmapFactory.decodeResource(resources, R.drawable.playercar)
        obstacleCar = BitmapFactory.decodeResource(resources, R.drawable.opp_car)
        powerUp = BitmapFactory.decodeResource(resources, R.drawable.coin)
        roadBackground = BitmapFactory.decodeResource(resources, R.drawable.road)

        // Scale assets
        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        playerCar = Bitmap.createScaledBitmap(playerCar, screenWidth / 5, screenHeight / 10, false)
        obstacleCar = Bitmap.createScaledBitmap(obstacleCar, screenWidth / 5, screenHeight / 10, false)
        powerUp = Bitmap.createScaledBitmap(powerUp, screenWidth / 10, screenHeight / 20, false)
        roadBackground = Bitmap.createScaledBitmap(roadBackground, screenWidth, screenHeight, false)

        // Initialize positions
        playerX = (screenWidth / 2 - playerCar.width / 2).toFloat()
        playerY = (screenHeight - playerCar.height - 50).toFloat()
        obstacleX = (0 until screenWidth - obstacleCar.width).random().toFloat()
        obstacleY = -obstacleCar.height.toFloat()
        powerUpX = (0 until screenWidth - powerUp.width).random().toFloat()
        powerUpY = -powerUp.height.toFloat()

        backgroundY1 = 0f
        backgroundY2 = -screenHeight.toFloat()

        // Load sounds
        collisionSound = MediaPlayer.create(context, R.raw.colossion_sound)
        backgroundMusic = MediaPlayer.create(context, R.raw.background_music)
        backgroundMusic.isLooping = true

        // Load high score
        highScore = sharedPreferences.getInt("HighScore", 0)
    }

    // In GameView class
    // In GameView class
    override fun run() {
        backgroundMusic.start()
        while (isPlaying) {
            if (!isGameOver) {
                update()
                checkCollision()  // Check for collisions
                draw()
                sleep()
            }
        }
    }



    private fun update() {
        // Move background
        backgroundY1 += 10
        backgroundY2 += 10
        if (backgroundY1 >= screenHeight) backgroundY1 = -screenHeight.toFloat()
        if (backgroundY2 >= screenHeight) backgroundY2 = -screenHeight.toFloat()

        // In GameView class

// Move obstacle
        obstacleY += 15
        if (obstacleY > screenHeight) {
            obstacleY = -obstacleCar.height.toFloat()
            obstacleX = (0 until screenWidth - obstacleCar.width).random().toFloat()
            score += if (powerUpActive) 20 else 10  // Update score
        }

// Move power-up
        powerUpY += 10
        if (powerUpY > screenHeight) {
            powerUpY = -powerUp.height.toFloat()
            powerUpX = (0 until screenWidth - powerUp.width).random().toFloat()
        }


        // Power-up timer
        if (powerUpActive) {
            powerUpTimer++
            if (powerUpTimer > 300) { // Reset after 5 seconds
                powerUpActive = false
                powerUpTimer = 0
            }
        }

        // Collision detection
        val playerRect = RectF(playerX, playerY, playerX + playerCar.width, playerY + playerCar.height)
        val obstacleRect = RectF(obstacleX, obstacleY, obstacleX + obstacleCar.width, obstacleY + obstacleCar.height)
        val powerUpRect = RectF(powerUpX, powerUpY, powerUpX + powerUp.width, powerUpY + powerUp.height)

        // Check for collisions with obstacle
        if (playerRect.intersects(obstacleRect.left, obstacleRect.top, obstacleRect.right, obstacleRect.bottom)) {
            isGameOver = true
        }

        // Check for collisions with power-up
        if (playerRect.intersects(powerUpRect.left, powerUpRect.top, powerUpRect.right, powerUpRect.bottom)) {
            powerUpActive = true
            powerUpTimer = 0
            powerUpY = -powerUp.height.toFloat()
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(roadBackground, 0f, backgroundY1, paint)
            canvas.drawBitmap(roadBackground, 0f, backgroundY2, paint)
            canvas.drawBitmap(playerCar, playerX, playerY, paint)
            canvas.drawBitmap(obstacleCar, obstacleX, obstacleY, paint)
            canvas.drawBitmap(powerUp, powerUpX, powerUpY, paint)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun saveHighScore() {
        if (score > highScore) {
            highScore = score
            sharedPreferences.edit().putInt("HighScore", highScore).apply()
        }
    }

    fun resetGame() {
        score = 0
        isGameOver = false
        playerX = (screenWidth / 2 - playerCar.width / 2).toFloat()
        obstacleY = -obstacleCar.height.toFloat()
        powerUpY = -powerUp.height.toFloat()
    }

    private fun sleep() {
        Thread.sleep(16) // ~60 FPS
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        playerX = event.x - playerCar.width / 2
        return true
    }

    fun stopGame() {
        isPlaying = false
        backgroundMusic.stop()
        collisionSound.stop()
    }

    fun startGame() {
        isPlaying = true
        thread = Thread(this)
        thread?.start()
    }

    // In GameView class
    // In GameView class

    fun checkCollision() {
        if (hasCollisionOccurred()) {
            isGameOver = true

            // Notify MainActivity that the game is over and pass the current score
            (context as MainActivity).onGameOver(score)
        }
    }

    fun hasCollisionOccurred(): Boolean {
        val playerRect = RectF(playerX, playerY, playerX + playerCar.width, playerY + playerCar.height)
        val obstacleRect = RectF(obstacleX, obstacleY, obstacleX + obstacleCar.width, obstacleY + obstacleCar.height)

        // Check if the player car intersects with the obstacle car
        return playerRect.intersects(obstacleRect.left, obstacleRect.top, obstacleRect.right, obstacleRect.bottom)
    }


}
