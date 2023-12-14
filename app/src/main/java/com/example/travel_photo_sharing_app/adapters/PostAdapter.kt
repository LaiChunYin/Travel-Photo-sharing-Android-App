package com.example.travel_photo_sharing_app.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.databinding.ItemPostBinding
import com.example.travel_photo_sharing_app.repositories.PostRepository
import com.example.travel_photo_sharing_app.repositories.UserRepository
import com.example.travel_photo_sharing_app.utils.CameraImageHelper
//import com.example.travel_photo_sharing_app.utils.getLoggedInUser
import com.example.travel_photo_sharing_app.utils.saveDataToSharedPref
import com.example.travel_photo_sharing_app.utils.sharedPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PostAdapter(private var posts: MutableList<Post>, var loggedInUser: User?, private val showShortlistOnly: Boolean, context: Context) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
//class PostAdapter(private var posts: MutableList<Post>, private var loggedInUserName: String, private val showShortlistOnly: Boolean) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
//class PostAdapter(private var posts: MutableList<Post>, private var loggedInUserEmail: String, private val showShortlistOnly: Boolean) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
//    private var loggedInUser: User? = null
//    private val savedPosts: MutableList<String> = mutableListOf()
    private val userRepository = UserRepository()
    private val postRepository = PostRepository()
    private val tag = "Post Adapter"
//    private val loggedInUserPosts = mutableListOf<Post>()
//
//    init {
//        if(loggedInUser != null){
//            (context as AppCompatActivity).lifecycleScope.launch {
//                for(postId in loggedInUser!!.savedPosts){
//                    val savedPost = postRepository.getPostById(postId)
//                    loggedInUserPosts.add(savedPost!!)
//                }
//            }
//        }
//    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, context: Context, pos: Int) {
//        suspend fun bind(post: Post, context: Context, pos: Int) {
//            if(loggedInUserName != ""){
////            if(loggedInUserEmail != ""){
//                loggedInUser = getLoggedInUser(loggedInUserName, context)
////                loggedInUser = userRepository.findUserByEmail(loggedInUserEmail)
//                savedPosts.clear()
//                for (post in loggedInUser!!.savedPosts){
//                    savedPosts.add(post.address)
//                }
//            }
            Log.d(tag, "in tag, post and loggedInUser ${post}, ${loggedInUser}")
//            if(loggedInUser != null){
//                savedPosts.clear()
//                for (savedPostId in loggedInUser!!.savedPosts){
//                    savedPosts.add(post.idFromDb!!)
//                }
//            }

            // Assuming you want to display the post type as the title
            binding.postAuthor.text = post.authorEmail
            binding.postType.text = post.type
            binding.postDescriptionTextView.text = post.description
            binding.postAddressTextView.text = post.address
            binding.postId.text = post.idFromDb
//            binding.postCityPostalTextView.text = "${post.city}, ${post.postalCode}"
//            Glide.with(binding.root.context).load(post.imageUrl).into(binding.postImage)  // for online images

//            val imageName = post.imageUrl ?: "default_image"
//            val res = context.resources.getIdentifier(imageName, "drawable", context.packageName)
//            this.binding.postImage.setImageResource(res)
            Log.d(tag, "post image is ${post.imageUrl}")
            val image = post.imageUrl ?: "default_image"
            if(image == "default_image"){
                val res = context.resources.getIdentifier(image, "drawable", context.packageName)
                this.binding.postImage.setImageResource(res)
            }
            else{
                val imgBitmap: Bitmap = CameraImageHelper.base64ToBitmap(image)
                this.binding.postImage.setImageBitmap(imgBitmap)
            }

//            Log.d(tag, "savedpost and idfromdb ${savedPosts}, ${post.idFromDb}")
            Log.d(tag, "savedpost and idfromdb ${loggedInUser?.savedPosts}, ${post.idFromDb}")
