package com.example.travel_photo_sharing_app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.travel_photo_sharing_app.MainActivity
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthenticationHelper(val context: Context) {
//    var isLoggedIn: Boolean = false
    var loggedInUser: User? = null
    val tag: String = "AuthenHelper"
//    private lateinit var firebaseAuth : FirebaseAuth
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val userRepository: UserRepository = UserRepository()

    companion object {
        var instance: AuthenticationHelper? = null
        fun getInstance(context: Context?): AuthenticationHelper{
            return if(instance != null){
                Log.d(tag, "getting existing instance, user ${instance!!.loggedInUser}")
                instance!!
            }
            else{
                if(context != null){
                    Log.d(tag, "getting new instance")
                    instance = AuthenticationHelper(context)
                    instance!!
                }
                else{
                    Log.e(tag, "Please provide a context to create a authenticationHelper instance")
                    throw Exception("Please provide a context to create a authenticationHelper instance")
                }
            }
        }
//        fun setContext(context: Context) {
//
//        }
    }

//    fun getLoggedInUser(): User?{
////        val userEmail: String? = firebaseAuth.currentUser?.email
////        if(userEmail == null){
////            Log.d(tag, "userEmail is null")
////            return null
////        }
////        else{
////            Log.d(tag, "userEmail is ${userEmail}")
////            return userRepository.findUserByEmail(userEmail)
////        }
//
//
////        val loggedInUser = User()
////        return loggedInUser
//
//        return this.loggedInUser
//    }

    suspend fun signUp(newUser: User) {
//        SignUp using FirebaseAuth
        this.firebaseAuth
            .createUserWithEmailAndPassword(newUser.email, newUser.password)
            .addOnCompleteListener(context as Activity){task ->
                if (task.isSuccessful){
                    (context as AppCompatActivity).lifecycleScope.launch {
                        userRepository.addUserToDB(newUser)
                        Log.d(tag, "createAccount: User ${newUser} created")
                        // user is automatically logged in after creation
                        signIn(newUser.email, newUser.password)
                    }
                }else{
                    Log.d(tag, "createAccount: Unable to create user account : ${task.exception}", )
                    Toast.makeText(context, "Account creation failed due to ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }

    }

    
    suspend fun signIn(email: String, password: String) {
        //signIn using FirebaseAuth
        this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "signIn: Login successful ${firebaseAuth.currentUser?.email}")
//                    isLoggedIn = true
                    (context as AppCompatActivity).lifecycleScope.launch {
                        val userEmail: String? = firebaseAuth.currentUser?.email
                        if(userEmail == null){
                            Log.d(tag, "userEmail is null")
                            loggedInUser = null
                        }
                        else{
                            Log.d(tag, "userEmail is ${userEmail}")
                            loggedInUser = userRepository.findUserByEmail(userEmail)
                        }
//                        loggedInUser = getLoggedInUser()
                        Log.d(tag, "logged in user is ${loggedInUser}")
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                } else {
                    Log.e(tag, "signIn: Login Failed : ${task.exception}")
                    Toast.makeText(context, "Authentication failed. Check the credentials", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun signOut(){
        Log.d(tag, "signing out")
        firebaseAuth.signOut()
        loggedInUser = null

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("REFERER", "SignOut")
        context.startActivity(intent)
//        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
    }
}