package com.example.travel_photo_sharing_app.screens

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.travel_photo_sharing_app.databinding.ActivityAddPostBinding
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.repositories.PostRepository
import com.example.travel_photo_sharing_app.utils.AuthenticationHelper
import com.example.travel_photo_sharing_app.utils.CameraImageHelper
import com.example.travel_photo_sharing_app.utils.CameraImageHelper.Companion.base64ToBitmap
import com.example.travel_photo_sharing_app.utils.CameraImageHelper.Companion.bitmapToBase64
import com.example.travel_photo_sharing_app.utils.LocationHelper
import com.example.travel_photo_sharing_app.utils.checkDuplicatedPost
import com.example.travel_photo_sharing_app.utils.getCategorySpinnerList
import com.example.travel_photo_sharing_app.utils.saveDataToSharedPref
import com.example.travel_photo_sharing_app.utils.sharedPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream


class AddPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPostBinding
//    lateinit var sharedPreferences: SharedPreferences
//    lateinit var prefEditor: SharedPreferences.Editor
//    private var loggedInUserName: String = ""
    private var loggedInUser: User? = null
    private var postRepository = PostRepository()
    private lateinit var locationHelper: LocationHelper

    val tag = "Add Post"
    private val TAKE_PHOTO = 1
    private var base64UploadedImg: String? = null

    var savedPosts: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationHelper = LocationHelper(applicationContext, this)

        binding.longitude.visibility = View.GONE
        binding.latitude.visibility = View.GONE
        binding.imageTaken.visibility = View.GONE

//        loggedInUserName = this.intent.getStringExtra("USER") ?: ""
        //        loggedInUser = AuthenticationHelper.instance!!.loggedInUser
        AuthenticationHelper.instance!!.loggedInUser.observe(this) {user ->
            loggedInUser = user
        }
        Log.i(tag, "In AddPost, user: ${loggedInUser}")

        binding.addressCoordinatesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // If the switch is ON, make the address field invisible
                binding.address.visibility = View.GONE
                binding.longitude.visibility = View.VISIBLE
                binding.latitude.visibility = View.VISIBLE
            } else {
                // If the switch is OFF, make the address field visible
                binding.address.visibility = View.VISIBLE
                binding.longitude.visibility = View.GONE
                binding.latitude.visibility = View.GONE
            }
        }

        val selectedPost = intent.getSerializableExtra("POST_DATA") as Post?
        if (selectedPost != null) {
            binding.address.setText(selectedPost.address)
            binding.categorySpinner.setSelection(getCategorySpinnerList(this).indexOf(selectedPost.type))
//            binding.type.setText(selectedPost.type)
//            binding.authorName.setText(selectedPost.author.username)
//            binding.authorEmail.setText(selectedPost.author.email)
//            binding.authorEmail.setText(selectedPost.authorEmail)

//            binding.authorPhone.setText(selectedPost.author.phone)
            binding.description.setText(selectedPost.description)
            binding.visibleToGuest.isChecked = selectedPost.visibleToGuest

            if(selectedPost.imageUrl != null){
                binding.imageTaken.setImageBitmap(CameraImageHelper.base64ToBitmap(selectedPost.imageUrl!!))
                binding.imageTaken.visibility = View.VISIBLE
            }
        }

