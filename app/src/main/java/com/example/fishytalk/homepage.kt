package com.example.fishytalk

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.media3.effect.Crop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.io.File

class homepage : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<user>
    private lateinit var adapter: userAdapter
    private lateinit var mauth: FirebaseAuth
    private lateinit var mDBref: DatabaseReference
    private lateinit var prf_img: de.hdodenhof.circleimageview.CircleImageView       //for menu
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var profilePicRef: StorageReference
    private var storageRef = Firebase.storage

    var imageUri : Uri? = null ;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        mauth = FirebaseAuth.getInstance()
        mDBref = FirebaseDatabase.getInstance().getReference()

        prf_img = findViewById(R.id.pro_img)       //for menu

       get_profile_image() // this bring prf_img from Firebase Database ,if exists

        prf_img.setOnClickListener {

            val popup = PopupMenu(this, prf_img)     //its a popup menu
            popup.inflate(R.menu.three_dot_menu)
            popup.show()

            popup.setOnMenuItemClickListener {

                if (it.itemId == R.id.logout_menu) {         //logic for logging out from homepage.
                    // write the logic for logout
                    mauth.signOut()

                    finish()

                    val intent = Intent(this@homepage, login::class.java)
                    startActivity(intent)
                }
                if (it.itemId == R.id.setprf_menu) {           //logic for setting profile image

                    //picks the image from gallery
                    pick_img_upload()
                }


                true


            }
        }


        userList = ArrayList()
        adapter = userAdapter(this, userList)        //creating instance of arraylist and userAdapter

        userRecyclerView = findViewById(R.id.user_recycler_view)

        userRecyclerView.layoutManager = LinearLayoutManager(this)     //to keep recycler_view as a LinearLayout
        userRecyclerView.adapter = adapter                                    //updating recycler_view adapter

        mDBref.child("user").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()                                   //clearing the previous loaded userlist

                for (ps in snapshot.children) {
                    val currUser = ps.getValue(user::class.java)

                    if (mauth.currentUser!!.uid != currUser!!.uid) {
                        userList.add(currUser!!)                           //adds the details of other users in 'userList' array
                    }
                }

                adapter.notifyDataSetChanged()                    // notifies the changes to adapter (why?)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun get_profile_image() {

        val mDBref2 = FirebaseDatabase.getInstance().getReference("user_image").child(mauth.currentUser!!.uid)

        mDBref2.get().addOnSuccessListener {

            prf_img = findViewById(R.id.pro_img)

            val img_url = it.child("url").value
            Glide.with(this)
                .load(img_url)
                .placeholder(R.drawable.person_icon)
                .error(R.drawable.person_icon)
                .into(prf_img)

            Toast.makeText(this,"Profile image found",Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {
            Toast.makeText(this,"Profile image not found",Toast.LENGTH_SHORT).show()
        }

    }

    private fun pick_img_upload() {

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data            //image URI (path of image)
            prf_img.setImageURI(imageUri)   //for now ,made it visible on prf_img
            uploadToFirebase(imageUri);     //uploading the image in Firebase Storage and saving its link in Firebase Database
        }
    }

    private fun uploadToFirebase(uri: Uri?) {

        if (uri != null) {

            storageRef = FirebaseStorage.getInstance()

            storageRef.getReference("images").child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl //this gets the link of uploaded image from 'Firebase Storage'
                        .addOnSuccessListener {

                            val mapimage = mapOf(
                                "url" to it.toString()               //maps the url link to string
                            )

                            val dbref = FirebaseDatabase.getInstance().getReference("user_image")  //adds the img link to 'Firebase database'
                            dbref.child(mauth.currentUser!!.uid).setValue(mapimage)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "image uploaded to DB and Storage",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener{

                                    Toast.makeText(
                                        this,
                                        "image did not uploaded to DB and Storage",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                        }.addOnFailureListener{

                            Toast.makeText(
                                this,
                                "image did not put in storage ",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                }.addOnFailureListener{

                    Toast.makeText(
                        this,
                        "image uri haven't fetched ",
                        Toast.LENGTH_SHORT
                    ).show()

                }

        }
    }


}

