package com.example.travel_photo_sharing_app.screens

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.travel_photo_sharing_app.databinding.ActivityLoginBinding
import com.example.travel_photo_sharing_app.MainActivity
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.utils.AuthenticationHelper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.launch

open class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
//    private lateinit var authenticationHelper: AuthenticationHelper
    open val tag = "Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // configure shared preferences
        this.sharedPreferences = getSharedPreferences("USERS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

//        this.authenticationHelper = AuthenticationHelper(this)

        val isFromMain = this@LoginActivity.intent.extras != null &&
                        this@LoginActivity.intent.extras!!.containsKey("REFERER") &&
                        this@LoginActivity.intent.getStringExtra("REFERER") == "MainActivity"
        if(isFromMain){
            Log.i(tag, "login needed")
            this@LoginActivity.intent.removeExtra("REFERER")
            Snackbar.make(binding.root, "Please login for further actions.", Snackbar.LENGTH_LONG).show()
        }

        binding.loginBtn.setOnClickListener {
            val email = this.binding.emailInput.text.toString()
            val password = this.binding.passwordInput.text.toString()

            lifecycleScope.launch {
//                authenticationHelper.signIn(email, password)
                AuthenticationHelper.instance!!.signIn(email, password)
            }

//            login(user)
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

    private fun successLogInCallBack(){

    }
    private fun failLogInCallBack(){

    }
    private fun successCreateCallBack(){

    }
    private fun failCreateCallBack(){

    }

}