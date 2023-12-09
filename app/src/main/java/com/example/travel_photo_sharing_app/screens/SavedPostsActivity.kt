package com.example.travel_photo_sharing_app.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_photo_sharing_app.adapters.PostAdapter
import com.example.travel_photo_sharing_app.databinding.ActivitySavedPostsBinding
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.utils.getLoggedInUser

class SavedPostsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedPostsBinding
    private var loggedInUser: User? = null
    private val tag = "Shortlist"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.returnBtn.setOnClickListener {
            finish()
        }

        loggedInUser = getLoggedInUser(this)
        if(loggedInUser != null){
            var savedPosts :MutableList<Post> = mutableListOf()
            if (loggedInUser != null) {
                savedPosts = loggedInUser!!.savedPosts
            }

            var adapter = PostAdapter(savedPosts, loggedInUser?.username ?: "", true)
            this.binding.shortlistRv.adapter = adapter
            this.binding.shortlistRv.layoutManager = LinearLayoutManager(this)
            this.binding.shortlistRv.addItemDecoration(
                DividerItemDecoration(
                    this,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

    }

}