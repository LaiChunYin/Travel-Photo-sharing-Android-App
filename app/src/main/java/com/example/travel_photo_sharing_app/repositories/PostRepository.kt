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


}