package com.example.travel_photo_sharing_app.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.screens.LoginActivity
import com.example.travel_photo_sharing_app.screens.PostDetailActivity
import com.google.gson.Gson
import android.view.View
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.databinding.ItemPostBinding
import com.example.travel_photo_sharing_app.utils.getLoggedInUser
import com.example.travel_photo_sharing_app.utils.saveDataToSharedPref
import com.example.travel_photo_sharing_app.utils.sharedPreferences

class PostAdapter(private var posts: MutableList<Post>, private var loggedInUserName: String, private val showShortlistOnly: Boolean) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var loggedInUser: User? = null
    private val savedPosts: MutableList<String> = mutableListOf()
    private val tag = "Post Adapter"

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, context: Context, pos: Int) {
            if(loggedInUserName != ""){
                loggedInUser = getLoggedInUser(loggedInUserName, context)
                savedPosts.clear()
                for (post in loggedInUser!!.savedPosts){
                    savedPosts.add(post.address)
                }
            }

            // Assuming you want to display the post type as the title
            binding.postTitleTextView.text = post.type
            binding.postDescriptionTextView.text = post.description
            binding.postAddressTextView.text = post.address
//            binding.postCityPostalTextView.text = "${post.city}, ${post.postalCode}"
//            Glide.with(binding.root.context).load(post.imageUrl).into(binding.postImage)  // for online images

            val imageName = post.imageUrl ?: "default_image"
            val res = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            this.binding.postImage.setImageResource(res)

            if(loggedInUser != null && loggedInUser?.userType == "Tenant" && savedPosts.contains(post.address)){
                binding.removeBtn.visibility = View.VISIBLE
                binding.shortListBtn.visibility = View.GONE
            }
            else if(loggedInUser != null && loggedInUser?.userType == "Tenant"){
                binding.shortListBtn.visibility = View.VISIBLE
                binding.removeBtn.visibility = View.GONE
            }
            else {
                binding.shortListBtn.visibility = View.GONE
                binding.removeBtn.visibility = View.GONE
            }


            binding.postCard.setOnClickListener {
                // popup post details
                if(this@PostAdapter.loggedInUser != null){
                    Log.i(tag, "${loggedInUser} logged in")
                    val intent = Intent(context, PostDetailActivity::class.java)
                    intent.putExtra("POST", post)
                    context.startActivity(intent)
                }
                else {
                    Log.i(tag, "no one logged in")
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.putExtra("REFERER", "MainActivity")
                    context.startActivity(intent)
                }
            }

            binding.shortListBtn.setOnClickListener {
                loggedInUser = Gson().fromJson(sharedPreferences.getString(loggedInUser?.username, ""), User::class.java)
                loggedInUser!!.savedPosts.add(post)
                saveDataToSharedPref(context, "USERS", loggedInUser!!.username, loggedInUser!!, true)

                if(showShortlistOnly) {
                    posts = loggedInUser!!.savedPosts
                }
                this@PostAdapter.notifyDataSetChanged()
            }

            binding.removeBtn.setOnClickListener {
                loggedInUser = getLoggedInUser(loggedInUserName, context)

                for(i in 0..< loggedInUser!!.savedPosts.size){
                    if(loggedInUser!!.savedPosts[i].equals(post)){
                        loggedInUser!!.savedPosts.removeAt(i)
                        break
                    }
                }
                saveDataToSharedPref(context, "USERS", loggedInUser!!.username, loggedInUser!!, true)

                if(showShortlistOnly){
                    posts = loggedInUser!!.savedPosts
                }
                this@PostAdapter.notifyDataSetChanged()
            }

            // Set the availability tag based on the availableForRent post
            val availabilityTag = binding.postAvailabilityTag
            if (post.visibleToGuest) {
                // Post is available
                availabilityTag.text = context.getString(R.string.available)
                availabilityTag.visibility = View.VISIBLE
                availabilityTag.setBackgroundResource(R.drawable.available_tag_background) // Green background for available
            } else {
                // Post is not available
                availabilityTag.text = context.getString(R.string.not_available)
                availabilityTag.visibility = View.VISIBLE
                availabilityTag.setBackgroundResource(R.drawable.not_available_tag_background) // Red background for not available
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.itemView.context
        holder.bind(post, context, position)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

}
