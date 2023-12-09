package com.example.travel_photo_sharing_app.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_photo_sharing_app.databinding.ActivityMyPostsBinding
import com.example.travel_photo_sharing_app.MainActivity
import com.example.travel_photo_sharing_app.adapters.MyPostAdapter
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.utils.saveDataToSharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MyPostsActivity : MainActivity() {
    lateinit var binding: ActivityMyPostsBinding
    lateinit var adapter: MyPostAdapter
    private var datasource: MutableList<Post> = mutableListOf<Post>()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    private var loggedInUserName: String = ""
    override val tag = "Landlord"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.sharedPreferences = getSharedPreferences("POSTS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()


        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.title = "Your Post List"

        loggedInUserName = this.intent.getStringExtra("USER")!!
        val myPostsJson = sharedPreferences.getString(loggedInUserName, "")
        val myPosts = if(myPostsJson != "") Gson().fromJson<List<Post>>(myPostsJson, object : TypeToken<List<Post>>() {}.type) else mutableListOf()
        datasource.addAll(myPosts)
        Log.i(tag, "datasource is ${datasource}")

        adapter = MyPostAdapter(
            datasource,
            { pos -> rowClicked(pos) },
            { pos -> deletePost(pos) },
            { pos -> editClicked(pos)}
        )

        binding.returnBtn.setOnClickListener {
            finish()
        }

        binding.addPostBtn.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            intent.putExtra("USER", loggedInUserName)
            startActivity(intent)
        }

        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

    }


    private fun rowClicked(position: Int) {
        val selectedPost: Post = datasource[position]
        val intent = Intent(this, PostDetailActivity::class.java)

        intent.putExtra("POST", selectedPost)
        startActivity(intent)
    }

    private fun editClicked(position: Int) {
        val selectedPost: Post = datasource[position]
        val intent = Intent(this, AddPostActivity::class.java)
        intent.putExtra("USER", loggedInUserName)
        intent.putExtra("POST_DATA", selectedPost)
        intent.putExtra("INDEX", position)

        startActivity(intent)
    }


    private fun deletePost(position: Int) {
        datasource.removeAt(position)
        saveDataToSharedPref(this, "POSTS", loggedInUserName, datasource, true )
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        val postsListFromSP = sharedPreferences.getString(loggedInUserName, "")
        if (postsListFromSP != "") {
            val gson = Gson()
            val typeToken = object : TypeToken<List<Post>>() {}.type
            val postsList = gson.fromJson<List<Post>>(postsListFromSP, typeToken)

            datasource.clear()
            datasource.addAll(postsList)
            adapter.notifyDataSetChanged()
        }

    }
}