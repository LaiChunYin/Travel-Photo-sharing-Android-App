package com.example.travel_photo_sharing_app.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

class UserRepository {
    companion object {
        private val tag = "User Repo"
        private val db = Firebase.firestore

        private val COLLECTION_USERS = "Users"
        private val FIELD_EMAIL = "email"
        private val FIELD_PASSWORD = "password"
        private val FIELD_NAME = "username"
        private val FIELD_SAVED_POSTS = "savedPosts"
        private val FIELD_FOLLOWED_BY = "followedBy"
        private val FIELD_FOLLOWING = "following"
        private val FIELD_CREATED_POSTS = "createdPosts"
//        private val postRepository = PostRepository()

        var allFollowers: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
        var allFollowees: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
        var allMyPosts: MutableLiveData<List<Post>> = MutableLiveData<List<Post>>()

        fun addUserToDB(newUser: User) {
            try {
                val data: MutableMap<String, Any> = HashMap()

                data[FIELD_EMAIL] = newUser.email
                data[FIELD_PASSWORD] = newUser.password
                data[FIELD_NAME] = newUser.username
                data[FIELD_SAVED_POSTS] = mutableListOf<Post>()
                data[FIELD_FOLLOWED_BY] = mutableListOf<String>()
                data[FIELD_FOLLOWING] = mutableListOf<String>()
                data[FIELD_CREATED_POSTS] = mutableListOf<String>()

                db.collection(COLLECTION_USERS)
                    .document(newUser.email)
                    .set(data)
                    .addOnSuccessListener { docRef ->
                        Log.d(tag, "addUserToDB: User document successfully created with ID $docRef")
                    }
                    .addOnFailureListener { ex ->
                        Log.e(tag, "addUserToDB: Unable to create user document due to exception : $ex",)
                    }

            } catch (ex: Exception) {
                Log.e(tag, "addUserToDB: Couldn't add user document $ex",)
            }
        }

        suspend fun findUserByEmail(email: String): User? {
            return try {
                val result = db.collection(COLLECTION_USERS)
                    .document(email)
                    .get().await()

                Log.d(tag, "result is ${result.data}")
                Log.d(tag, "find user by email user created from result ${User(result.data!!)}")

                if (result.data != null) {
                    return User(result.data!!)
                } else {
                    return null
                }

            } catch (ex: Exception) {
                Log.e(tag, "findUserByEmail: Couldn't find user $email due to $ex")
                return null
            }
        }

        suspend fun userNameAlreadyUsed(name: String): Boolean {
            return try {
                val result = db.collection(COLLECTION_USERS)
                    .whereEqualTo(FIELD_NAME, name)
                    .get().await()

                Log.d(tag, "find user by name result is ${result.documents}")
//                Log.d(tag, "user created from result ${User(result.data!!)}")

                return result.documents.size > 0

            } catch (ex: Exception) {
                Log.e(tag, "findUserByName: Couldn't find user $name due to $ex")
                return false
            }
        }

        suspend fun findUsersByEmails(emails: MutableList<String>): MutableList<User> {
            val users = mutableListOf<User>()
            try {
                for (email in emails) {
                    val result = db.collection(COLLECTION_USERS)
                        .document(email)
                        .get().await()

                    Log.d(tag, "result is ${result.data}")
                    Log.d(tag, "user created from result ${User(result.data!!)}")

                    if (result.data != null) {
                        users.add(User(result.data!!))
                    }

                }
                return users

            } catch (ex: Exception) {
                Log.e(tag, "findUsersByEmails: Couldn't find user $emails due to $ex")
                return users
            }
        }


        fun follow(followerEmail: String, followeeEmail: String) {
            Log.d(tag, "in follow ${followerEmail}, ${followeeEmail}")
            try {
                db.collection(COLLECTION_USERS)
                    .document(followerEmail)
                    .update(FIELD_FOLLOWING, FieldValue.arrayUnion(followeeEmail))
                    .addOnSuccessListener {
                        Log.d(tag, "${followerEmail} followed ${followeeEmail}")
                    }
                    .addOnFailureListener {
                        Log.d(tag, "${followerEmail} failed to follow ${followeeEmail}")
                    }

                db.collection(COLLECTION_USERS)
                    .document(followeeEmail)
                    .update(FIELD_FOLLOWED_BY, FieldValue.arrayUnion(followerEmail))
                    .addOnSuccessListener {
                        Log.d(tag, "${followeeEmail} followed by ${followerEmail}")
                    }
                    .addOnFailureListener {
                        Log.d(tag, "${followeeEmail} failed to followed by ${followerEmail}")
                    }

            } catch (ex: Exception) {
                Log.e(tag, "follow: Couldn't follow $ex",)
            }
        }

        fun unfollow(followerEmail: String, followeeEmail: String) {
            Log.d(tag, "in unfollow ${followerEmail}, ${followeeEmail}")
            try {
                db.collection(COLLECTION_USERS)
                    .document(followerEmail)
                    .update(FIELD_FOLLOWING, FieldValue.arrayRemove(followeeEmail))
                    .addOnSuccessListener {
                        Log.d(tag, "${followerEmail} unfollowed ${followeeEmail}")
                    }
                    .addOnFailureListener {
                        Log.d(tag, "${followerEmail} failed to unfollow ${followeeEmail}")
                    }

                db.collection(COLLECTION_USERS)
                    .document(followeeEmail)
                    .update(FIELD_FOLLOWED_BY, FieldValue.arrayRemove(followerEmail))
                    .addOnSuccessListener {
                        Log.d(tag, "${followeeEmail} unfollowed by ${followerEmail}")
                    }
                    .addOnFailureListener {
                        Log.d(tag, "${followeeEmail} failed to unfollowed by ${followerEmail}")
                    }

            } catch (ex: Exception) {
                Log.e(tag, "unfollow: Couldn't unfollow $ex",)
            }
        }

        suspend fun getAllFollowers(userEmail: String) {
            Log.d(tag, "in getAllFollowers ${userEmail}")
            try {
                val followers = mutableListOf<User>()
                val followerEmails = findUserByEmail(userEmail)!!.followedBy
                for (email in followerEmails) {
                    followers.add(findUserByEmail(email)!!)
                }
                allFollowers.postValue(followers)

            } catch (ex: Exception) {
                Log.e(tag, "getAllFollowers: Couldn't get all followers $ex",)
            }
        }

        suspend fun getAllFollowees(userEmail: String) {
            Log.d(tag, "in getAllFollowees ${userEmail}")
            try {
                val followees = mutableListOf<User>()
                val followeeEmails = findUserByEmail(userEmail)!!.following
                for (email in followeeEmails) {
                    followees.add(findUserByEmail(email)!!)
                }
                allFollowees.postValue(followees)

            } catch (ex: Exception) {
                Log.e(tag, "getAllFollowees: Couldn't get all followees $ex",)
            }
        }

        fun savePost(userEmail: String, postId: String) {
            Log.d(tag, "in savePost ${userEmail}, ${postId}")
            try {
                db.collection(COLLECTION_USERS)
                    .document(userEmail)
                    .update("savedPosts", FieldValue.arrayUnion(postId))
                    .addOnSuccessListener {
                        Log.d(tag, "saved post id ${postId}")
                    }
                    .addOnFailureListener {
                        Log.d(tag, "failed to save post ${postId}")
                    }

            } catch (ex: Exception) {
                Log.e(tag, "savePost: Couldn't save post $ex",)
            }

        }

        fun unSavePost(userEmail: String, postId: String) {
            Log.d(tag, "in unSavePost ${userEmail}, ${postId}")
            try {
                db.collection(COLLECTION_USERS)
                    .document(userEmail)
                    .update("savedPosts", FieldValue.arrayRemove(postId))
                    .addOnSuccessListener {
                        Log.d(tag, "unSaved post id ${postId}")
                    }
                    .addOnFailureListener {
                        Log.d(tag, "failed to unSave post ${postId}")
                    }

            } catch (ex: Exception) {
                Log.e(tag, "unSavePost: Couldn't unSave post $ex",)
            }

        }

        suspend fun getAllMyPosts(postIds: MutableList<String>) {
            Log.d(tag, "in getAllMyPosts ${postIds}")
            try {
                val result = mutableListOf<Post>()
                for (id in postIds) {
                    result.add(PostRepository.getPostById(id)!!)
                }
                allMyPosts.postValue(result)
            } catch (ex: Exception) {
                Log.e(tag, "getAllMyPosts: Couldn't get all my posts $ex",)
            }

        }

        fun getUserSnapshotListener(
            email: String,
            userLiveData: MutableLiveData<User?>
        ): ListenerRegistration {
            return db.collection(COLLECTION_USERS)
                .document(email)
                .addSnapshotListener(EventListener { result, error ->
                    Log.d(tag, "in snapshot listener ${result!!.data}, ${result}")
                    if (error != null) {
                        Log.e(
                            tag,
                            "getUserSnapshotListener: Listening to user ${email} collection failed due to error : $error",
                        )
                        return@EventListener
                    }

                    if (result != null) {
                        userLiveData.postValue(User(result.data!!))

                    } else {
                        Log.d(tag, "getUserSnapshotListener: No data in the result after updating")
                    }
                })

        }

        fun addUserCreatedPosts(authorEmail: String, documentId: String) {
            db.collection(COLLECTION_USERS).document(authorEmail)
                .update(FIELD_CREATED_POSTS, FieldValue.arrayUnion(documentId))
                .addOnSuccessListener {
                    Log.d(tag, "created posts added ${authorEmail}")
                }
                .addOnFailureListener { ex ->
                    Log.d(tag, "failed to add to created posts: ${ex}")
                }
        }

        fun removeUserCreatePosts(postId: String, authorEmail: String) {
            db.collection(COLLECTION_USERS)
                .document(authorEmail)
                .update(FIELD_CREATED_POSTS, FieldValue.arrayRemove(postId))
                .addOnSuccessListener {
                    Log.d(tag, "deleted post id ${postId}")
                }
                .addOnFailureListener {
                    Log.d(tag, "failed to delete post ${postId}")
                }
        }
    }
}