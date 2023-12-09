package com.example.travel_photo_sharing_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.databinding.ActivityMainBinding
import com.example.travel_photo_sharing_app.adapters.PostAdapter
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.screens.LoginActivity
import com.example.travel_photo_sharing_app.screens.SavedPostsActivity
import com.example.travel_photo_sharing_app.utils.getLoggedInUser
import com.example.travel_photo_sharing_app.screens.MyPostsActivity
import com.example.travel_photo_sharing_app.utils.initializePosts
import com.google.android.material.snackbar.Snackbar

open class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var postAdapter: PostAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
    private var postsToBeDisplayed: MutableList<Post> = mutableListOf()
    private var allPosts: MutableList<Post> = mutableListOf()
//    private var loggedInUserName: String = ""
    private var loggedInUser: User? = null
    open val tag = "Main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allPosts = initializePosts(this)
        loggedInUser = getLoggedInUser(this)

        Log.i(tag, "post to be displayed ${allPosts}")

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val justLoggedOut = (this.intent.getStringExtra("REFERER") ?: null) == "MainActivity"
        if(justLoggedOut){
            Snackbar.make(binding.root, "Logout Successful", Snackbar.LENGTH_LONG).show()
        }
        val justClearedData = (this.intent.getStringExtra("REFERER") ?: null) == "Toolbar"
        if(justClearedData){
            Snackbar.make(findViewById(R.id.root_layout), "Data erased!", Snackbar.LENGTH_LONG).show()
        }

        postsToBeDisplayed.addAll(allPosts)
        postAdapter = PostAdapter(postsToBeDisplayed, loggedInUser?.username ?: "", false)

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.postsRecyclerView.adapter = postAdapter

        binding.searchButton.setOnClickListener {
            performSearch(binding.searchEditText.text.toString())
        }

        binding.condoImage.setOnClickListener {
            triggerSearchWithType("Condo")
        }

        binding.houseImage.setOnClickListener {
            triggerSearchWithType("House")
        }

        binding.apartmentImage.setOnClickListener {
            triggerSearchWithType("Apartment")
        }
    }

    // Function to trigger search with type
    private fun triggerSearchWithType(type: String) {
        binding.searchEditText.setText(type)
        performSearch(type)
    }

    override fun onResume() {
        loggedInUser = getLoggedInUser(this)
        allPosts.clear()
        allPosts = initializePosts(this)
        postsToBeDisplayed.clear()
        postsToBeDisplayed.addAll(allPosts)
        postAdapter.notifyDataSetChanged()

        super.onResume()
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        if(loggedInUser == null || loggedInUser?.userType == "Landlord") {
            menu.findItem(R.id.go_to_saved_posts).setVisible(false)
        }
        if(loggedInUser?.userType == "Tenant") {
            menu.findItem(R.id.add_post).setVisible(false)
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.go_to_saved_posts -> {
                val intent = Intent(this, SavedPostsActivity::class.java)
                intent.putExtra("USER", loggedInUser?.username)
                startActivity(intent)
                return true
            }
            R.id.add_post -> {
                if(loggedInUser != null) {
                    val intent = Intent(this, MyPostsActivity::class.java)
                    intent.putExtra("USER", loggedInUser?.username)
                    startActivity(intent)
                    return true
                }
                else {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("REFERER", "MainActivity")
                    startActivity(intent)
                    return true
                }
            }
            R.id.logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER", "")
                intent.putExtra("REFERER", "MainActivity")
                startActivity(intent)
                return true
            }
            // for testing only. Remove this later
            R.id.delete_users -> {
                this.sharedPreferences = getSharedPreferences("USERS", MODE_PRIVATE)
                this.prefEditor = this.sharedPreferences.edit()

                prefEditor.clear()
                prefEditor.apply()

                // logs out the user after all users are deleted
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER", "")
                intent.putExtra("REFERER", "Toolbar")
                startActivity(intent)
//                Snackbar.make(findViewById(R.id.root_layout), "Data erased!", Snackbar.LENGTH_LONG).show()
                return true
            }
            R.id.delete_posts -> {
                this.sharedPreferences = getSharedPreferences("POSTS", MODE_PRIVATE)
                this.prefEditor = this.sharedPreferences.edit()

                prefEditor.clear()
                prefEditor.apply()
                Snackbar.make(findViewById(R.id.root_layout), "post erased!", Snackbar.LENGTH_LONG).show()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun performSearch(query: String) {
        val filteredPosts = if(query != "") allPosts.filter { post ->
            post.matchesQuery(query)
        } else allPosts
        Log.i(tag, "filtered posts: $filteredPosts")
        postsToBeDisplayed.clear()
        postsToBeDisplayed.addAll(filteredPosts)
        postAdapter.notifyDataSetChanged()
    }

}
