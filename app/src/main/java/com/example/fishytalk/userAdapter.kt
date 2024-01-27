package com.example.fishytalk

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class userAdapter(val context: Context, val userList: ArrayList<user>) :
    RecyclerView.Adapter<userAdapter.userViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {  //this function converts the layout into view
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_layout_single_row, parent, false)
        return userViewHolder(view)
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val curr_user = userList[position]

        holder.prfname.text = curr_user.name        // user data is been updated in recycler_view
        holder.prfemail.text = curr_user.email


        holder.itemView.setOnClickListener {
            val intent =  Intent(context , chatInterface::class.java)

            intent.putExtra("receiver_name" , curr_user.name)
            intent.putExtra("receiver_uid" , curr_user.uid)

            context.startActivity(intent)  //as we are in class , we start new activity of 'context' which is sent.
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class userViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val prfname = itemView.findViewById<TextView>(R.id.profile_name)
        val prfemail = itemView.findViewById<TextView>(R.id.profile_email)
    }
}