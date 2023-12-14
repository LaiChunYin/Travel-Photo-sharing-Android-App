package com.example.travel_photo_sharing_app.screens

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.databinding.ActivityPostDetailBinding
import com.example.travel_photo_sharing_app.models.Post

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private lateinit var post: Post
    private val tag = "Post detail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.returnBtn.setOnClickListener {
            finish()
        }

        val hasPost: Boolean = this@PostDetailActivity.intent.extras?.containsKey("POST") != null
        if (hasPost) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                post = intent.getSerializableExtra("POST", Post::class.java)!!
            }else{
                post = intent.getSerializableExtra("POST") as Post
            }

            Log.i(tag, "has post $post")

            if (post != null) {
                binding.postAddress.setText(post.address)
                binding.postDescription.setText(post.description)
                binding.authorEmail.setText(post.authorEmail)

//                binding.numberOfBathrooms.setText(post.numOfBathrooms.toString())
//                binding.numberOfBedrooms.setText(post.numOfBedrooms.toString())
//                binding.numberOfKitchens.setText(post.numOfKitchens.toString())

//                Glide.with(binding.root.context).load(post.imageUrl).into(binding.postImage)  // for online images
                val imageName = post.imageUrl ?: "default_image"
                val res = resources.getIdentifier(imageName, "drawable", this.packageName)
                this.binding.postImage.setImageResource(res)
            }

            // Set the availability tag
            with(binding.postAvailabilityTag) {
                text = if (post.visibleToGuest) {
                    post.type.toString()// "Available" string resource
                } else {
                    getString(R.string.not_available) // "Not Available" string resource
                }

                // Set background color depending on availability
                setBackgroundResource(if (post.visibleToGuest) {
                    R.drawable.available_tag_background // Green background drawable
                } else {
                    R.drawable.not_available_tag_background // Red background drawable
                })

                // Ensure the tag is visible
                visibility = View.VISIBLE
            }

        }
    }
}