//            if(loggedInUser != null && savedPosts.contains(post.idFromDb)){
            if(loggedInUser != null && loggedInUser!!.savedPosts.contains(post.idFromDb)){
                binding.removeBtn.visibility = View.VISIBLE
                binding.savePostBtn.visibility = View.GONE
            }
            else if(loggedInUser != null){
                binding.savePostBtn.visibility = View.VISIBLE
                binding.removeBtn.visibility = View.GONE
            }
            else {
                binding.savePostBtn.visibility = View.GONE
                binding.removeBtn.visibility = View.GONE
            }


            binding.postCard.setOnClickListener {
                // popup post details
                if(this@PostAdapter.loggedInUser != null){
                    Log.i(tag, "${loggedInUser} logged in")
                    val intent = Intent(context, PostDetailActivity::class.java)
                    intent.putExtra("POST", post.idFromDb) // pass only the email since passing the whole object is too large, which will cause an error
                    context.startActivity(intent)
                }
                else {
                    Log.i(tag, "no one logged in")
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.putExtra("REFERER", "MainActivity")
                    context.startActivity(intent)
                }
            }

            binding.savePostBtn.setOnClickListener {
//                loggedInUser = Gson().fromJson(sharedPreferences.getString(loggedInUser?.username, ""), User::class.java)
//                loggedInUser!!.savedPosts.add(post)
//                saveDataToSharedPref(context, "USERS", loggedInUser!!.username, loggedInUser!!, true)

//                if(showShortlistOnly) {
//                    posts = loggedInUser!!.savedPosts
//                }
//                this@PostAdapter.notifyDataSetChanged()

                (context as AppCompatActivity).lifecycleScope.launch {
                    val postId = post.idFromDb
                    userRepository.savePost(loggedInUser!!.email, postId!!)
                    loggedInUser!!.savedPosts.add(postId!!)
//                    savedPosts.add(postId)
                    Log.d(tag, "post id is ${postId}")
                    this@PostAdapter.notifyDataSetChanged()
                }
            }

            binding.removeBtn.setOnClickListener {
//                loggedInUser = getLoggedInUser(loggedInUserName, context)
//                loggedInUser = userRepository.findUserByEmail(loggedInUserEmail)

                val postIdToBeRemoved = post.idFromDb
                (context as AppCompatActivity).lifecycleScope.launch {
                    Log.d(tag,"removing saved post")
                    userRepository.unSavePost(loggedInUser!!.email, postIdToBeRemoved!!)
                }
                for(i in 0..< loggedInUser!!.savedPosts.size){
                    Log.d(tag, "savedPosts id is ${loggedInUser!!.savedPosts[i]}, idFromDb is ${post.idFromDb}")
                    if(loggedInUser!!.savedPosts[i] == post.idFromDb){
                        loggedInUser!!.savedPosts.removeAt(i)
                        if(showShortlistOnly){
                            posts.removeAt(i)
                        }
                        break
                    }
                }
//                saveDataToSharedPref(context, "USERS", loggedInUser!!.username, loggedInUser!!, true)

//                if(showShortlistOnly){
////                    posts = loggedInUser!!.savedPosts
////                    posts = loggedInUserPosts
//                }
                this@PostAdapter.notifyDataSetChanged()


            }

            // Set the availability tag based on the availableForRent post
//            val availabilityTag = binding.postAvailabilityTag
//            if (post.visibleToGuest) {
//                // Post is available
//                availabilityTag.text = context.getString(R.string.available)
//                availabilityTag.visibility = View.VISIBLE
//                availabilityTag.setBackgroundResource(R.drawable.available_tag_background) // Green background for available
//            } else {
//                // Post is not available
//                availabilityTag.text = context.getString(R.string.not_available)
//                availabilityTag.visibility = View.VISIBLE
//                availabilityTag.setBackgroundResource(R.drawable.not_available_tag_background) // Red background for not available
//            }
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