//        this.sharedPreferences = getSharedPreferences("POSTS", MODE_PRIVATE)
//        this.prefEditor = this.sharedPreferences.edit()
//
//        var resultsFromSP = sharedPreferences.getString(loggedInUser!!.username, "")
//        if (resultsFromSP != "") {
//            val gson = Gson()
//            val typeToken = object : TypeToken<List<Post>>() {}.type
//            val tempPostList = gson.fromJson<List<Post>>(resultsFromSP, typeToken)
//
//            savedPosts = tempPostList.toMutableList()
//        }

        this.binding.btnUploadPhoto.setOnClickListener {
//            val intent = Intent(this, CameraActivity::class.java)
            if (hasPermissions() == false) {
                ActivityCompat.requestPermissions(this, CameraActivity.CAMERAX_PERMISSIONS, 0)
            }
            else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, TAKE_PHOTO)
            }
        }

        this.binding.saveBtn.setOnClickListener {
            this.saveData()
        }

        this.binding.cancelBtn.setOnClickListener {
            finish()
        }

        this.binding.currentLocationBtn.setOnClickListener {
//            val showCoordinates = binding.addressCoordinatesSwitch.isChecked

            locationHelper.getCurrentLocation()
            locationHelper.currentLocation.observe(this) { currLocation ->
                val latitude: Double = currLocation.latitude
                val longitude: Double = currLocation.longitude
                binding.latitude.setText(latitude.toString())
                binding.longitude.setText(longitude.toString())
                binding.address.setText(locationHelper.coordinatesToAddress(latitude, longitude))
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            if (requestCode == TAKE_PHOTO) {

                Log.d(tag, "return from camera ${data!!.extras!!["data"]}")
                val bitmap: Bitmap = data!!.extras!!["data"] as Bitmap

                binding.imageTaken.setImageBitmap(bitmap);
                binding.imageTaken.visibility = View.VISIBLE

                //upload
                val base64_img = CameraImageHelper.bitmapToBase64(bitmap)
                Log.d(tag, "encoded image is ${base64_img}")
                base64UploadedImg = base64_img
            }
        }

    }
    private fun saveData() {
        var address: String? = this.binding.address.text.toString()
        var latitude: Double? = this.binding.longitude.text.toString().toDouble()
        var longitude: Double? = this.binding.latitude.text.toString().toDouble()
        var type: String? = this.binding.categorySpinner.selectedItem.toString()
//        var type: String? = this.binding.type.text.toString()
//        var city: String? = this.binding.city.text.toString()
//        var postalCode: String? = this.binding.postalCode.text.toString()
//        var authorName: String? = this.binding.authorName.text.toString()
        var authorEmail: String? = loggedInUser!!.email
//        var authorPhone: String? = this.binding.authorPhone.text.toString()
        var desc: String? = this.binding.description.text.toString()
        var visibleToGuest: Boolean? = this.binding.visibleToGuest.isChecked
        var base64Img: String? = CameraImageHelper.bitmapToBase64(this.binding.imageTaken.drawable.toBitmap())
        Log.d(tag, "img in savedata, ${base64Img}")
        Log.d(tag, "spinner value is ${type}")
        // error check
        var hasEmptyField = false
        if (address.isNullOrEmpty()) {
            this.binding.address.setError("Address cannot be empty")
            hasEmptyField = true
        }
        if (latitude == null || latitude.isNaN()) {
            this.binding.latitude.setError("Latitude cannot be empty")
            hasEmptyField = true
        }
        if (longitude == null || longitude.isNaN()) {
            this.binding.longitude.setError("Longitude cannot be empty")
            hasEmptyField = true
        }
//        if (type.isNullOrEmpty()) {
//            this.binding.type.setError("Type cannot be empty")
//            hasEmptyField = true
//        }
//        if (city.isNullOrEmpty()) {
//            this.binding.city.setError("City cannot be empty")
//            hasEmptyField = true
//        }
//        if (postalCode.isNullOrEmpty()) {
//            this.binding.postalCode.setError("Postal code cannot be empty")
//            hasEmptyField = true
//        }
//        if (authorName.isNullOrEmpty()) {
//            this.binding.authorName.setError("Author name cannot be empty")
//            hasEmptyField = true
//        }
//        if (authorPhone.isNullOrEmpty()) {
//            this.binding.authorPhone.setError("Author phone cannot be empty")
//            hasEmptyField = true
//        }
//        if (authorEmail.isNullOrEmpty()) {
//            this.binding.authorEmail.setError("Author email cannot be empty")
//            hasEmptyField = true
//        }
        if (desc.isNullOrEmpty()) {
            this.binding.description.setError("Description cannot be empty")
            hasEmptyField = true
        }
        if(hasEmptyField){
            Snackbar.make(binding.addPostParentLayout, "All fields are required.", Snackbar.LENGTH_LONG).show()
            return
        }

//        val author = User(authorName!!, authorEmail!!, authorPhone!!)
//        val author = User(authorName!!, authorEmail!!, "placeholder", mutableListOf(), "placeholder", authorPhone!!)
//        val author = User(authorEmail!!, )

        val selectedPost = intent.getSerializableExtra("POST_DATA") as Post?
        val index = intent.getIntExtra("INDEX", -1)
        Log.i(tag, "selected post ${index}: ${selectedPost}")

        // update post
        if(selectedPost != null){
//            val postToEdit = Post(
//                address!!,
//                type!!,
////                author,
//                authorEmail!!,
//                desc!!,
////                bedrooms!!,
////                kitchens!!,
////                bathrooms!!,
//                visibleToGuest!!,
//                latitude!!,
//                longitude!!,
//            )
            val postId: String = this.binding.postId.toString()
            Log.d(tag, "postId is ${postId}")
            val postToEdit = selectedPost
            postToEdit.address = address!!
            postToEdit.type = type!!
            postToEdit.description = desc!!
            postToEdit.visibleToGuest = visibleToGuest!!
            postToEdit.latitude = latitude!!
            postToEdit.longitude = longitude!!
            postToEdit.imageUrl = base64Img!!

//            savedPosts[index] = postToEdit
            postRepository.updatePost(postId, postToEdit)
        }
        // create new post
        else {
//            var postToAdd = Post(address!!, city!!, postalCode!!, type!!, author, desc!!, bedrooms!!, kitchens!!, bathrooms!!, availableForRent!!)
//            var postToAdd = Post(address!!, type!!, author, desc!!, visibleToGuest!!)
            var postToAdd = Post(address!!, type!!, authorEmail!!, desc!!, visibleToGuest!!, latitude!!, longitude!!, base64Img!!)
//            if(checkDuplicatedPost(postToAdd, this)){
//                Snackbar.make(binding.addPostParentLayout, "Post already exist!!", Snackbar.LENGTH_LONG).show()
//                return
//            }
//            savedPosts.add(postToAdd)
            postRepository.addPost(postToAdd)
        }
//        saveDataToSharedPref(this, "POSTS", loggedInUser!!.username, savedPosts, true)
        Snackbar.make(binding.addPostParentLayout, "Data Saved", Snackbar.LENGTH_LONG).show()
        finish()
    }

    private fun hasPermissions():Boolean {
        return CameraActivity.CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


}