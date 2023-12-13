package com.example.travel_photo_sharing_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.databinding.ActivityMainBinding
import com.example.travel_photo_sharing_app.adapters.PostAdapter
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.repositories.PostRepository
import com.example.travel_photo_sharing_app.screens.FollowerFolloweeActivity
import com.example.travel_photo_sharing_app.screens.LoginActivity
import com.example.travel_photo_sharing_app.screens.SavedPostsActivity
//import com.example.travel_photo_sharing_app.utils.getLoggedInUser
import com.example.travel_photo_sharing_app.screens.MyPostsActivity
import com.example.travel_photo_sharing_app.screens.PostDetailActivity
import com.example.travel_photo_sharing_app.utils.AuthenticationHelper
import com.example.travel_photo_sharing_app.utils.initializePosts
import com.example.travel_photo_sharing_app.utils.tag
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

open class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var postAdapter: PostAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
    private var postsToBeDisplayed: MutableList<Post> = mutableListOf()
    private var allPosts: MutableList<Post> = mutableListOf()
    private lateinit var allPublicPosts: MutableLiveData<List<Post>>
//    private var loggedInUserName: String = ""
    private var loggedInUser: User? = null
//    private val authenticationHelper = AuthenticationHelper.getInstance(this)
    open val tag = "Main"
    private val postRepository = PostRepository()
    private val markerPostMap = HashMap<Marker, Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        allPosts = initializePosts(this)
//        initializePosts(this)
//        loggedInUser = getLoggedInUser(this)
//        loggedInUser = authenticationHelper.getLoggedInUser()
        AuthenticationHelper.getInstance(this)
        loggedInUser = AuthenticationHelper.instance!!.loggedInUser
        Log.d(tag, "in main, loggedin users is $loggedInUser")

        Log.i(tag, "post to be displayed ${allPosts}")

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.view?.visibility = View.GONE

        val justLoggedOut = (this.intent.getStringExtra("REFERER") ?: null) == "Signout"
        if(justLoggedOut){
            Snackbar.make(binding.root, "Logout Successful", Snackbar.LENGTH_LONG).show()
        }
        val justClearedData = (this.intent.getStringExtra("REFERER") ?: null) == "Toolbar"
        if(justClearedData){
            Snackbar.make(findViewById(R.id.root_layout), "Data erased!", Snackbar.LENGTH_LONG).show()
        }

        postRepository.getAllPublicPosts()

        binding.viewSelection.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            // Respond to button selection
            var view = findViewById<Button>(checkedId).text.toString()
            Log.d(tag, "view is ${view}")
            Log.d(tag, "view clicked ${toggleButton}, ${checkedId}, ${isChecked}")
            if(view == "List"){
                binding.listViewBtn.setBackgroundColor(getColor(R.color.light_blue))
                binding.mapViewBtn.setBackgroundColor(getColor(R.color.light_grey))

                binding.postsRecyclerView.visibility = View.VISIBLE
                mapFragment.view?.visibility = View.GONE
            }
            else if(view == "Map"){
                binding.listViewBtn.setBackgroundColor(getColor(R.color.light_grey))
                binding.mapViewBtn.setBackgroundColor(getColor(R.color.light_blue))

                binding.postsRecyclerView.visibility = View.GONE
                mapFragment.view?.visibility = View.VISIBLE
            }
        }

//        postAdapter = PostAdapter(postsToBeDisplayed, loggedInUser?.username ?: "", false)
        postAdapter = PostAdapter(postsToBeDisplayed, loggedInUser, false, this@MainActivity)
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.postsRecyclerView.adapter = postAdapter
//        postRepository.publicPosts.observe(this){ publicPosts ->
//            Log.d(tag, "in observer ${publicPosts}")
//            postsToBeDisplayed.addAll(publicPosts)
//            postAdapter.notifyDataSetChanged()
//        }


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
//        loggedInUser = getLoggedInUser(this)
//        loggedInUser = authenticationHelper.getLoggedInUser()
        loggedInUser = AuthenticationHelper.instance!!.loggedInUser
        allPosts.clear()
//        allPosts = initializePosts(this)
//        initializePosts(this)
        postRepository.publicPosts.observe(this){ publicPosts ->
            Log.d(tag, "in observer ${publicPosts}")
            postsToBeDisplayed.clear()
            postsToBeDisplayed.addAll(publicPosts)
            postAdapter.notifyDataSetChanged()
            addPostsToMap(postsToBeDisplayed)
        }
//        postsToBeDisplayed.clear()
//        postsToBeDisplayed.addAll(allPosts)
//        postAdapter.notifyDataSetChanged()

        super.onResume()
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        Log.d(tag, "logged in user in menu ${loggedInUser}")
        if(loggedInUser == null) {
            menu.findItem(R.id.go_to_saved_posts).setVisible(false)
            menu.findItem(R.id.add_post).setVisible(false)
            menu.findItem(R.id.followersOrFollowees).setVisible(false)
            menu.findItem(R.id.logout).setVisible(false)
        }
        else {
            menu.findItem(R.id.login).setVisible(false)
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                val intent = Intent(this, LoginActivity::class.java)
//                intent.putExtra("USER", "")
//                intent.putExtra("REFERER", "MainActivity")
                startActivity(intent)
                return true
            }
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
            R.id.followersOrFollowees -> {
                val intent = Intent(this, FollowerFolloweeActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout -> {
                val intent = Intent(this, MainActivity::class.java)
                AuthenticationHelper.instance!!.signOut()
//                intent.putExtra("USER", "")
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

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(tag, "on map ready")
        mMap = googleMap
        Log.d(tag, "google map ${postsToBeDisplayed}")

        mMap.setOnInfoWindowClickListener {  marker ->
                Log.d(tag, "here info window clicked")
                markerClickedHandler(marker)
        }

        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.isTrafficEnabled = true

        val uiSettings = googleMap.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true

    }

    private fun markerClickedHandler(marker: Marker): Boolean {
        Log.d(tag, "marker clicked: ${marker}, ${markerPostMap}")
        // popup post details
        val post = markerPostMap[marker]
        Log.d(tag, "marketPostMap post: ${post}")
        if(loggedInUser != null){
            Log.i(tag, "${loggedInUser} logged in")
            val intent = Intent(this@MainActivity, PostDetailActivity::class.java)
            intent.putExtra("POST", post)
            this@MainActivity.startActivity(intent)
        }
        else {
            Log.i(tag, "no one logged in")
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("REFERER", "MainActivity")
            this@MainActivity.startActivity(intent)
        }
        return false
    }

    private fun addPostsToMap(posts: MutableList<Post>){
        Log.d(tag, "adding posts to map")
        var cameraSet = false  // set the camera position to the first post by default

        for(post in posts){
            val location = LatLng(post.latitude, post.longitude)
            Log.d(tag, "in addPostsToMap ${post}, ${location}")

            if(!cameraSet){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                cameraSet = true
            }

            val markerOption = MarkerOptions().position(location)
            markerOption.title(post.address)
            val marker: Marker = mMap.addMarker(markerOption)!!
            markerPostMap[marker] = post
            Log.d(tag, "Marker set is ${markerOption}")
        }
    }
}
