package com.example.travel_photo_sharing_app.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.utils.tag
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class PostRepository {
    private val tag = "Post Repo"
    private val db = Firebase.firestore

    private val COLLECTION_POSTS = "posts"
    private val FIELD_ADDRESS = "address"
    private val FIELD_AUTHOR_EMAIL = "authorEmail"
    private val FIELD_DESC = "description"
    private val FIELD_VISIBLE_TO_GUEST = "visibleToGuest"
    private val FIELD_IMAGE_URL = "imageUrl"
    private val FIELD_CREATED_AT = "createdAt"

    var publicPosts : MutableLiveData<List<Post>> = MutableLiveData<List<Post>>()

    suspend fun getPostById(postId: String): Post?{
        return try{
            val result = db.collection(COLLECTION_POSTS).document(postId).get().await()

            Log.d(tag, "getPostByEmail result ${result.data}")
            if(result.data != null){
                Post(result)
            }
            else{
                null
            }

        }catch (ex : Exception){
            Log.e(tag, "removeFromFav failed: $ex")
            null
        }
    }

    fun getAllPublicPosts(){
        try{
            db.collection(COLLECTION_POSTS)
                .whereEqualTo(FIELD_VISIBLE_TO_GUEST, true)
                .get()
                .addOnSuccessListener { result ->
                    val posts = mutableListOf<Post>()
                    Log.d(tag, "getAllPublicPosts result ${result.documents}")
                    for(post in result.documents){
                        Log.d(tag, "post is ${post.data},,, ${post}")
                        Log.d(tag, "post latitude ${post.data!!["latitude"]}, ${post.data!!["latitude"]!!::class.java}, longitude ${post.data!!["longitude"]}, ${post.data!!["longitude"]!!::class.java}")
                        posts.add(Post(post))
                    }
                    Log.d(tag, "getAllPublicPosts: $result")
                    publicPosts.postValue(posts)
                }
                .addOnFailureListener { ex ->
                    Log.e(tag, "Failed to getAllPublicPosts ${tag}: $ex")
                }

        }catch (ex : Exception){
            Log.e(tag, "removeFromFav failed: $ex")
        }
    }

    fun addPost(newPost: Post){
        try{
            val data : MutableMap<String, Any?> = newPost.toHashMap()

            db.collection(COLLECTION_POSTS)
            .document()
            .set(data)
            .addOnSuccessListener { docRef ->
                Log.d(tag, "Added ${docRef} to Posts")
            }
            .addOnFailureListener { ex ->
                Log.e(tag, "Failed to add ${newPost} to Fav: $ex")
            }

        }catch (ex : Exception){
            Log.e(tag, "addPost failed: $ex", )
        }
    }
    fun getPostsByType(type: String): MutableLiveData<List<Post>> {
        val postsByType: MutableLiveData<List<Post>> = MutableLiveData()

        db.collection(COLLECTION_POSTS)
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { documents ->
                val posts: MutableList<Post> = mutableListOf()
                for (document in documents) {
                    try {
                        document.data?.let {
                            val post = Post(document)
                            posts.add(post)
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error converting document to Post", e)
                    }
                }
                postsByType.value = posts
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Error getting documents by type: $type", exception)
            }

        return postsByType
    }

    fun getPostsByAddress(searchAddress: String): MutableLiveData<List<Post>> {
        val postsByAddress: MutableLiveData<List<Post>> = MutableLiveData()

        db.collection(COLLECTION_POSTS)
            .whereEqualTo("address", searchAddress)
            .get()
            .addOnSuccessListener { documents ->
                val posts: MutableList<Post> = mutableListOf()
                for (document in documents) {
                    try {
                        document.data?.let {
                            val post = Post(document)
                            posts.add(post)
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error converting document to Post", e)
                    }
                }
                postsByAddress.value = posts
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Error getting documents by address: $searchAddress", exception)
                postsByAddress.value = emptyList() // Set empty list in case of failure
            }

        return postsByAddress
    }




}