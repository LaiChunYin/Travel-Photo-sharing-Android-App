package com.example.travel_photo_sharing_app.screens

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.travel_photo_sharing_app.databinding.ActivityLoginBinding
import com.example.travel_photo_sharing_app.MainActivity
import com.example.travel_photo_sharing_app.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

open class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
    open val tag = "Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // configure shared preferences
        this.sharedPreferences = getSharedPreferences("USERS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        val isFromMain = this@LoginActivity.intent.extras != null &&
                        this@LoginActivity.intent.extras!!.containsKey("REFERER") &&
                        this@LoginActivity.intent.getStringExtra("REFERER") == "MainActivity"
        if(isFromMain){
            Log.i(tag, "login needed")
            this@LoginActivity.intent.removeExtra("REFERER")
            Snackbar.make(binding.root, "Please login for further actions.", Snackbar.LENGTH_LONG).show()
        }

        binding.loginBtn.setOnClickListener {
            val username = this.binding.usernameInput.text.toString()
            val password = this.binding.passwordInput.text.toString()

            val userJson = sharedPreferences.getString(username, "")
            val user = Gson().fromJson(userJson, User::class.java)

            if(user == null || password != user.password) {
                Snackbar.make(binding.root, "Invalid username and password", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            login(user)
        }

        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    protected fun login(user: User){
        Log.i(tag, "logging in")
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("USER", user.username)
        startActivity(intent)
    }

}