package com.example.travel_photo_sharing_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.models.Post


class MyPostAdapter(
    private val postList:MutableList<Post>,
    private val rowClickHandler: (Int) -> Unit,
    private val deleteBtnClickHandler: (Int) -> Unit,
    private val editBtnClickHandler: (Int) -> Unit) : RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>() {

    inner class MyPostViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        init {
            itemView.setOnClickListener {
                rowClickHandler(adapterPosition)
            }
            itemView.findViewById<Button>(R.id.btnEdit).setOnClickListener {
                editBtnClickHandler(adapterPosition)
            }
            itemView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                deleteBtnClickHandler(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_my_post, parent, false)
        return MyPostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {

        val currPost: Post = postList.get(position)

        val tvTitle = holder.itemView.findViewById<TextView>(R.id.post_address)
        tvTitle.text = currPost.address

        val tvDetail = holder.itemView.findViewById<TextView>(R.id.post_type)
        tvDetail.text = currPost.type

    }
}
