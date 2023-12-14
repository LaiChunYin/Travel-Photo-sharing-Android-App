package com.example.travel_photo_sharing_app.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_photo_sharing_app.databinding.ActivityMyPostsBinding
import com.example.travel_photo_sharing_app.MainActivity
import com.example.travel_photo_sharing_app.adapters.MyPostAdapter
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.repositories.PostRepository
import com.example.travel_photo_sharing_app.repositories.UserRepository
import com.example.travel_photo_sharing_app.utils.AuthenticationHelper
import com.example.travel_photo_sharing_app.utils.saveDataToSharedPref
import com.example.travel_photo_sharing_app.utils.sharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch


class MyPostsActivity : MainActivity() {
    lateinit var binding: ActivityMyPostsBinding
    lateinit var adapter: MyPostAdapter
    private var datasource: MutableList<Post> = mutableListOf<Post>()
//    lateinit var sharedPreferences: SharedPreferences
//    lateinit var prefEditor: SharedPreferences.Editor
//    private var loggedInUserName: String = ""
    private var loggedInUser: User? = null
    private val postRepository = PostRepository()
    private val userRepository = UserRepository()
    override val tag = "Landlord"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        this.sharedPreferences = getSharedPreferences("POSTS", MODE_PRIVATE)
//        this.prefEditor = this.sharedPreferences.edit()


        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.title = "Your Post List"

//        loggedInUserName = this.intent.getStringExtra("USER")!!
//        val myPostsJson = sharedPreferences.getString(loggedInUserName, "")
//        val myPosts = if(myPostsJson != "") Gson().fromJson<List<Post>>(myPostsJson, object : TypeToken<List<Post>>() {}.type) else mutableListOf()
//        datasource.addAll(myPosts)

        Log.i(tag, "datasource is ${datasource}")

        adapter = MyPostAdapter(
            datasource,
            this@MyPostsActivity,
            { pos -> rowClicked(pos) },
            { pos -> deletePost(pos) },
            { pos -> editClicked(pos)}
        )

        binding.returnBtn.setOnClickListener {
            finish()
        }

        binding.addPostBtn.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            intent.putExtra("USER", loggedInUser!!.username)
            startActivity(intent)
        }

        binding.myPostsRv.adapter = adapter
        binding.myPostsRv.layoutManager = LinearLayoutManager(this)
        binding.myPostsRv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

    }


    private fun rowClicked(position: Int) {
        val selectedPost: Post = datasource[position]
        val intent = Intent(this, PostDetailActivity::class.java)

        intent.putExtra("POST", selectedPost.idFromDb)
        startActivity(intent)
    }

    private fun editClicked(position: Int) {
        val selectedPost: Post = datasource[position]
        val intent = Intent(this, AddPostActivity::class.java)
        intent.putExtra("USER", loggedInUser!!.username)
//        intent.putExtra("POST_DATA", selectedPost)
        intent.putExtra("POST", selectedPost.idFromDb) // pass only the email since passing the whole object is too large, which will cause an error
        intent.putExtra("INDEX", position)

        startActivity(intent)
    }


    private fun deletePost(position: Int) {
        val postToBeDeleted = datasource[position]
        Log.d(tag, "deleting post ${postToBeDeleted} by ${loggedInUser!!.email}")
        postRepository.deletePost(postToBeDeleted.idFromDb!!, loggedInUser!!.email)
        datasource.removeAt(position)
//        saveDataToSharedPref(this, "POSTS", loggedInUser!!.username, datasource, true )
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

//        val postsListFromSP = sharedPreferences.getString(loggedInUserName, "")
//        if (postsListFromSP != "") {
//            val gson = Gson()
//            val typeToken = object : TypeToken<List<Post>>() {}.type
//            val postsList = gson.fromJson<List<Post>>(postsListFromSP, typeToken)
//
//            datasource.clear()
//            datasource.addAll(postsList)
//            adapter.notifyDataSetChanged()
//        }
//        loggedInUser = AuthenticationHelper.instance!!.loggedInUser
        AuthenticationHelper.instance!!.loggedInUser.observe(this) {user ->
            loggedInUser = user

            lifecycleScope.launch {
                Log.d(tag, "getting my posts ${loggedInUser!!.createdPosts}")
                userRepository.getAllMyPosts(loggedInUser!!.createdPosts)
                userRepository.allMyPosts.observe(this@MyPostsActivity) {myPosts ->
                    Log.d(tag, "myPosts are ${myPosts}")
                    datasource.clear()
                    datasource.addAll(myPosts)
                    adapter.notifyDataSetChanged()
                }
            }
        }




    }
}