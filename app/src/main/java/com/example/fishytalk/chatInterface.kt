package com.example.fishytalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class chatInterface : AppCompatActivity() {

    private lateinit var chat_recycler_view : RecyclerView
    private lateinit var msgBox : EditText
    private lateinit var send_bt : ImageButton
    private lateinit var receiver_prf_name : TextView   //profile name
    private lateinit var receiver_prf_image : ImageView   //profile image
    private lateinit var back_arrow : ImageButton      //back arrow

    private lateinit var msg_list : ArrayList<message>
    private lateinit var msg_adapter : messageAdapter

    private lateinit var mDBref : DatabaseReference    //initializing database

    var recieverRoom : String? = null
    var senderRoom : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatinterface)

        chat_recycler_view = findViewById(R.id.chat_recycler_view)
        msgBox = findViewById(R.id.msg_Box)
        send_bt = findViewById(R.id.send_but)
        receiver_prf_name = findViewById(R.id.profile_name)
        receiver_prf_image = findViewById(R.id.pro_img)
        back_arrow = findViewById(R.id.back_arrow)

        msg_list = ArrayList()
        msg_adapter = messageAdapter(this,msg_list)

        chat_recycler_view.layoutManager = LinearLayoutManager(this)
        chat_recycler_view.adapter = msg_adapter

        mDBref = FirebaseDatabase.getInstance().getReference()

        val rec_name = intent.getStringExtra("receiver_name")    // getting the name and uid from userAdapter (Homepage)
        val rec_uid = intent.getStringExtra("receiver_uid")
        val send_uid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = rec_uid + send_uid         //creating a unique ID for private sender and receiver ChatRoom
        recieverRoom = send_uid + rec_uid

        receiver_prf_name.text = rec_name

        // logic for adding messages in recycler_view

        mDBref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener( object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    msg_list.clear()                                          //when new message arrives , it should be refreshed

                    for( ps in snapshot.children){

                        var msges = ps.getValue(message::class.java)
                        msg_list.add(msges!!)

                    }

                    msg_adapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        back_arrow.setOnClickListener {                                              //logic for back arrow
            val intent = Intent(this@chatInterface , homepage::class.java )
            startActivity(intent)
        }

        send_bt.setOnClickListener {     //logic for adding the message to database

            val msg = msgBox.text.toString()
            val message_obj = message(msg , send_uid )     //creating object of sender message and sender ID

            mDBref.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(message_obj).addOnSuccessListener {
                    mDBref.child("chats").child(recieverRoom!!).child("messages").push()
                        .setValue(message_obj)
                }                                                          //adding message_object to database

            msgBox.setText("")
        }


    }
}