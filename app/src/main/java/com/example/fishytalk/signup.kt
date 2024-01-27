package com.example.fishytalk

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.persistableBundleOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class signup : AppCompatActivity() {

    private lateinit var namid: EditText
    private lateinit var emaid: EditText
    private lateinit var pasid: EditText
    private lateinit var login_but: AppCompatButton
    private lateinit var signup_but: AppCompatButton

    private lateinit var mauth: FirebaseAuth
    private lateinit var mdbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        namid = findViewById(R.id.name_id)
        emaid = findViewById(R.id.email_id)
        pasid = findViewById(R.id.password_id)
        login_but = findViewById(R.id.login_but_sp)
        signup_but = findViewById(R.id.signup_but_sp)

        mauth = FirebaseAuth.getInstance()  //gets the firebase features to signup a new account

        login_but.setOnClickListener {

            finish()

            val intent = Intent(this, login::class.java)  //redirects to different page (login page)
            startActivity(intent)
        }

        signup_but.setOnClickListener {

            val email = emaid.text.toString()
            val password = pasid.text.toString()
            val name = namid.text.toString()

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this@signup, "Please, Enter your name ", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@signup, "Please, Enter your email ", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@signup, "Please, Enter your password ", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Signup(email, password, name)
            }
        }

    }

    private fun Signup(email: String, password: String, name: String) {

        //logic for creating user (copied from google (Get Started with Firebase Authentication on Android - https://firebase.google.com/docs/auth/android/start) )

        mauth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // code for jumping to homepage (MainActivity)

                    // adding user details to Database :
                    addUserToDatabase(name, email, mauth.currentUser?.uid!! )

                    Toast.makeText(
                        this@signup,
                        "Signup Successful.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    finish()

                    val intent = Intent(this@signup, homepage::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@signup,
                        "Signup failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {

        mdbref = FirebaseDatabase.getInstance().getReference() // To get the data from firebase Database

        mdbref.child("user").child(uid).setValue(
            user(
                name,
                email,
                uid
            )
        )  //this will add user to database (from firebase to 'user' class )

    }
}