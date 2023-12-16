package com.example.travel_photo_sharing_app.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.screens.LoginActivity
import com.example.travel_photo_sharing_app.screens.PostDetailActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapHelper(val context: Context, val mMap: GoogleMap) {
    private val tag = "MapHelper"
    private val markerPostMap = HashMap<Marker, Post>()
    fun markerClickedHandler(marker: Marker, loggedInUser: User?): Boolean {
        Log.d(tag, "marker clicked: ${marker}, ${markerPostMap}")
        // popup post details
        val post = markerPostMap[marker]
        Log.d(tag, "marketPostMap post: ${post}")
        if(loggedInUser != null){
            Log.i(tag, "${loggedInUser} logged in")
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra("POST", post!!.idFromDb)
            context.startActivity(intent)
        }
        else {
            Log.i(tag, "no one logged in")
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("REFERER", "MainActivity")
            context.startActivity(intent)
        }
        return false
    }

    private fun removeAllMarkersOnMap(){
        Log.d(tag, "removing markers")
        for((marker, Post) in markerPostMap){
            Log.d(tag, "removing marker ${marker}")
            marker.remove()
        }
    }
    fun addPostsToMap(posts: MutableList<Post>){
        removeAllMarkersOnMap()
        Log.d(tag, "adding posts to map")
        var cameraSet = false  // set the camera position to the first post by default

        for(post in posts){
            val location = LatLng(post.latitude, post.longitude)
            Log.d(tag, "in addPostsToMap ${post}, ${location}")

            if(!cameraSet){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                cameraSet = true
            }

            val markerOption = MarkerOptions().position(location)
            markerOption.title(post.address)
            val marker: Marker = mMap.addMarker(markerOption)!!
            markerPostMap[marker] = post
            Log.d(tag, "Marker set is ${markerOption}")
        }
    }
}