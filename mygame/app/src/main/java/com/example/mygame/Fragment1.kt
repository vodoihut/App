package com.example.mygame

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import org.json.JSONArray


class Fragment1 : Fragment() , SensorEventListener {
    private lateinit var btnhighscore: Button
    private lateinit var tvQuestion: TextView
    private lateinit var tvContentQuestion: TextView
    private lateinit var tvScore: TextView
    private lateinit var rootView: View

    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var proximitySensor: Sensor
    private lateinit var vibrator: Vibrator

    private val questions = arrayOf(
        "What is the largest species of shark in the world?",
        "Which bird is known for its beautiful and elaborate tail feathers?",
        "What is the world's smallest mammal?",
        "Which of the following animals is a marsupial?",
        "What is the primary diet of a panda?",

        )
    private val answers = arrayOf(
        arrayOf("Great white shark", "Hammerhead shark", "Whale shark", "Tiger shark"),
        arrayOf("Penguin", "Peacock", "Sparrow", "Eagle"),
        arrayOf("African elephant", "Bumblebee bat", "Polar bear", "Giraffe"),
        arrayOf("Koala", "Panda", "Zebra", "Cheetah"),
        arrayOf("Bamboo", "Ants", "Fish", "Eucalyptus leaves"),

        )

    private val correctAnswers = arrayOf(
        "Whale shark",
        "Peacock",
        "Bumblebee bat",
        "Koala",
        "Bamboo",

        )

    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_1, container, false)
        tvQuestion = rootView.findViewById(R.id.tv_question)
        tvContentQuestion = rootView.findViewById(R.id.tv_content_question)
        tvScore = rootView.findViewById(R.id.tvscore)
        btnhighscore = rootView.findViewById(R.id.btnhighscore)

        // Initialize the SensorManager to manage sensors
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Get the light sensor and proximity sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!!

        // Initialize Vibrator to use the device's vibration feature
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Check if the device supports proximity sensor
        if (proximitySensor != null) {
            // If supported, register for proximity sensor event
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            // If not supported, display a notification to the user
            Toast.makeText(requireContext(), "The device does not support proximity sensors", Toast.LENGTH_SHORT).show()
        }
        // Check if the device has a light sensor
        if (lightSensor != null) {
            // If available, register for light sensor event
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            // If not available, display a notification to the user
            Toast.makeText(requireContext(), "The device does not support light sensors", Toast.LENGTH_SHORT).show()
        }

        updateQuestion()

        val answerButtons = arrayOf(
            rootView.findViewById<TextView>(R.id.tv_answer1),
            rootView.findViewById<TextView>(R.id.tv_answer2),
            rootView.findViewById<TextView>(R.id.tv_answer3),
            rootView.findViewById<TextView>(R.id.tv_answer4)
        )

        for (i in answerButtons.indices) {
            answerButtons[i].setOnClickListener {
                checkAnswer(answerButtons[i].text.toString())
            }
        }
        btnhighscore.setOnClickListener{
         highScore()
        }
        return rootView
    }

    private fun updateQuestion() {
        // Check if there are more questions to display
        if (currentQuestionIndex < questions.size) {
            // Set the question number and content on the UI
            tvQuestion.text = "Question ${currentQuestionIndex + 1}"
            tvContentQuestion.text = questions[currentQuestionIndex]
            val answerButtonIds = arrayOf(
                R.id.tv_answer1,
                R.id.tv_answer2,
                R.id.tv_answer3,
                R.id.tv_answer4
            )

            for (i in answerButtonIds.indices) {
                val answerButton = rootView.findViewById<TextView>(answerButtonIds[i])
                answerButton.text = answers[currentQuestionIndex][i]
            }

        } else {
            if (currentQuestionIndex == questions.size) {
                // Display a notification when the player has answered all questions correctly
                Toast.makeText(requireContext(), "You Win!", Toast.LENGTH_SHORT).show()
                saveHighScore(score)
                highScore()
            }
            // Return to the initial game screen
            resetGame()
        }
    }

    private fun resetGame() {
        // Reset the game to the initial state
        currentQuestionIndex = 0
        score = 0
        updateQuestion()
    }
    private fun highScore(){
        findNavController().navigate(R.id.action_to_fragment_high_score)
    }

    private fun checkAnswer(selectedAnswer: String) {
        if (currentQuestionIndex < questions.size) {
            if (selectedAnswer == correctAnswers[currentQuestionIndex]) {
                score++
                currentQuestionIndex++
                updateQuestion()
            } else {
                Toast.makeText(requireContext(), "You Lost!", Toast.LENGTH_SHORT).show()
                saveHighScore(score)
                resetGame()
                highScore()
            }

            tvScore.text = "Score: $score"
        }
    }

    private fun saveHighScore(score: Int) {

        val sharedPreferences = requireContext().getSharedPreferences("HighScores", Context.MODE_PRIVATE)
        val highScoresSet = sharedPreferences.getStringSet("highScores", HashSet<String>())

        // Add current score to the list
        highScoresSet?.add(score.toString())

        // Save new score list
        val editor = sharedPreferences.edit()
        editor.putStringSet("highScores", highScoresSet)
        editor.apply()

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT)
        {
            val lightValue = event.values[0]

            if (lightValue < 10) {
                setNightMode()
            } else {
                setDayMode()
            }
        }
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            if (distance < proximitySensor?.maximumRange ?: 10.0f) {
                // If an object is in proximity, trigger vibration
                showNotificationOrVibrate()
            }
        }
    }

    private fun showNotificationOrVibrate() {
        // Define a pattern for device vibration
        val pattern = longArrayOf(0, 100, 1000)
        vibrator.vibrate(pattern, -1) // Trigger the device vibration using the defined pattern
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the sensor event listener to release resources
        sensorManager.unregisterListener(this)
    }
    private fun setNightMode() {
        val layout1 = rootView.findViewById<View>(R.id.layoutgame)
        layout1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun setDayMode() {
        val layout = rootView.findViewById<View>(R.id.layoutgame)
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

    }
}