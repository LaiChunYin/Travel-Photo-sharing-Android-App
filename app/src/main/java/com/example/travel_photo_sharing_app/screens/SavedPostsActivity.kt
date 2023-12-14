package com.example.travel_photo_sharing_app.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_photo_sharing_app.adapters.PostAdapter
import com.example.travel_photo_sharing_app.databinding.ActivitySavedPostsBinding
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.repositories.PostRepository
import com.example.travel_photo_sharing_app.repositories.UserRepository
import com.example.travel_photo_sharing_app.utils.AuthenticationHelper
import kotlinx.coroutines.launch

class SavedPostsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedPostsBinding
    private var loggedInUser: User? = null
    private val tag = "Shortlist"
    private val postRepository = PostRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

//        binding.returnBtn.setOnClickListener {
//            finish()
//        }

//        loggedInUser = getLoggedInUser(this)
        loggedInUser = AuthenticationHelper.instance!!.loggedInUser
//        if(loggedInUser != null){
//            var savedPosts :MutableList<Post> = mutableListOf()
//            if (loggedInUser != null) {
//                savedPosts = loggedInUser!!.savedPosts
//            }
//
////            var adapter = PostAdapter(savedPosts, loggedInUser?.username ?: "", true)
//            var adapter = PostAdapter(savedPosts, loggedInUser, true, this@SavedPostsActivity)
//            this.binding.shortlistRv.adapter = adapter
//            this.binding.shortlistRv.layoutManager = LinearLayoutManager(this)
//            this.binding.shortlistRv.addItemDecoration(
//                DividerItemDecoration(
//                    this,
//                    LinearLayoutManager.VERTICAL
//                )
//            )
//        }

        lifecycleScope.launch {
            if(loggedInUser != null){
                var savedPosts :MutableList<Post> = mutableListOf()
                for(postId in loggedInUser!!.savedPosts){
                    savedPosts.add(postRepository.getPostById(postId)!!)
                }

//            var adapter = PostAdapter(savedPosts, loggedInUser?.username ?: "", true)
                var adapter = PostAdapter(savedPosts, loggedInUser, true, this@SavedPostsActivity)
                this@SavedPostsActivity.binding.shortlistRv.adapter = adapter
                this@SavedPostsActivity.binding.shortlistRv.layoutManager = LinearLayoutManager(this@SavedPostsActivity)
                this@SavedPostsActivity.binding.shortlistRv.addItemDecoration(
                    DividerItemDecoration(
                        this@SavedPostsActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
            }
        }

    }

}