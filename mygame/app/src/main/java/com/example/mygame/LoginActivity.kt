package com.example.mygame

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val userName = findViewById<EditText>(R.id.username)
        val passWord = findViewById<EditText>(R.id.password)
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        btnlogin.setOnClickListener {
            val username = userName.text.toString()
            val password = passWord.text.toString()

            if (username == "admin" && password == "123") {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {

                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}