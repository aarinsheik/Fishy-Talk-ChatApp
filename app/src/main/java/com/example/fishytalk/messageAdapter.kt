package com.example.fishytalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class messageAdapter(val context : Context , val messageList : ArrayList<message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val sent_item = 1       //to determine the sender and receiver
    val recieve_item = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){      //inflating the required recycler_view
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_layout_sender , parent , false)
            return sentViewHolder(view)
        }
        else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_layout_reciever , parent , false)
            return recieverViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val curr_msg = messageList[position]

        if(holder.javaClass == sentViewHolder::class.java){

            val viewholder = holder as sentViewHolder     //passing the message from sender(editText) to layout(recycler_view)
            holder.sender_msg.text = curr_msg.msg
        }
        else{
            val viewholder = holder as recieverViewHolder
            holder.reciever_msg.text = curr_msg.msg
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {  // this function identifies whether the message is from sender or receiver

        val curr_msg = messageList[position]

        if( FirebaseAuth.getInstance().currentUser?.uid.equals(curr_msg.senderId) ){  //if message is from sender : use sender_recycle_layout
            return sent_item
            }
        else{
            return recieve_item
        }

    }

    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sender_msg = itemView.findViewById<TextView>(R.id.sender_msag)
    }
    class recieverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val reciever_msg = itemView.findViewById<TextView>(R.id.reciever_msag)
    }

}