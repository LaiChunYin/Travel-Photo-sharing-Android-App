package com.example.travel_photo_sharing_app.screens;

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_photo_sharing_app.MainActivity
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.adapters.FollowerFolloweeAdapter
import com.example.travel_photo_sharing_app.databinding.ActivityFollowerFolloweeBinding
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.repositories.UserRepository
import com.example.travel_photo_sharing_app.utils.AuthenticationHelper
import kotlinx.coroutines.launch

class FollowerFolloweeActivity : MainActivity() {
    private lateinit var binding: ActivityFollowerFolloweeBinding
    private var followers: MutableList<User> = mutableListOf()
    private var followees: MutableList<User> = mutableListOf()
    private var loggedInUser: User? = null
    private lateinit var followerAdapter: FollowerFolloweeAdapter
    private lateinit var followeeAdapter: FollowerFolloweeAdapter
    override val tag = "Follower Followee"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowerFolloweeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Follower/Followee List"

        AuthenticationHelper.instance!!.loggedInUser.observe(this) {user ->
            loggedInUser = user
        }
        Log.d(tag, "in follower/followee, loggedin users is $loggedInUser")


        followerAdapter = FollowerFolloweeAdapter(followers, null)
        this.binding.followerRv.adapter = followerAdapter
        this.binding.followerRv.layoutManager = LinearLayoutManager(this)
        this.binding.followerRv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        followeeAdapter = FollowerFolloweeAdapter(followees, {followeeEmail -> unfollowBtnHandler(followeeEmail)})
        this.binding.followeeRv.adapter = followeeAdapter
        this.binding.followeeRv.layoutManager = LinearLayoutManager(this)
        this.binding.followeeRv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )


        binding.followeeBtn.setOnClickListener{
            binding.followeeBtn.setBackgroundColor(getColor(R.color.light_orange))
            binding.followerBtn.setBackgroundColor(getColor(R.color.light_grey))
            binding.followeeRv.visibility = View.VISIBLE
            binding.followerRv.visibility = View.GONE
        }

        binding.followerBtn.setOnClickListener{
            binding.followeeBtn.setBackgroundColor(getColor(R.color.light_grey))
            binding.followerBtn.setBackgroundColor(getColor(R.color.light_orange))
            binding.followeeRv.visibility = View.GONE
            binding.followerRv.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            Log.d(tag, "getting follwers/followees")
            UserRepository.getAllFollowees(loggedInUser!!.email)
            UserRepository.getAllFollowers(loggedInUser!!.email)
        }
        UserRepository.allFollowers.observe(this){users ->
            followers.clear()
            followers.addAll(users)
            Log.d(tag, "followers displayed are ${followers}")
            followerAdapter.notifyDataSetChanged()
        }

        UserRepository.allFollowees.observe(this) {users ->
            followees.clear()
            followees.addAll(users)
            Log.d(tag, "followees displayed are ${followees}")
            followeeAdapter.notifyDataSetChanged()
        }
    }

    private fun unfollowBtnHandler(followeeEmail: String) {
        Log.d(tag, "unfollow btn clicked ${loggedInUser}, ${followeeEmail}")
        UserRepository.unfollow(loggedInUser!!.email, followeeEmail)
    }

}
