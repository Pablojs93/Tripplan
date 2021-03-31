package com.pjas.tripplan.Login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pjas.tripplan.App.CreateTrip.CreateTrip
import com.pjas.tripplan.App.MyTrips.MyTrips
import com.pjas.tripplan.R

class Login : AppCompatActivity() {

    //global variables
    private var email: String? = null
    private var password: String? = null
    //UI elements
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var bLogin: Button? = null
    private var bCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()
    }

    private fun initialise() {
        etEmail = findViewById<View>(R.id.et_EmailL) as EditText
        etPassword = findViewById<View>(R.id.et_PasswordL) as EditText
        bLogin = findViewById<View>(R.id.b_LoginL) as Button
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        bLogin!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = etEmail?.text.toString().toLowerCase().trim()
        password = etPassword?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar!!.setMessage("Logging User...")
            mProgressBar!!.show()
            Log.d("Login", "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if(FirebaseAuth.getInstance().currentUser.isEmailVerified){
                        if (task.isSuccessful) {
                            // Sign in success, update UI with signed-in user's information
                            Log.d("Login", "signInWithEmail:success")

                            val intent = Intent(this, MyTrips::class.java)
                            startActivity(intent)
                            //updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("Login", "signInWithEmail:failure", task.exception)
                            Toast.makeText(applicationContext, "Login failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        // If sign in fails, display a message to the user.
                        Log.e("Login", "signInWithEmail:failure", task.exception)
                        Toast.makeText(applicationContext, "You need to verify your email.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            val toast = Toast.makeText(applicationContext, "Email and password shouldn't be empty", Toast.LENGTH_LONG)
            toast.show()
        }
    }
}