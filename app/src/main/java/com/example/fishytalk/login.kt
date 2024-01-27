package com.example.fishytalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var emaid : EditText
    private lateinit var pasid : EditText
    private lateinit var login_but : AppCompatButton
    private lateinit var signup_but : AppCompatButton

    private lateinit var mauth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emaid = findViewById(R.id.email_id)
        pasid = findViewById(R.id.password_id)
        login_but = findViewById(R.id.login_but)
        signup_but = findViewById(R.id.signup_but)

        mauth = FirebaseAuth.getInstance()    //gets the firebase features to login a account

        signup_but.setOnClickListener {

            finish()

            val intent = Intent(this,signup::class.java)   // redirects to different page (signup page)
            startActivity(intent)

        }

        login_but.setOnClickListener {
            val email = emaid.text.toString()
            val password = pasid.text.toString()

            if( TextUtils.isEmpty(email) ){
                Toast.makeText(this@login , "Please, Enter your email_ID ",Toast.LENGTH_SHORT).show()
            }
            else if( TextUtils.isEmpty(password) ){
                Toast.makeText(this@login , "Please, Enter your password ",Toast.LENGTH_SHORT).show()
            }
            else {
                Login(email, password)
            }
        }

    }

    private fun Login(email:String , password:String){

        //logic for logging in a user (copied from google (Get Started with Firebase Authentication on Android - https://firebase.google.com/docs/auth/android/start) )

        mauth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this@login,
                        "Login Successful.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    finish()

                    val intent = Intent(this@login , homepage::class.java )
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        this@login,
                        "Login failed. User does not exist",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }
}