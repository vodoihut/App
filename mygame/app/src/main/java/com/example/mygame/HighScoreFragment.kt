package com.example.mygame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController

class HighScoreFragment : Fragment() {
    private lateinit var rootView1: View
    private lateinit var btnreturn: Button
    private lateinit var btnhome: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView1 = inflater.inflate(R.layout.fragment_high_score, container, false)
        btnreturn = rootView1.findViewById(R.id.btnreturn)
        btnhome = rootView1.findViewById(R.id.btnhome)

        // Get the list of scores from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("HighScores", Context.MODE_PRIVATE)
        val highScoresSet = sharedPreferences.getStringSet("highScores", HashSet<String>())

// Convert the score list to an integer list and sort it
        val highScoresList = highScoresSet?.map { it.toInt() }?.sortedDescending()

// Get the 3 highest scores
        val topThreeScores = highScoresList?.take(3)

// Show the 3 highest scores
        val textView1 = rootView1.findViewById<TextView>(R.id.textView1)
        val textView2 = rootView1.findViewById<TextView>(R.id.textView2)
        val textView3 = rootView1.findViewById<TextView>(R.id.textView3)

        if (topThreeScores != null && topThreeScores.isNotEmpty()) {
            textView1.text = "1st Place: ${topThreeScores[0]}"
            if (topThreeScores.size > 1) {
                textView2.text = "2nd Place: ${topThreeScores[1]}"
            }
            if (topThreeScores.size > 2) {
                textView3.text = "3rd Place: ${topThreeScores[2]}"
            }
        }
        btnreturn.setOnClickListener {
            findNavController().navigate(R.id.action_to_playagain)
        }
        btnhome.setOnClickListener {
            findNavController().navigate(R.id.action_to_home)
        }

        return rootView1
    }
}